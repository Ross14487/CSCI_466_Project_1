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
			byte[] msg = sct.receive();
			if(msg != null)
				System.out.println(new String(msg));
			
			msg = sct.receive();
			if(msg != null)
				System.out.println(sct.receive());
		}
	}
	
	public static void main(String[] args)
	{
		MulticastClient client = new MulticastClient(4446, "225.4.5.6");
		client.start();
	}
}
