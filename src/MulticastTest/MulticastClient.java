package MulticastTest;

import TrivaGameServer.NetworkInterface;
import TrivaGameServer.UDP_Sock;

public class MulticastClient 
{
	private NetworkInterface sct;
	
	public MulticastClient(int port, String groupIp)
	{
		sct = new UDP_Sock(port, true);
		sct.connect(groupIp);
	}
	
	public void start()
	{
		while(true)
		{
			System.out.println(new String(sct.receive()));
			System.out.println(sct.receive());
		}
	}
	
	public static void main(String[] args)
	{
		MulticastClient client = new MulticastClient(4446, "203.0.113.0");
		client.start();
	}
}
