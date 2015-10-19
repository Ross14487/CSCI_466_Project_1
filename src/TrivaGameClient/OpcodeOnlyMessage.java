package TrivaGameClient;

public class OpcodeOnlyMessage implements Message 
{
	private byte[] rawMsg;
	
	public OpcodeOnlyMessage(byte[] rawMsg)
	{
		this.rawMsg = rawMsg;
	}
	
	public OpcodeOnlyMessage(int opcode)
	{
		rawMsg = new byte[1];
		rawMsg[0] = ((byte)opcode);
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
