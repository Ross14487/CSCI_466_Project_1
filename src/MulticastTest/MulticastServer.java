package MulticastTest;

import TrivaGameClient.OpcodeOnlyMessage;
import TrivaGameServer.NetworkInterface;
import TrivaGameServer.UDP_Sock;

public class MulticastServer 
{
	private NetworkInterface sct;
	private String testMsg1 = "This is a test", groupIp;
	private int testMsg2 = 555;
	public MulticastServer(int port, String groupIp)
	{
		sct = new UDP_Sock(port, false);
		this.groupIp = groupIp;
	}
	
	public void start() throws InterruptedException
	{
		while(true)
		{
			System.out.println("Sent[" + groupIp + ": " + testMsg1);
			sct.send(testMsg1.getBytes(), groupIp);
			Thread.sleep(1000);
			System.out.println("Sent[" + groupIp + ": " + testMsg2);
			sct.send(new OpcodeOnlyMessage(testMsg2).getMessage(), groupIp);
			Thread.sleep(1000);
		}
	}
	
	static void main(String[] args) throws InterruptedException
	{
		MulticastServer svr = new MulticastServer(4446, "203.0.113.0");
		
		svr.start();
	}
}
