package TrivaGameClient;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class UserIDMessage implements Message 
{
	private byte[] rawMsg;
	private UUID userId;
	
	public UserIDMessage(byte[] rawMsg)
	{
		ByteBuffer buffer = ByteBuffer.wrap(Arrays.copyOfRange(rawMsg, 1, rawMsg.length));
		this.rawMsg = rawMsg;
		
		userId = new UUID(buffer.getLong(), buffer.getLong());
	}
	
	public UserIDMessage(int opcode, UUID userId)
	{
		ByteBuffer buffer = ByteBuffer.wrap(new byte[17]);
		buffer.put((byte) opcode);
		buffer.putLong(userId.getMostSignificantBits());
		buffer.putLong(userId.getLeastSignificantBits());
		
		this.rawMsg = buffer.array();
	}
	
	public UUID getUserId()
	{
		return userId;
	}

	@Override
	public byte[] getMessage() 
	{
		// TODO Auto-generated method stub
		return rawMsg;
	}

	@Override
	public int getOpcode() 
	{
		// TODO Auto-generated method stub
		return rawMsg[0];
	}
}
