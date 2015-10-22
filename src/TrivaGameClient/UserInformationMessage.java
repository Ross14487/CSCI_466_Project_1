package TrivaGameClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class UserInformationMessage implements Message 
{
	private byte[] rawMsg;
	private UUID playerID;
	private InetAddress groupIp;
	private int portNum;
	
	public UserInformationMessage(byte[] rawMsg) throws UnknownHostException
	{
		byte[] ipAddr = new byte[4];
		ByteBuffer buffer = ByteBuffer.wrap(Arrays.copyOfRange(rawMsg, 1, rawMsg.length));
		this.rawMsg = rawMsg;
		
		this.playerID = new UUID(buffer.getLong(), buffer.getLong());
		for(int x=0; x < 4; x++)
			ipAddr[x] = buffer.get();
		
		groupIp = InetAddress.getByAddress(ipAddr);
		
		portNum = buffer.getInt();
	}
	
	public UserInformationMessage(int opcode, UUID playerID, InetAddress groupIp, int portNum)
	{
		ByteBuffer buffer = ByteBuffer.wrap(new byte[25]);
		
		this.playerID = playerID;
		this.groupIp = groupIp;
		this.portNum = portNum;
		
		buffer.put((byte) opcode);
		buffer.putLong(playerID.getMostSignificantBits());
		buffer.putLong(playerID.getLeastSignificantBits());
		buffer.put(groupIp.getAddress());
		buffer.putInt(portNum);
		
		this.rawMsg = buffer.array();
	}
	
	public UUID getPlayerId()
	{
		return playerID;
	}
	
	public InetAddress getGroupIp()
	{
		return groupIp;
	}
	
	public int getPortNumber()
	{
		return portNum;
	}

	@Override
	public byte[] getMessage() 
	{
		return rawMsg;
	}

	@Override
	public int getOpcode() 
	{
		return rawMsg[0];
	}

}
