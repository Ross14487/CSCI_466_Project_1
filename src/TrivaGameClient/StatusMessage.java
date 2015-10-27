package TrivaGameClient;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class StatusMessage implements Message 
{
	private byte[] rawMsg;
	private boolean success;
	private String errMsg = "";
	
	public StatusMessage(byte[] rawMsg)
	{
		this.rawMsg = rawMsg;
		success = rawMsg[1] != 0 ? true : false;
		if(!success)
			errMsg = new String(Arrays.copyOfRange(rawMsg, 2, rawMsg.length), StandardCharsets.UTF_8);
	}
	
	public StatusMessage(int opcode, boolean success, String errorMsg)
	{
		byte[] rawErrMsg = errorMsg.getBytes(StandardCharsets.UTF_8);
		ByteBuffer buffer = ByteBuffer.wrap(new byte[2+rawErrMsg.length]);
		
		buffer.put((byte)opcode);
		buffer.put((byte) (success ? 0x01 : 0x00));
		buffer.put(rawErrMsg);
		
		this.success = success;
		this.errMsg = errorMsg;
		
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
	
	public boolean getSuccess()
	{
		return success;
	}
	
	public String getErrorMessage()
	{
		return errMsg;
	}

}
