package TrivaGameClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class BasicUserMessage implements Message 
{
	protected byte[] rawMsg;
	private UUID playerId;
	private InetAddress groupIp;
	
	protected final int offset = 21;
	
	public BasicUserMessage(byte[] rawMsg) throws UnknownHostException
	{
		byte[] ipaddr = new byte[4];
		this.rawMsg = rawMsg;
		ByteBuffer buffer = ByteBuffer.wrap(Arrays.copyOfRange(rawMsg, 1, rawMsg.length));
		
		for(int x=0; x<4; x++)
			ipaddr[x] = buffer.get();
		
		groupIp = InetAddress.getByAddress(ipaddr);
		
		playerId = new UUID(buffer.getLong(), buffer.getLong());
	}
	
	public BasicUserMessage(int opcode, UUID playerId, InetAddress addr)
	{
		ByteBuffer buffer = ByteBuffer.wrap(new byte[21]);
		
		this.playerId = playerId;
		
		buffer.put((byte) opcode);
		buffer.put(addr.getAddress());
		buffer.putLong(playerId.getMostSignificantBits());
		buffer.putLong(playerId.getLeastSignificantBits());
		
		this.rawMsg = buffer.array();
	}
	
	public UUID getPlayerId()
	{
		return playerId;
	}
	
	public InetAddress getGroupAddress()
	{
		return groupIp;
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
