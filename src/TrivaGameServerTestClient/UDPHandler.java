package TrivaGameServerTestClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

import TrivaGameServer.HandableObject;

public class UDPHandler implements HandableObject
{
	public void handler(InetAddress addr, byte[] msg) 
	{
		try 
		{
			TestClient.printMsg(addr.getHostAddress(), msg, false);
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}
		
	}

}
