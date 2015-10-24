package TrivaGameClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class AnswerMessage extends BasicUserMessage 
{
	private int timeElapsed;
	private UUID answerId;
	
	public AnswerMessage(byte[] rawMsg) throws UnknownHostException 
	{
		super(rawMsg);
		ByteBuffer buffer = ByteBuffer.wrap(Arrays.copyOfRange(rawMsg, offset, rawMsg.length));
		
		answerId = new UUID(buffer.getLong(), buffer.getLong());
		timeElapsed = buffer.getInt();
	}

	public AnswerMessage(int opcode, UUID playerId, InetAddress addr, UUID answerId, int timeElapsed) 
	{
		super(opcode, playerId, addr);
		this.answerId = answerId;
		this.timeElapsed = timeElapsed;
		ByteBuffer buffer = ByteBuffer.wrap(new byte[offset+20]);
		
		buffer.put(rawMsg);
		buffer.putLong(answerId.getMostSignificantBits());
		buffer.putLong(answerId.getLeastSignificantBits());
		buffer.putInt(timeElapsed);
		
		this.rawMsg = buffer.array();
	}
	
	public UUID getAnswerId()
	{
		return answerId;
	}
	
	public int getTimeElapsed()
	{
		return timeElapsed;
	}
}
