package TrivaGameServer;

/*************************************************************************
 * Name        :  Ross Mitchell 
 * email       :  RPMitchell@mtech.edu
 * Date        :  10-8-2012
 * Description :  This is the Player Class for CSCI-466 Networks Project 1,
 * Trivia Game Server.  This class creates an object for each player.  The 
 * 'getter' for the players will return the name, playerId, address, and
 * score.  There is a 'setter' to update the player's score.
 *      
 *************************************************************************/
//import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
//import java.util.UUID;


public class ActivePlayerList
{
	private int gamePort;
	private InetAddress groupAddr;
    private List<Player> activePlayers = new ArrayList<Player>();
    private List<Player> timedOutPlayers = new ArrayList<Player>();
    private boolean registrationIsOpen = false;

    
    public ActivePlayerList()
    {
       
    }//constructor
    
    private ActivePlayerList(ActivePlayerList playerList)
    {
    	this.gamePort = playerList.gamePort;
    	this.groupAddr = playerList.groupAddr;
    	this.registrationIsOpen = playerList.registrationIsOpen;
    	this.activePlayers.addAll(playerList.activePlayers);
    	this.timedOutPlayers.addAll(playerList.timedOutPlayers);
    }
    
    public ActivePlayerList clone()
    {
    	return new ActivePlayerList(this);	
    }
    
    public List<Player> getListOfPlayers()
    {
    	return activePlayers;
    }
    
    public void clearList()
    {
    	gamePort = 0;
    	groupAddr = null;
    	activePlayers = new ArrayList<Player>();
    	timedOutPlayers = new ArrayList<Player>();
    	registrationIsOpen = false;
    }
    
    public void clearReceived()
    {
    	for(Player player : activePlayers)
    		player.setReceived(false);
    }
    
    public void setPlayerReceived(UUID id, boolean received)
    {
    	findPlayer(id).setReceived(received);
    }
    
    public boolean didPlayerReceive(UUID id)
    {
    	return findPlayer(id).getReceived();
    }
    
    public boolean didAllPlayersReceive()
    {
    	for(Player player : activePlayers)
    	{
    		if(!player.getReceived())
    			return false;
    	}
    	
    	return true;
    }
    
    public boolean playersLeft()
    {
    	return !activePlayers.isEmpty();
    }
    
    public void closeRegistration()
    {
        registrationIsOpen = false;
    }//closeRegistration
    
    public void openRegistration()
    {
        registrationIsOpen = true;
    }//openRegistration
    
    public boolean isRegistrationOpen()
    {
       return registrationIsOpen; 
    }//isRegistrationOpen
    
    public UUID addPlayer(Player player)
    {
    	if(activePlayers.add(player))
    		return player.getPlayerId();
    	else
    		return null;
    }//addPlayer
    
    public boolean removePlayer(UUID playerId)
    {
    	Player player = findPlayer(playerId);
        return player != null ? activePlayers.remove(player) : false;
    }//removePlayer
     
    public List<Player> findTimedOutPlayers(int timedOut)
     {
         for (Player player : activePlayers)
         {
           if (player.getTimedOut() == true)
           {
               timedOutPlayers.add(player);
               activePlayers.remove(player);
           }
         }
         return timedOutPlayers;
     }//findTimedOutPlayers
    
    public void setPort(int port)
    {
    	gamePort = port;
    }
    
    public void setGroupAddr(String address) throws UnknownHostException
    {
    	groupAddr = InetAddress.getByName(address);
    }
    
    public int getGamePort()
    {
    	return gamePort;
    }
    
    public InetAddress getGroupAddress()
    {
    	return groupAddr;
    }
    
    public boolean allPlayersReady()
    {
    	if(activePlayers.isEmpty())
    		return false;
    	
    	for(Player player : activePlayers)
    	{
    		if(!player.isReady())
    			return false;
    	}
    	return true;
    }
    
    public void playerReady(UUID id, boolean ready)
    {
    	Player player = findPlayer(id);
    	player.setReady(ready);
    }
    
    public void clearPlayersReady()
    {
    	for(Player player : activePlayers)
    		player.setReady(false);
    }
    
    private Player findPlayer(UUID id)
    {
    	for(Player player : activePlayers)
    	{
    		if(id.equals(id))
    			return player;
    	}
    	return null;	// player not found
    }
         
}//end ActivePlayerList
