package TrivaGameServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TCP_Sock implements NetworkInterface, Runnable 
{
	private int port;
	private boolean server;
	private ServerSocket srvSocket;
	private Socket clientSocket;
	private Exception lastException;
    private HandableObject handler;
    private Thread rcvThread;
	
	private volatile boolean rx = false;
	private int timeout = 0;
	
	public TCP_Sock(int port, boolean server) throws IOException
	{
		this.port = port;
		this.server = server;
		
		if(server)
			srvSocket = new ServerSocket(port);
		
		clientSocket = new Socket();
	}
	
	public TCP_Sock(int port, int timeout, boolean server) throws IOException
	{
		this(port, server);
		this.timeout = timeout;
		
		if(server)
			srvSocket.setSoTimeout(timeout);

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
		receive(handler);
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
			if(!clientSocket.isClosed())
				clientSocket.close();
			
			clientSocket = new Socket(addr, port);
			clientSocket.setSoTimeout(timeout);
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
		if(clientSocket != null && !clientSocket.isClosed())
		{
			try 
			{
				send(msg, clientSocket);
			} 
			catch (IOException e) 
			{
				lastException = e;
				e.printStackTrace();
				return false;
			}
		}
		else
		{
			lastException = new IOException("Client connection is not open");
			return false;
		}
		
		return true;
	}

	@Override
	public boolean send(byte[] msg, String ip) 
	{
		try 
		{
			// open a new socket
			Socket tmpClient = new Socket(ip, port);
			
			// send the message
			send(msg, tmpClient);
			
			// close the socket
			tmpClient.close();
		} 
		catch (IOException e) {
			lastException = e;
			e.printStackTrace();
			return false;
		}
		
		
		return true;
	}

	@Override
	public boolean startReceive(HandableObject handler) 
	{
		rx = true;
	   	this.handler = handler;
	   	 
	   	if(rcvThread == null || !rcvThread.isAlive())
	   	{
		    	rcvThread = new Thread(this);					// create the new thread
		    	rcvThread.start();								// start the new thread
	   	}
	   	else
	   		return false;	// Thread already runnung
   	 
	   	return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean stopReceive() 
	{
		rx = false;
	   	if(rcvThread != null)
	   	{
	   		try 
	   		{
	   			// close the open socket
	   			close();
	   			// wait x2 the time out for the thread to exit gracefully
				Thread.sleep(srvSocket.getSoTimeout()*2);
					
				// if the thread fails to exit assume failure and force exit
				if(rcvThread.isAlive())
				{
					// worse case kill the thread
					rcvThread.stop();
						
					// if the thread is still alive return false
					if(rcvThread.isAlive())
						return false;
				}
					
				} 
	   		catch (IOException | InterruptedException e) 
	   		{
				lastException = e;
				return false; // general error
			} 
	   	}
	   	return true;
	}

	@Override
	public byte[] receive() 
	{
		try 
		{
			return receive(clientSocket);
		} 
		catch (IOException e) 
		{
			lastException = e;
			e.printStackTrace();
		}
		
		return null;
	}
	
	protected void receive(HandableObject handler)
    {
	   	if(server)
	   		serverReceive(handler);
	   	else
	   		clientReceive(handler);
    }
	
	private void send(byte[] msg, Socket sct) throws IOException
	{
		// create a new buffered output stream then write the msg
		DataOutputStream output = new DataOutputStream(sct.getOutputStream());
		output.write(msg, 0, msg.length);
	}
	
	private byte[] receive(Socket sct) throws IOException, SocketTimeoutException
	{
		byte[] msg = new byte[1024];
		DataInputStream input = new DataInputStream(sct.getInputStream());
		
		input.read(msg);
		
		return msg;
	}
	
	private void serverReceive(HandableObject handler)
	{
	   	while(rx && srvSocket != null && !srvSocket.isClosed())
	   	{
	   		try 
	   		{
	   			clientSocket = srvSocket.accept();
	   			clientSocket.setSoTimeout(timeout);
	   			
	   			clientReceive(handler);
	   		} 
	   		catch (SocketTimeoutException  e)
	   		{}
	   		catch (IOException e) 
	   		{
				lastException = e;
	   		}
	   	}
	}
	
	private void clientReceive(HandableObject handler)
	{
		byte[] msg;
		while(rx && !clientSocket.isClosed())
		{
			try 
			{
				msg = receive(clientSocket);
				handler.handler(clientSocket.getInetAddress(), msg);
			} 
			catch (SocketTimeoutException  e)
	   		{}
	   		catch (IOException e) 
	   		{
				lastException = e;
	   		}
		}
	}

}
