package TrivaGameClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class BuzzerQueryMessage extends BasicUserMessage 
{
	private long timeStamp;
	
	public BuzzerQueryMessage(byte[] rawMsg) throws UnknownHostException 
	{
		super(rawMsg);
		ByteBuffer buffer = ByteBuffer.wrap(Arrays.copyOfRange(rawMsg, offset, rawMsg.length));
		timeStamp = buffer.getLong();
	}
	
	public BuzzerQueryMessage(int opcode, UUID playerId, InetAddress addr, long timeStamp) 
	{
		super(opcode, playerId, addr);
		this.timeStamp = timeStamp;
		
		ByteBuffer buffer = ByteBuffer.wrap(new byte[offset+8]);
		buffer.put(this.rawMsg);
		buffer.putLong(timeStamp);
		
		this.rawMsg = buffer.array();
	}
	
	
	public long getTimeStamp()
	{
		return timeStamp;
	}
}
