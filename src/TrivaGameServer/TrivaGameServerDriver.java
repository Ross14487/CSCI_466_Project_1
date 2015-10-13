package TrivaGameServer;

import java.io.IOException;

public class TrivaGameServerDriver 
{
	static final int userRegPort = 3025;
	static final int gamePort = 3030;
	static final int maxGames = 10;
	static final int userRegTimeout = 10000;
	static final String csvFile = "TrivaCSV.csv";
	
	public static void main(String args[])
	{
		// create the servers
		NetworkInterface userRegSocket;
		NetworkInterface gameSocket;
		try 
		{
			System.out.println("Starting Server...");
			System.out.println("Loading CSV...");
			CSVAccess.loadFile(csvFile);	// load the csv files
			System.out.printf("Opening TCP Socket on port %d...\n", userRegPort);
			userRegSocket = new TCP_Sock(userRegPort, userRegTimeout, true);
			System.out.printf("Opening UDP Socket on port %d...\n", gamePort);
			gameSocket = new UDP_Sock(gamePort, false);
			System.out.println("Creating Lobby...");
			ActivePlayerList lobby = new ActivePlayerList();
			UserRegistrationServer userRegServer = UserRegistrationServer.createInstance(userRegSocket, lobby);
			TrivaGameServer gameServer = TrivaGameServer.createInstance(maxGames, gameSocket, lobby, CSVAccess.getInstance(), gamePort);
			
			System.out.println("Starting user registration server...");
			// start the user reg server
			if(!userRegServer.startServer())
				throw new Exception("User registration server failed to start");
			
			System.out.println("Starting game server...");
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
