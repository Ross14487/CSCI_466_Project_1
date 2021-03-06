package TrivaGameClient;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

import TrivaGameServer.NetworkInterface;

public class UserRegistrationService implements ServiceInterface 
{
	private int port;
	private Queue<Message> messageQueue = new LinkedList<Message>();
	private NetworkInterface socket;
	private InetAddress addr;
	
	public int getPort()
	{
		return port;
	}
	
	public InetAddress getAddress()
	{
		return addr;
	}
	
	public UserRegistrationService(int port, NetworkInterface socket)
	{
		this.port = port;
		this.socket = socket;
	}
	
	public void setAddress(String addr) throws UnknownHostException
	{
		this.addr = InetAddress.getByName(addr);
	}

	@Override
	public Message getQueuedMessage() 
	{
		return messageQueue.poll();
	}

	@Override
	public boolean sendMessage(Message msg) throws UnknownHostException, IllegalArgumentException
	{
		socket.connect(addr.getHostAddress());
		
		if(!socket.send(msg.getMessage()))
			return false;
		
		// 0x03 and 0x04 do not get responses from the server so receive will just hang if this is the opcode
		if(msg.getOpcode() != 0x03 && msg.getOpcode() != 0x04)
			queueMessage(socket.receive());
		
		socket.close();
		return true;
	}
	
	private void queueMessage(byte[] msg) throws UnknownHostException, IllegalArgumentException
	{
		switch(msg[0])
		{
		case 0x00:
			messageQueue.add(new StatusMessage(msg));
			break;
		case 0x01:
			if(msg[1] != 0x00)
				messageQueue.add(new UserInformationMessage(msg));
			else
				messageQueue.add(new StatusMessage(msg));
			break;
		case 0x02:
			messageQueue.add(new StatusMessage(msg));
			break;
		default:
			throw new IllegalArgumentException("Invalid Message Received");
		}
	}
}
