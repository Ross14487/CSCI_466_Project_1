package TrivaGameServer;

import java.net.DatagramPacket;

public interface HandableObject
{
	void handler(DatagramPacket packet);
}
