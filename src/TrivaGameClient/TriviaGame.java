package TrivaGameClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.Observable;

import TrivaGameServer.TrivaGameServer;

public class TriviaGame extends Observable implements Runnable 
{
    private ServiceInterface service;
    private UUID playerID, correctPlayerID, chosenAnswerId, answerId;
    private InetAddress groupIp, addr;
    private int portNum, buzzerTime, elapsedTime, unlockTime, allowedTime = 25, playerScore, correctInRow = 0;
    private TrivaGameServer server;
    private boolean freezeFlag = false;
    private QuestionMessage questionMsg;

    public TriviaGame(ServiceInterface service, InetAddress groupIp, InetAddress addr, UUID playerID, int portNum, TrivaGameServer server)
    {
        this.service = service;
        this.groupIp = groupIp;
        this.addr = addr;
        this.playerID = playerID;
        this.portNum = portNum;
        this.server = server;
        this.playerScore = playerScore;
    }//constructor
    
    private void setUnlockTime()
    {
        unlockTime = (int)System.currentTimeMillis();
    }
    
    private void setBuzzerTime()
    {
        buzzerTime = (int)System.currentTimeMillis();
    }
    
    private void setElapsedTime()
    {
        elapsedTime = buzzerTime - unlockTime;
    }
    
    public void submitAnswer(UUID answerID)
    {
        setBuzzerTime();
        if (buzzerTime <= allowedTime)
        try
        {
            service.sendMessage(new BasicUserMessage(0x01, playerID, groupIp));
        }
        catch (UnknownHostException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        chosenAnswerId = answerID;
    }
    
    public void leaveGame(UUID playerID, InetAddress groupIp)
    {
        try
        {
            service.sendMessage(new BasicUserMessage(0x07, playerID, groupIp));
        }
        catch (UnknownHostException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public UUID getPlayerID()
    {
        return playerID;
    }
    
    public InetAddress getGroupIp()
    {
        return groupIp;
    }
    
    public InetAddress GetAddress()
    {
        return addr;
    }
    
    public int GetPortNum()
    {
        return portNum;
    }
    
    public TrivaGameServer GetServer()
    {
        return server;
    }
    
    public int getBuzzerTime()
    {
        return buzzerTime;
    }
    
    public int getElapsedTime()
    {
        return elapsedTime;
    }
    
    public QuestionMessage getQuestionMsg()
    {
        return questionMsg;
    }
    
    public boolean getFreezeFlag()
    {
        return freezeFlag;
    }
    
    public void setScore(int pointsAwarded)
    {
        playerScore = playerScore + pointsAwarded;
        setChanged();
        notifyObservers();
    }
    
    public int getScore()
    {
        return playerScore;
    }
   
    public void run()
    {
        Message msg = null;
        while (true)
        {
            msg = service.getQueuedMessage();
            if (msg == null)
                continue;
            switch (msg.getOpcode())
            {
                case 0x00: //Question
                    questionMsg = ((QuestionMessage)msg);
                    try
                    {
                        service.sendMessage(new BasicUserMessage(0x00, playerID, groupIp));
                    }
                    catch (UnknownHostException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (IllegalArgumentException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;

                case 0x01: //Buzzer
                    freezeFlag = true;
                    if (elapsedTime <= allowedTime)
                    try
                    {
                        service.sendMessage(new BasicUserMessage(0x01, playerID, groupIp));
                    }
                    catch (UnknownHostException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (IllegalArgumentException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;

                case 0x02: //Buzzer Query
                    try
                    {
                        service.sendMessage(new BuzzerQueryMessage(0x02, playerID, groupIp, elapsedTime));
                    }
                    catch (UnknownHostException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (IllegalArgumentException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                    
                case 0x03: //Allow Answer
                    correctPlayerID = ((UserIDMessage)msg).getUserId();
                    if (playerID == correctPlayerID)
                    {
                        setElapsedTime();
                        try
                        {
                            service.sendMessage(new AnswerMessage(0x03, playerID, addr, chosenAnswerId, elapsedTime));
                        }
                        catch (UnknownHostException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        catch (IllegalArgumentException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }//if playerID == correctPlayerID
                    break;
                    
                case 0x04: //Answer Correct
                    if((((CorrectAnswerMessage)msg).getPlayerId() == playerID) && (((CorrectAnswerMessage)msg).getPoints() != 0))
                    {
                        correctInRow ++;
                        if ((correctInRow % 5) == 0)
                            allowedTime = allowedTime - 1;
                        if (allowedTime < 10)
                            allowedTime = 10;
//                        int maxPoints, pointsAwarded;
//                        maxPoints = ((CorrectAnswerMessage)msg).getPoints();
//                        if (getElapsedTime() <= 5)
//                            pointsAwarded = maxPoints;
//                        else if (getElapsedTime() <= 10)
//                            pointsAwarded = (int)(0.75 * maxPoints);
//                        else if (getElapsedTime() <= 15)
//                            pointsAwarded = (int)(0.5 * maxPoints);
//                        else if (getElapsedTime() <= 20)
//                            pointsAwarded = (int)(0.25 * maxPoints);
//                        else
//                            pointsAwarded = 1;
                        setScore (((CorrectAnswerMessage)msg).getPoints());
                    }
                    else if((((CorrectAnswerMessage)msg).getPlayerId() == playerID) && (((CorrectAnswerMessage)msg).getPoints() == 0))
                    {
                        correctInRow = 0;  
                        allowedTime = allowedTime + 2;
                        if (allowedTime > 25)
                            allowedTime = 25;
                    }
                    else if (((CorrectAnswerMessage)msg).getPlayerId() != playerID)  //Is this needed?  what if a player does not win the buzz in, does correctInRow go to 0?
                    {
                        correctInRow = 0;  
                        allowedTime = allowedTime + 2;
                        if (allowedTime > 25)
                            allowedTime = 25;
                    }
                    try
                    {
                        service.sendMessage(new BasicUserMessage(0x04, playerID, groupIp));
                    }
                    catch (UnknownHostException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (IllegalArgumentException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                    
                case 0x05: //Start or Unfreeze
                    setUnlockTime();
                    freezeFlag = false;
                    setChanged();
                    notifyObservers();
                    try
                    {
                        service.sendMessage(new BasicUserMessage(0x05, playerID, groupIp));
                    }
                    catch (UnknownHostException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (IllegalArgumentException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                    
                case 0x06: //Times Up
                    correctPlayerID = ((UserIDMessage)msg).getUserId(); //should this be AnswerMessage ????????
                    freezeFlag = true;
                    setChanged();
                    notifyObservers();
                    try
                    {
                        service.sendMessage(new BasicUserMessage(0x06, playerID, groupIp));
                    }
                    catch (UnknownHostException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (IllegalArgumentException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                    
                case 0x07: //Leave Game
                    correctPlayerID = ((UserIDMessage)msg).getUserId();
                    try
                    {
                        service.sendMessage(new BasicUserMessage(0x07, playerID, groupIp));
                    }
                    catch (UnknownHostException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (IllegalArgumentException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                    
                case 0x08: //Get Scores
                    //??? Is this a message that will list all the scores for all the players or just our own score??? only message with getPoints is CorrectAnswerMessage??????
                    try
                    {
                        service.sendMessage(new BasicUserMessage(0x08, playerID, groupIp));
                    }
                    catch (UnknownHostException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (IllegalArgumentException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;     
            }//switch
        }//while true
    }//run

}//TriviaGame class
