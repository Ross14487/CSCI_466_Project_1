package TrivaGameClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

import TrivaGameServer.HandableObject;
import TrivaGameServer.NetworkInterface;

public class TrivaGameService implements HandableObject, ServiceInterface 
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
	
	public TrivaGameService(String addr, int port, NetworkInterface socket) throws UnknownHostException
	{
		this.port = port;
		this.addr = InetAddress.getByName(addr);
		this.socket = socket;
		
		socket.startReceive(this);
	}

	@Override
	public Message getQueuedMessage() 
	{
		return messageQueue.poll();
	}

	@Override
	public boolean sendMessage(Message msg) throws UnknownHostException, IllegalArgumentException 
	{
		return socket.send(msg.getMessage());
	}

	@Override
	public void handler(InetAddress addr, byte[] msg) 
	{
		try 
		{
			queueMessage(msg);
		} 
		catch (UnknownHostException | IllegalArgumentException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void queueMessage(byte[] msg) throws UnknownHostException, IllegalArgumentException
	{
		switch(msg[0])
		{
		case 0x00:
			messageQueue.add(new QuestionMessage(msg));
			break;
		case 0x01:
		case 0x02:
		case 0x05:
			messageQueue.add(new OpcodeOnlyMessage(msg));
			break;
		case 0x03:
			messageQueue.add(new UserIDMessage(msg));
			break;
		case 0x04:
			messageQueue.add(new CorrectAnswerMessage(msg));
			break;
		}
	}
}
