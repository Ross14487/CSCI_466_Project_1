package TrivaGameClient;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RegisterUserMessage implements Message 
{
	private byte[] rawMsg;
	private String userName;
	
	public RegisterUserMessage(byte[] rawMsg)
	{
		this.rawMsg = rawMsg;
		
		userName = new String(Arrays.copyOfRange(rawMsg, 1, rawMsg.length), StandardCharsets.UTF_8);
	}
	
	public RegisterUserMessage(int opcode, String userName)
	{
	    int x = 1;
	    byte[] msg = new byte[1+userName.length()];
	    this.userName = userName;
	    
	    msg[0] = (byte) opcode;
	    
	    for(byte chr : userName.getBytes(StandardCharsets.UTF_8))
	        msg[x++] = chr;
	    
	    this.rawMsg = msg;
	}
	
	public String getUserName()
	{
		return userName;
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
