package TrivaGameServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class TrivaMessage 
{
	public byte opcode;
	public InetAddress addr;
	public InetAddress groupIp;
	public UUID userID;
	public byte[] message;
	
	public TrivaMessage(InetAddress addr, byte[] message, boolean send) throws UnknownHostException
	{
		// sending a message so no need to parse the data
		if(send)
		{
			this.message = message;		// store the raw message
		}
		
		else
		{
			// a received packet so parse some of the message
			opcode = message[0];
			byte[] byteAddr = Arrays.copyOfRange(message, 1, 5);
			ByteBuffer buffer = ByteBuffer.wrap(Arrays.copyOfRange(message, 5, 22));
			userID = new UUID(buffer.getLong(), buffer.getLong());
			groupIp = InetAddress.getByAddress(byteAddr);
			
			if(message.length > 22)
				this.message = Arrays.copyOfRange(message, 23, message.length);
			else
				this.message = null;
		}
		
		this.addr = addr;
	}
}
