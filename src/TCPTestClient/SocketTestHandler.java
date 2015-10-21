package TCPTestClient;

import java.net.InetAddress;

import TrivaGameServer.HandableObject;
import TrivaGameServer.NetworkInterface;

public class SocketTestHandler implements HandableObject
{
	private NetworkInterface socket;
	
	public SocketTestHandler(NetworkInterface socket)
	{
		this.socket = socket;
	}

	@Override
	public void handler(InetAddress addr, byte[] msg) 
	{
		System.out.print("[" + addr.getHostAddress() + "]: ");
		//System.out.println(new String(msg));
		System.out.println(msg[0]);
		
		// echo
		socket.send(msg);		
	}

}
