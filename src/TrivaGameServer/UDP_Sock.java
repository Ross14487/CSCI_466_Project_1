package TrivaGameServer;
import java.io.*;
import java.net.*;

public class UDP_Sock implements NetworkInterface, Runnable {
	 private int prt;
     private volatile boolean rx;
     private String defaultAddr;
     private DatagramSocket sct;
     private Exception lastException;
     private Thread rcvThread;
     private HandableObject handler;

     // getters
     public Exception getLastException()
     {
    	 return lastException;
     }
     
 	public void clearError() 
 	{
 		lastException = null;
 	}
     
     public UDP_Sock(int port)
     {
    	 prt = port;
    	 rx = false;
    	 defaultAddr = "255.255.255.255";

    	 try 
    	 {
    		 sct = new DatagramSocket(port);
    		 sct.setSoTimeout(1000);
    		 sct.setBroadcast(true);
    	 } 
    	 catch (SocketException e) 
    	 {
			lastException = e;
    	 }
     }
     public UDP_Sock(int port, int timeout)
     {
    	 prt = port;
    	 rx = false;
    	 defaultAddr = "255.255.255.255";
    	 
    	 try 
    	 {
    		 sct = new DatagramSocket(port);
    		 sct.setSoTimeout(timeout);
    		 sct.setBroadcast(true);
    	 } 
    	 catch (SocketException e) 
    	 {
			lastException = e;
    	 }
     }
     public UDP_Sock(DatagramSocket socket)
     {
    	 // use the passed socket
    	 sct = socket;
    	 rx = false;
    	 defaultAddr = "255.255.255.255";
    	 prt = socket.getLocalPort();
     }
     public void close()
     {
    	 if(sct != null)
    		 sct.close();
     }
     
 	public boolean connect(String addr) 
 	{
 		defaultAddr = addr;
 		return true;
 	}
 	
     public boolean send(byte[] msg)
     {
    	 return send(msg, defaultAddr);
     }
     public boolean send(byte[] msg, String ip)
     {
    	 try 
    	 {
			InetAddress addr = InetAddress.getByName(ip);
			sct.send(new DatagramPacket(msg, msg.length, addr, prt));
    	 } 
    	 catch (UnknownHostException e) 
    	 {
			lastException = e;
			return false;
    	 } 
    	 catch (IOException e) 
    	 {
			lastException = e;
			return false;
    	 }
    	 catch (Exception e) {
    		 lastException = e;
    		 return false;
    	 }
    	 return true;
     }
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
	public boolean stopReceive()
     {
    	 rx = false;
    	 if(rcvThread != null)
    	 {
    		 try {
    			 // wait x2 the time out for the thread to exit gracefully
				Thread.sleep(sct.getSoTimeout()*2);
				
				// if the thread fails to exit assume failure and force exit
				if(rcvThread.isAlive())
				{
					rcvThread.stop();
					
					// if the thread is still alive return false
					if(rcvThread.isAlive())
						return false;
				}
				
			} catch (SocketException | InterruptedException e) {
				lastException = e;
				return false; // general error
			}
    	 }
    	 return true;
     }
    
    public byte[] receive()
    {
   	 byte[] msg = new byte[1024];
   	 
		 try 
		 {
			DatagramPacket msgPckt = new DatagramPacket(msg, msg.length, InetAddress.getByName("0.0.0.0"), prt);
			sct.receive(msgPckt);
			
			return msgPckt.getData();
		 } 
		 catch (SocketTimeoutException  e)
		 {
			 lastException = e;
		 }
		 catch (IOException e) 
		 {
			lastException = e;
		 }
		 
		 return null;	// no data received or error occurred
    }
     
     // Runnable methods
     public void run()
     {
    	 receive(handler);
     }
     
     protected void receive(HandableObject handler)
     {
    	 byte[] msg = new byte[1024];
    	 while(rx && sct != null && !sct.isClosed())
    	 {
    		 try 
    		 {
    			DatagramPacket msgPckt = new DatagramPacket(msg, msg.length, InetAddress.getByName("0.0.0.0"), prt);
				sct.receive(msgPckt);
				handler.handler(msgPckt.getAddress(), msgPckt.getData());
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
