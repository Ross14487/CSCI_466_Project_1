package TrivaGameClient;

import java.net.UnknownHostException;

public interface ServiceInterface 
{
	Message getQueuedMessage();
	boolean sendMessage(Message msg) throws UnknownHostException, IllegalArgumentException;
}
