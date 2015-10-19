package TrivaGameClient;

import java.nio.ByteBuffer;
import java.util.UUID;

public class CorrectAnswerMessage implements Message 
{
	protected byte[] rawMsg;
	private UUID playerId;
	private int points;
	
	public CorrectAnswerMessage(byte[] rawMsg)
	{
		this.rawMsg = rawMsg;
		ByteBuffer buffer = ByteBuffer.wrap(rawMsg, 1, rawMsg.length);
		
		playerId = new UUID(buffer.getLong(), buffer.getLong());
		points = buffer.getInt();
	}
	
	public CorrectAnswerMessage(int opcode, UUID playerId, int points)
	{
		ByteBuffer buffer = ByteBuffer.wrap(new byte[21]);
		
		this.playerId = playerId;
		this.points = points;
		
		buffer.put((byte) opcode);
		buffer.putLong(playerId.getMostSignificantBits());
		buffer.putLong(playerId.getLeastSignificantBits());
		buffer.putInt(points);
	}
	
	public UUID getPlayerId()
	{
		return playerId;
	}
	
	public int getPoints()
	{
		return points;
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
