package TrivaGameServerTestClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

import TrivaGameServer.NetworkInterface;
import TrivaGameServer.TCP_Sock;
import TrivaGameServer.UDP_Sock;

public class TestClient
{
	static final int userRegPort = 3025;
	static final int gamePort = 3030;
	
	static InetAddress groupIp;
	static UUID userId;
	
	static String serverIp;
	
	static NetworkInterface tcpSocket;
	static NetworkInterface udpSocket;
	
	static UUID[] answers = new UUID[4];

	public static void main(String[] args)
	{
		boolean run = true;
		
		try 
		{
			// build a raw TCP and UDP socket for testing
			tcpSocket = new TCP_Sock(userRegPort, false);
			udpSocket = new UDP_Sock(gamePort, true);
			
			System.out.println("Enter server IP:");
			serverIp = System.console().readLine();
			System.out.println("Connecting to: " + serverIp);
			
			if(!tcpSocket.connect(serverIp))
			{
				System.out.println("Error connecting to: " + serverIp);
				tcpSocket.getLastException().printStackTrace();
				throw new IOException("Failed to connect to server");
			}
			
			System.out.println("Triva Server Test Client\n1-TCP\n2-UDP\n3-quit");
			
			while(run)
			{
				int command = -1;
				int selection = Integer.parseInt(System.console().readLine());
				switch(selection)
				{
				case 1:
					System.out.println("Select a TCP command to send");
					System.out.println("0-Check Server\n1-Register User\n2-Deregister User\n3-Ready\n4-Not Ready");
					command = Integer.parseInt(System.console().readLine());
					if(command > -1 && command < 5)
					{
						System.out.println("Sending message");
						tcpSocket.send(createMsg(command, true));
						
						if(command != 3 && command != 4)
							printMsg(serverIp, tcpSocket.receive(), true);
					}
					else
						System.out.println("Invalid command");
					break;
				case 2:
					System.out.println("Select a UDP command to send");
					System.out.println("1-Buzzer");
					command = Integer.parseInt(System.console().readLine());
					if(command != 1)
					{
						System.out.println("Sending message");
						udpSocket.send(createMsg(command, false), serverIp);
					}
					else
						System.out.println("Invalid command");
					break;
				case 3:
					System.out.println("Closing Client");
					run = false;
					break;
				default:
					System.out.println("Invalid command");
					break;
				}
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static byte[] createMsg(int opcode, boolean tcp)
	{
		byte[] msg = null;
		
		if(tcp)
			msg = createTCPMsg(opcode);
		else
			msg = createUDPMsg(opcode);
		
		return msg;
	}
	
	private static byte[] createTCPMsg(int opcode)
	{
		ByteBuffer buffer = ByteBuffer.wrap(new byte[1]);
		
		switch(opcode)
		{
		case 0:
			buffer = ByteBuffer.wrap(new byte[1]);
			buffer.put((byte) 0x00);
			break;
		case 1:
			String userName = "Test User Name";
			buffer = ByteBuffer.wrap(new byte[userName.length()+1]);
			buffer.put((byte) 0x01);
			buffer.put(userName.getBytes(StandardCharsets.UTF_8));
			break;
		case 2:
		case 3:
		case 4:
			buffer = ByteBuffer.wrap(new byte[17]);
			buffer.put((byte) opcode);
			buffer.putLong(userId.getMostSignificantBits());
			buffer.putLong(userId.getLeastSignificantBits());
			break;
		}
		
		return buffer.array();
	}
	
	private static byte[] createUDPMsg(int opcode)
	{
		ByteBuffer buffer = ByteBuffer.wrap(new byte[1]);
		
		switch(opcode)
		{
		case 0:
		case 1:
		case 4:
		case 5:
			buffer = ByteBuffer.wrap(new byte[21]);
			buffer.put((byte) opcode);
			buffer.put(groupIp.getAddress());
			buffer.putLong(userId.getMostSignificantBits());
			buffer.putLong(userId.getLeastSignificantBits());
			break;
		case 2:
			buffer = ByteBuffer.wrap(new byte[25]);
			buffer.put((byte) 0x02);
			buffer.put(groupIp.getAddress());
			buffer.putLong(userId.getMostSignificantBits());
			buffer.putLong(userId.getLeastSignificantBits());
			buffer.putInt(999999);
			break;
		case 3:
			buffer = ByteBuffer.wrap(new byte[41]);
			buffer.put((byte) 0x02);
			buffer.put(groupIp.getAddress());
			buffer.putLong(userId.getMostSignificantBits());
			buffer.putLong(userId.getLeastSignificantBits());
			buffer.putLong(answers[0].getMostSignificantBits());
			buffer.putLong(answers[0].getLeastSignificantBits());
			buffer.putInt(999999);
			break;
		}
		
		return buffer.array();
	}
	
	public static void printMsg(String addr, byte[] msg, boolean tcp) throws UnknownHostException
	{
		System.out.println("Received packet form: " + addr);
		System.out.printf("Opcode: %d", msg[0]);
		System.out.printf("Message size: %d", msg.length);
		parseMsg(msg.clone(), tcp);
		System.out.println("RAW MESSAGE:");
		System.out.print("0x");
		for(byte b : msg)
			System.out.printf("%02X", b);
		System.out.println(" END");
	}
	
	private static void parseMsg(byte[] msg, boolean tcp) throws UnknownHostException
	{
		if(tcp)
			parseTCPMsg(msg);
		else
			parseUDPMsg(msg);
	}
	
	private static void parseTCPMsg(byte[] msg) throws UnknownHostException
	{
		switch(msg[0])
		{
		case 0x00:
			if(msg[1] == 0x01)
				System.out.println("Server: OPEN");
			else
				System.out.println("Server: CLOSED");
			break;
		case 0x01:
			if(msg[1] == 0x00)
				System.out.printf("[ERR]: %s", new String(Arrays.copyOfRange(msg, 2, msg.length)));
			else
			{
				ByteBuffer buffer = ByteBuffer.wrap(Arrays.copyOfRange(msg, 1, msg.length));
				userId = new UUID(buffer.getLong(), buffer.getLong());
				groupIp = InetAddress.getByAddress(ByteBuffer.allocate(4).putInt(buffer.getInt()).array());
				
				// connect to the group and start receiving
				udpSocket.connect(groupIp.getHostAddress());
				udpSocket.startReceive(new UDPHandler());
				
				System.out.printf("User ID: %s\nGroup IP: %s\nPort: %d\n", userId.toString(), groupIp.getHostAddress(), buffer.getInt());
			}
			break;
		case 0x02:
			if(msg[1] == 0x00)
				System.out.printf("[ERR]: %s", new String(Arrays.copyOfRange(msg, 2, msg.length)));
			else
				System.out.println("Successfully deregestered");
		default:
			System.out.println("[ERR] Inavlid msg");
		}
	}
	
	private static void parseUDPMsg(byte[] msg)
	{
		ByteBuffer buffer = ByteBuffer.wrap(Arrays.copyOfRange(msg, 1, msg.length));
		switch(msg[0])
		{
		case 0x00:
			System.out.printf("Quesion ID: %d\n", buffer.getInt());
			
			for(int index = 0; index < 4; index++)
				answers[index] = new UUID(buffer.getLong(), buffer.getLong());
			
			String pAnswers = new String(Arrays.copyOfRange(msg, 69, msg.length));
			
			int index = 0;
			for(String ans : pAnswers.split(","))
				System.out.printf("%s\t\t%s", ans, answers[index++].toString());
			
			udpSocket.send(createMsg(0, false), serverIp);
			break;
		case 0x01:
			System.out.println("BUZZER PRESSED");
			break;
		case 0x02:
			System.out.println("QUERY REQUEST RECEIVED");
			udpSocket.send(createMsg(2, false), serverIp);
			break;
		case 0x03:
			System.out.printf("User $s can answer\n", new UUID(buffer.getLong(), buffer.getLong()).toString());
			udpSocket.send(createMsg(3, false), serverIp);
			break;
		case 0x04:
			System.out.printf("User $s reveived $d points\n", new UUID(buffer.getLong(), buffer.getLong()).toString(), buffer.getInt());
			udpSocket.send(createMsg(4, false), serverIp);
			break;
		case 0x05:
			System.out.println("UNFREEZE");
			udpSocket.send(createMsg(5, false), serverIp);
			break;
		}
	}	
}
