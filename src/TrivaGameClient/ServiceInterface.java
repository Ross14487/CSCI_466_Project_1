package TrivaGameClient;

public interface ServiceInterface 
{
	Message getQueuedMessage();
	boolean sendMessage(Message msg);
}
