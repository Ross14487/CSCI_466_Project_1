package TrivaGameServer;
import java.net.InetAddress;

public interface HandableObject 
{
	public void handler(InetAddress addr, byte[] msg);
}
