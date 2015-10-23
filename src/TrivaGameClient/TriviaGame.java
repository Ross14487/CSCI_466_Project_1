package TrivaGameClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.Observable;
import java.util.Observer;

public class TriviaGame extends Observable implements Observer 
{
    private ServiceInterface service;
    private UUID playerID, correctPlayerID, chosenAnswerId, answerId;
    private InetAddress groupIp;
    private int portNum, buzzerTime, elapsedTime, unlockTime, startTime, timeLeft, allowedTime = 25, playerScore, correctInRow = 0;
    private boolean freezeFlag = false;
    private QuestionMessage questionMsg;

    public TriviaGame(ServiceInterface service, InetAddress groupIp, UUID playerID, int portNum)
    {
        this.service = service;
        this.groupIp = groupIp;
        this.playerID = playerID;
        this.portNum = portNum;
        this.playerScore = 0;
        
        ((TrivaGameService)service).addObserver(this);
    }//constructor
    
    private void setUnlockTime()
    {
        unlockTime = (int)System.currentTimeMillis();
    }
    
    public int getAllowedTime()
    {
        return allowedTime;
    }
    
    public int getTimeRemaining()
    {
           startTime = unlockTime;
           timeLeft = allowedTime - ((int)System.currentTimeMillis() - startTime);
           return timeLeft;
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
        {
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
    }
    
    public void leaveGame()
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
    
    public int GetPortNum()
    {
        return portNum;
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
    }
    
    public int getScore()
    {
        return playerScore;
    }
   
    public void preformAction(ServiceInterface srv)
    {
        Message msg = null;
        
        // read the queue
        while ((msg = srv.getQueuedMessage()) != null)
        {
            switch (msg.getOpcode())
            {
                case 0x00: //Question
                    questionMsg = ((QuestionMessage)msg);            
                    try
                    {
                    	srv.sendMessage(new BasicUserMessage(0x00, playerID, groupIp));
                    	setChanged();
                        notifyObservers();
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

                    setChanged();

                    break;

                case 0x02: //Buzzer Query
                    try
                    {
                    	srv.sendMessage(new BuzzerQueryMessage(0x02, playerID, groupIp, elapsedTime));
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
                        	srv.sendMessage(new AnswerMessage(0x03, playerID, groupIp, chosenAnswerId, elapsedTime));
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
                    
                    try
                    {
                    	srv.sendMessage(new BasicUserMessage(0x04, playerID, groupIp));
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
                    try
                    {
                    	srv.sendMessage(new BasicUserMessage(0x05, playerID, groupIp));
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
                    try
                    {
                    	srv.sendMessage(new BasicUserMessage(0x06, playerID, groupIp));
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
                    break;
                    
                case 0x08: //Get Scores
                    //??? Is this a message that will list all the scores for all the players or just our own score??? only message with getPoints is CorrectAnswerMessage??????
                    break;     
            }//switch
        	try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {	}
        }//while true
        
        notifyObservers();
    }//run

	@Override
	public void update(Observable o, Object arg) 
	{
		if(o instanceof ServiceInterface)
		{
			preformAction(((ServiceInterface)o));
		}
	}

}//TriviaGame class
