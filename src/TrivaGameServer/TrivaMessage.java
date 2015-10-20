package TrivaGameServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import TrivaGameClient.AnswerMessage;
import TrivaGameClient.BasicUserMessage;
import TrivaGameClient.BuzzerQueryMessage;
import TrivaGameClient.Message;

public class TrivaMessage 
{
	public InetAddress addr;
	public InetAddress groupIp;
	public Message message;
	
	public TrivaMessage(InetAddress addr, byte[] msg) throws UnknownHostException
	{
		switch(msg[0])
		{
		case 0x00:
		case 0x01:
		case 0x04:
		case 0x05:
		case 0x06:
		case 0x07:
			message = new BasicUserMessage(msg);
			break;
		case 0x02:
			message = new BuzzerQueryMessage(msg);
			break;
		case 0x03:
			message = new AnswerMessage(msg);
			break;
		}
		
		this.addr = addr;
		this.groupIp = ((BasicUserMessage) message).getGroupAddress();
	}
	
	public TrivaMessage(InetAddress addr, Message msg) 
	{			
		this.addr = addr;
		this.message = msg;
		
		if(msg instanceof BasicUserMessage)
			groupIp = ((BasicUserMessage) msg).getGroupAddress();
		else
			groupIp = InetAddress.getLoopbackAddress();
		
	}
}
