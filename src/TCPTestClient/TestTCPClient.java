package TCPTestClient;

import java.io.IOException;
import java.net.InetAddress;

import TrivaGameServer.NetworkInterface;
import TrivaGameServer.TCP_Sock;

public class TestTCPClient 
{
	public static void main(String ards[]) throws IOException
	{
		String msg = "Test String";
		NetworkInterface socket = new TCP_Sock(2001, false);
		
		socket.connect(InetAddress.getLoopbackAddress().getHostAddress());
		
		while(true)
		{
			socket.send(msg.getBytes());
			
			System.out.println(new String(socket.receive()));
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {}
		}
	}
}
