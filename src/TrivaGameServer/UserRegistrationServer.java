package TrivaGameServer;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

public class UserRegistrationServer implements HandableObject 
{
	private volatile ActivePlayerList playerList;
	private NetworkInterface socket;
	private static UserRegistrationServer instance;
	
	private UserRegistrationServer(NetworkInterface socket, ActivePlayerList playerList)
	{
		this.socket = socket;
		this.playerList = playerList;
	}
	
	public static synchronized UserRegistrationServer createInstance(NetworkInterface socket,ActivePlayerList playerList)
	{
		if(instance == null)
			instance = new UserRegistrationServer(socket, playerList);
		
		return instance;
	}
	
	public static UserRegistrationServer getInstance()
	{
		return instance;
	}
	
	public boolean startServer()
	{
		return socket.startReceive(this);
	}
	
	public boolean stopServer()
	{
		return socket.stopReceive();
	}

	@Override
	public void handler(InetAddress addr, byte[] msg) 
	{
		switch(msg[0])
		{
		// check if the game is still open to registration
		case 0x00:
			if(playerList.isRegistrationOpen())
				socket.send(createPacket((byte)0x00, new byte[] {0x01}));
			else
				socket.send(createPacket((byte)0x00, new byte[] {0x00}));
			break;
		// Register with the game
		case 0x01:
			if(playerList.isRegistrationOpen())
			{
				UUID id = addNewUser(Arrays.copyOfRange(msg, 1, msg.length), addr);
				if(id != null)
					socket.send(createRegistaredMessage((byte)0x01, id, playerList.getGroupAddress(), playerList.getGamePort()));
				else
					socket.send(createStringMessage((byte)0x01, "Unable to register"));
			}
			else
				socket.send(createStringMessage((byte)0x01, "Registration Closed"));
			break;
		// Deregister with the game
		case 0x02:
			if(playerList.removePlayer(getPlayerId(Arrays.copyOfRange(msg, 1, msg.length))))
				socket.send(createPacket((byte)0x02, new byte[] {0x01}));
			else
				socket.send(createStringMessage((byte)0x02, "Could not remove user"));
			break;
		case 0x03:
			playerList.playerReady(getPlayerId(Arrays.copyOfRange(msg, 1, msg.length)), true);
			break;
		case 0x04:
			playerList.playerReady(getPlayerId(Arrays.copyOfRange(msg, 1, msg.length)), false);
			break;
		}
	}
	
	private UUID getPlayerId(byte[] id)
	{
		ByteBuffer buffer = ByteBuffer.wrap(id);
		
		return new UUID(buffer.getLong(), buffer.getLong());
	}
	
	private UUID addNewUser(byte[] rawUserName, InetAddress addr)
	{
		String userName = new String(rawUserName, StandardCharsets.UTF_8);
		
		return playerList.addPlayer(new Player(userName, addr));
	}
	
	private byte[] createRegistaredMessage(byte opCode, UUID id, InetAddress addr, int port)
	{
		// convert the UUID
		ByteBuffer buffer = ByteBuffer.wrap(new byte[21+addr.getAddress().length]);
		
		buffer.put(opCode);
		buffer.putLong(id.getMostSignificantBits());
		buffer.putLong(id.getLeastSignificantBits());
		buffer.put(addr.getAddress());
		buffer.putInt(port);
		
		return buffer.array();
	}
	
	private byte[] createStringMessage(byte opCode, String msg)
	{
		return createPacket(opCode, msg.getBytes(StandardCharsets.UTF_8));
	}
	
	private byte[] createPacket(byte opCode, byte msg[])
	{
		int index = 1;
		byte[] response = new byte[msg.length+1];
		response[0] = opCode;
		
		// copy the message
		for(byte b : msg)
			response[index++] = b;
		
		return response;
	}
}
