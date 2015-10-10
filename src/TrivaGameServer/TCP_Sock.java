package TrivaGameServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCP_Sock implements NetworkInterface, Runnable 
{
	private int port;
	private int timeout = 0;
	private boolean server;
	private volatile boolean run;
	private ServerSocket srvSocket;
	private Socket clientSocket;
	private Exception lastException;
	
	public TCP_Sock(int port, boolean server) throws IOException
	{
		this.port = port;
		this.server = server;
		
		if(server)
			srvSocket = new ServerSocket(port);
		else
			clientSocket = new Socket();
	}
	
	public TCP_Sock(int port, int timeout, boolean server) throws IOException
	{
		this(port, server);
		this.timeout = timeout;
		
		if(server)
			srvSocket.setSoTimeout(timeout);
		else
			clientSocket.setSoTimeout(timeout);
	}
	
	public TCP_Sock(Object socket)
	{
		if(socket instanceof ServerSocket)
			srvSocket = (ServerSocket)socket;
		else if(socket instanceof Socket)
			clientSocket = (Socket)socket;
		else
			throw new IllegalArgumentException("Passed object is not an accepted socket");
	}

	@Override
	public void run() 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Exception getLastException() 
	{
		return lastException;
	}

	@Override
	public boolean connect(String addr) 
	{
		try
		{
			if(clientSocket != null && clientSocket.isClosed())
				clientSocket = new Socket(addr,port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			lastException = e;
			return false;
		}
		return true;
	}

	@Override
	public void close() 
	{
		if(clientSocket != null && !clientSocket.isClosed())
		{
			try 
			{
				clientSocket.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}

	}

	@Override
	public void clearError() 
	{
		lastException = null;
	}

	@Override
	public boolean send(byte[] msg) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean send(byte[] msg, String ip) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean startReceive(HandableObject handler) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stopReceive() 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte[] receive() 
	{
		// TODO Auto-generated method stub
		return null;
	}

}
