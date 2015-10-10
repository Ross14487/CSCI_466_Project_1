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
import java.util.ArrayList;
//import java.util.UUID;


public class ActivePlayerList
{
    List<Player> activePlayers = new ArrayList<Player>();
    List<Player> timedOutPlayers = new ArrayList<Player>();
    boolean registrationIsOpen = false;

    
    ActivePlayerList()
    {
       
    }//constructor
    
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
    
    public boolean addPlayer(Player player)
    {
        return (activePlayers.add(player));    
    }//addPlayer
    
    public boolean removePlayer(Player player)
    {
        return (activePlayers.remove(player));
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
         
}//end ActivePlayerList
