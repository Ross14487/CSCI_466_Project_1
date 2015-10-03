package TrivaGameServer;

public interface NetworkInterface 
{
	public Exception getLastException();
	public boolean connect(String addr);
	public void close();
	public void clearError();
	public boolean send(byte[] msg);
	public boolean send(byte[] msg, String ip);
	public boolean startReceive(HandableObject handler);
	public boolean stopReceive();
	public byte[] receive();
}
