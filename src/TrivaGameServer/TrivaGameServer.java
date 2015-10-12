package TrivaGameServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Queue;

public class TrivaGameServer extends Observable implements HandableObject 
{
	private int port;
	private int maxGames;
	private NetworkInterface socket;
	private TrivaDataAccessInterface trivaData;
	private volatile ActivePlayerList playerList;
	private volatile boolean run;
	private volatile TrivaMessage message;  
	private static TrivaGameServer instance;
	private Queue<TrivaMessage> msgQueue = new LinkedList<TrivaMessage>();
	private Queue<InetAddress> groupAddressPool = new LinkedList<InetAddress>();
	private List<Thread> games = new ArrayList<Thread>();
	
	public TrivaMessage getMessage()
	{
		return message;
	}
	
	private TrivaGameServer(int maxGames, NetworkInterface socket, ActivePlayerList playerList, TrivaDataAccessInterface trivaData, int port) throws UnknownHostException
	{
		run = false;
		this.socket = socket;
		this.playerList = playerList;
		this.trivaData = trivaData;
		this.port = port;
		this.maxGames = maxGames;
		
		genMulticastAddr(new byte[] {(byte)225,0,0,0}, (byte)15);
	}
	
	public static synchronized TrivaGameServer createInstance(int maxGames, NetworkInterface socket,ActivePlayerList playerList, TrivaDataAccessInterface trivaData, int port)
	{
		if(instance == null)
		{
			try 
			{
				instance = new TrivaGameServer(maxGames, socket, playerList, trivaData, port);
			} 
			catch (UnknownHostException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return instance;
	}
	
	public static TrivaGameServer getInstance()
	{
		return instance;
	}
	
	public boolean isRunning()
	{
		return run;
	}
	
    public boolean startServer()
    {
    	run = true;
    	
    	// start the socket
    	socket.startReceive(this);
    	
    	// ready the player list for a new game
    	readyGame();
    	
    	// tick tock
    	while(run)
    	{    		
    		// check if a game should be started
    		if(playerList.allPlayersReady())
    			startGame();
    		
    		// send the first queued message if any are queued
    		sendQueuedMessage();
    		
    		// sleep for a sec
    		try 
    		{
				Thread.sleep(1);
			} 
    		catch (InterruptedException e) 
    		{}
    	}
	   	
	   	return true;
    }
    
    public boolean stopServer()
    {
    	run = false;
    	return socket.stopReceive();
    }

	@Override
	public void handler(InetAddress addr, byte[] msg) 
	{
		// check if it is the stop code
		if(msg[0] == 0xFF)
			stopServer();	// kill the server
		try 
		{
			message = new TrivaMessage(addr, msg, false);
			
			// let the observers know new data is available
			setChanged();
			notifyObservers();
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}
	}
	
	public synchronized void queueMessage(TrivaMessage msg)
	{
		msgQueue.add(msg);
	}
	
	public synchronized void returnAddress(InetAddress addr)
	{
		groupAddressPool.add(addr);
	}
	
	private void genMulticastAddr(byte[] address, byte number) throws UnknownHostException
	{
		for(byte n = 0; n<number; n++)
		{
			groupAddressPool.add(InetAddress.getByAddress(address));
			address[3]++; // Increment the last byte of an ipv4 addr
		}
	}
	
	private void startGame()
	{
		// close registration
		playerList.closeRegistration();
		
		//check if their is a thread avaiable
		if(games.size() <= maxGames)
		{
			// start a new game
			Thread game = new Thread(new TrivaGame(this, playerList.clone(), trivaData.getProblems())); 
			game.start();
			games.add(game);
			
			// ready for a new game
			readyGame();
		}
	}
	
	private void readyGame()
	{
		// set the group IP and Port
		try 
		{
			playerList.clearList();
			playerList.setPort(port);
			playerList.setGroupAddr(groupAddressPool.poll().getHostAddress());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private void sendQueuedMessage()
	{
		if(!msgQueue.isEmpty())
		{
			TrivaMessage msg = msgQueue.poll();
			// send the first message
			socket.send(msg.message, msg.addr.getHostAddress());
		}
	}
}
