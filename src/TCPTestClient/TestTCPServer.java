package TCPTestClient;

import java.io.IOException;
import TrivaGameServer.NetworkInterface;
import TrivaGameServer.TCP_Sock;

public class TestTCPServer
{
	public static void main(String ards[]) throws IOException
	{
		NetworkInterface socket = new TCP_Sock(2001, true);
		
		socket.startReceive(new SocketTestHandler(socket));
		
		while(true);
	}
}
