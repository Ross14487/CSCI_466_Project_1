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

import java.util.UUID;
//import java.util.Timer;

public class Player
{
    private String name;
    private UUID playerId;
    private String address;
    private int score; 
    private long playTimeStamp, currentTimeStamp, outOfTime = 180000;
 //   private Timer timeOutTimer = new Timer();
    private boolean timedOut;
    
    public Player(String [] args, String playerAddress)
    {
        name = args[0];
        playerId = UUID.randomUUID();
        address = playerAddress;
        score = 0;
        playTimeStamp = System.currentTimeMillis();
        timedOut = false;
           
    }//constructor
    
    public String getName()
    {
        return name;
    }//getName
    
    public UUID getPlayerId()
    {
        return playerId;
    }//getPlayerId
    
    public String getAddress()
    {
        return address;
    }//getAddress
    
    public int getScore()
    {
        return score;
    }//getScore
    
    public void setScore (int newScore)
    {
        score = newScore;
    }//setScore
    
    public boolean getTimedOut()
    {
        return timedOut;
    }//getTimedOut
    
    
    public void resetPlayTimeStamp()
    {
        playTimeStamp = System.currentTimeMillis();
    }//resetPlayTimeStamp
    
    public void setTimedOut()
    {
        if ((currentTimeStamp - playTimeStamp) >= outOfTime)
        {
            timedOut = true;
        }
        else 
        {
            timedOut = false;
        }
    }//setTimedOut
    
}//end Player Class
