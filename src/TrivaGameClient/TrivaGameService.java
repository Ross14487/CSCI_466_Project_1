package TrivaGameClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import TrivaGameServer.HandableObject;
import TrivaGameServer.NetworkInterface;

public class TrivaGameService extends Observable implements HandableObject, ServiceInterface 
{
	private int port;
	private Queue<Message> messageQueue = new ConcurrentLinkedQueue<Message>();
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
	
	public TrivaGameService(String addr, String groupIp, int port, NetworkInterface socket) throws UnknownHostException
	{
		this.port = port;
		this.addr = InetAddress.getByName(addr);
		this.socket = socket;
		socket.connect(groupIp);
		
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
		return socket.send(msg.getMessage(), addr.getHostAddress());
	}

	@Override
	public void handler(InetAddress addr, byte[] msg) 
	{
		try 
		{
			queueMessage(msg);
			setChanged();
			notifyObservers();
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
			 messageQueue.offer(new QuestionMessage(msg));
			break;
		case 0x01:
		case 0x02:
		case 0x05:
			messageQueue.offer(new OpcodeOnlyMessage(msg));
			break;
		case 0x03:
			messageQueue.offer(new UserIDMessage(msg));
			break;
		case 0x04:
			messageQueue.offer(new CorrectAnswerMessage(msg));
			break;
		case 0x08:
			messageQueue.offer(new UserScoreMessage(msg));
			break;
		}
	}
}
