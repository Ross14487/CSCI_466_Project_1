package TrivaGameServer;

import java.io.IOException;

public class TrivaGameServerDriver 
{
	static final int userRegPort = 5000;
	static final int gamePort = 5500;
	static final int maxGames = 10;
	static final int userRegTimeout = 10000;
	static final String csvFile = "TrivaCSV.csv";
	
	public static void main(String args[])
	{
		// create the servers
		NetworkInterface userRegSocket;
		try 
		{
			CSVAccess.loadFile(csvFile);	// load the csv files
			userRegSocket = new TCP_Sock(userRegPort, userRegTimeout, true);
			NetworkInterface gameSocket = new UDP_Sock(gamePort, false);
			ActivePlayerList lobby = new ActivePlayerList();
			UserRegistrationServer userRegServer = UserRegistrationServer.createInstance(userRegSocket, lobby);
			TrivaGameServer gameServer = TrivaGameServer.createInstance(maxGames, gameSocket, lobby, CSVAccess.getInstance(), gamePort);
			
			// start the user reg server
			if(!userRegServer.startServer())
				throw new Exception("User registration server failed to start");
				
			// start the game server
			gameServer.startServer();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}
	}

}
