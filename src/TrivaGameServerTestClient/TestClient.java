package TrivaGameServerTestClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
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
	
	static UUID[] answers = new UUID[4];

	public static void main(String[] args) 
	{
		// build a raw TCP and UDP socket for testing
		NetworkInterface tcpSocket;
		NetworkInterface udpSocket;
		
		try 
		{
			tcpSocket = new TCP_Sock(userRegPort, false);
			udpSocket = new UDP_Sock(gamePort, true);
			
			System.out.println("");
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void printMsg(String addr, byte[] msg, boolean tcp) throws UnknownHostException
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
			break;
		case 0x01:
			System.out.println("BUZZER PRESSED");
			break;
		case 0x02:
			System.out.println("QUERY REQUEST RECEIVED");
			break;
		case 0x03:
			System.out.printf("User $s can answer\n", new UUID(buffer.getLong(), buffer.getLong()).toString());
			break;
		case 0x04:
			System.out.printf("User $s reveived $d points\n", new UUID(buffer.getLong(), buffer.getLong()).toString(), buffer.getInt());
			break;
		case 0x05:
			System.out.println("UNFREEZE");
			break;
		}
	}
	
}
