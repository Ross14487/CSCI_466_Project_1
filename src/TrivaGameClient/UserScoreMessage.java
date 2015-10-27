package TrivaGameClient;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class UserScoreMessage implements Message 
{
	private byte[] rawMsg;
	private int score;
	private String userName = "";
	
	public UserScoreMessage(byte[] rawMsg)
	{
		this.rawMsg = rawMsg;
		ByteBuffer buffer = ByteBuffer.wrap(Arrays.copyOfRange(rawMsg, 1, rawMsg.length));
		score = buffer.getInt();
		
		userName = new String(Arrays.copyOfRange(rawMsg, 5, rawMsg.length), StandardCharsets.UTF_8);
	}
	
	public UserScoreMessage(int opcode, int score, String userName)
	{
		byte[] rawErrMsg = userName.getBytes(StandardCharsets.UTF_8);
		ByteBuffer buffer = ByteBuffer.wrap(new byte[2+rawErrMsg.length]);
		
		buffer.put((byte)opcode);
		buffer.putInt(score);
		buffer.put(rawErrMsg);
		
		this.score = score;
		this.userName = userName;
		
		this.rawMsg = buffer.array();
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
	
	
	public String getUserName()
	{
		return userName;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public String getString()
	{
		return (userName + ": " + score);
	}
	
	public void updateScore(int score)
	{
		this.score = score;
	}
	
	@Override
    public boolean equals(Object object)
    {
		if(object != null && object instanceof UserScoreMessage)
			return this.userName.equals(((UserScoreMessage) object).userName);
		
		return false;
    }
}
