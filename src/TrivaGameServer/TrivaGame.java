package TrivaGameServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.UUID;

import TrivaGameClient.Message;
import TrivaGameClient.OpcodeOnlyMessage;
import TrivaGameClient.QuestionMessage;
import TrivaGameClient.UserIDMessage;
import TrivaGameClient.AnswerMessage;
import TrivaGameClient.BasicUserMessage;
import TrivaGameClient.BuzzerQueryMessage;
import TrivaGameClient.CorrectAnswerMessage;

public class TrivaGame implements Observer, Runnable 
{
	private Problem currentProblem;
	private TrivaGameServer server;
	private ActivePlayerList playerList;
	private InetAddress groupIp;
	private boolean nextQuestion = true;
	private volatile boolean run = true;
	private Queue<TrivaMessage> msgQueue = new LinkedList<TrivaMessage>();
	private List<Problem> questions;
	
	public TrivaGame(TrivaGameServer server, ActivePlayerList playerList, List<Problem> questions)
	{
		this.server = server;
		this.playerList = playerList;
		this.groupIp = playerList.getGroupAddress();
		this.questions = questions;
		this.server.addObserver(this);
	}

	@Override
	public void update(Observable arg0, Object arg1) 
	{
		if(arg0 instanceof TrivaGameServer)
		{
			TrivaGameServer caller = (TrivaGameServer)arg0;
			// check if the server is still running
			if(caller.isRunning())
			{
				if(caller.getMessage().groupIp.equals(groupIp))
					msgQueue.add(caller.getMessage());	// must be a message so added it to the message queue
			}
			
			else
				run = false;						// the server is done stop running
		}
		
	}
	@Override
	public void run() 
	{
		int index = 0;
		while(run)
		{
			Message msg;
			TrivaMessage receivedMessage;
			playerList.clearReceived();
			// sleep for a sec
			try 
			{
				// send the question
				if(nextQuestion || playerList.allPlayersReady())
				{
					currentProblem = questions.get(index++%questions.size());
					nextQuestion = false;
					playerList.clearPlayersReady();
					msg = createQuestionPacket();
					server.queueMessage(new TrivaMessage(groupIp, msg));
					
					while(!confirmReceived((byte)0x00, 5000))
						server.queueMessage(new TrivaMessage(groupIp, msg));	// try again if it failed
					
					unlockScreen(5000);
				}
				
				// look for the buzzer
				if((receivedMessage = processQueuedMessage()) != null && receivedMessage.message.getOpcode() == 0x01)
				{
					// buzzer was pressed start the answer sequence
					// order a freeze
					server.queueMessage(new TrivaMessage(groupIp, new OpcodeOnlyMessage(0x01)));
					
					checkBuzzer(5000);
				}
				// the user has timed out
				else if(receivedMessage != null && receivedMessage.message.getOpcode() == 0x06)
				{
					// set that, that user timed out
					playerList.playerReady(((BasicUserMessage) receivedMessage.message).getPlayerId(), true);
					server.queueMessage(new TrivaMessage(groupIp, new UserIDMessage(0x06, ((BasicUserMessage) receivedMessage.message).getPlayerId())));
				}
				// the user has left
				else if(receivedMessage != null && receivedMessage.message.getOpcode() == 0x07)
				{
					playerList.removePlayer(((BasicUserMessage) receivedMessage.message).getPlayerId());
					server.queueMessage(new TrivaMessage(groupIp, new UserIDMessage(0x06, ((BasicUserMessage) receivedMessage.message).getPlayerId())));
					run = playerList.playersLeft();
				}
				
				Thread.sleep(1);
			} 
			catch (InterruptedException e) 
			{} 
			catch (UnknownHostException e) 
			{
				e.printStackTrace();
			}	
		}
	}
	
	private void unlockScreen(long timeToWait) throws UnknownHostException, InterruptedException 
	{
		// unlock the players screen
		byte[] msg = new byte[1];
		msg[0] = (byte)0x05;
		server.queueMessage(new TrivaMessage(groupIp, new OpcodeOnlyMessage(0x05)));
		
		playerList.clearReceived();
		
		//confirm with request
		while(!confirmReceived((byte)0x05, timeToWait))
			server.queueMessage(new TrivaMessage(groupIp, new OpcodeOnlyMessage(0x05)));	// try again if it failed
		
		playerList.clearReceived();
	}

	private void checkBuzzer(long timeToWait) throws UnknownHostException, InterruptedException
	{
		UUID player = null;
		TrivaMessage msg;
		long timeStamp = -1;
		long maxWait = System.currentTimeMillis() + timeToWait;
		byte[] rawMsg = new byte[1];
		rawMsg[0] = (byte)0x02;
		
		server.queueMessage(new TrivaMessage(groupIp, new OpcodeOnlyMessage(0x01)));
		server.queueMessage(new TrivaMessage(groupIp, new OpcodeOnlyMessage(0x02)));
		
		// give some time for responses to queue up
		Thread.sleep(500);
		
		while((maxWait > System.currentTimeMillis() || !msgQueue.isEmpty()) && player == null)
		{
			maxWait = System.currentTimeMillis() + timeToWait; // reset
			// found a response
			if((msg = processQueuedMessage()) != null && msg.message.getOpcode() == 0x02)
			{
				BuzzerQueryMessage qry = ((BuzzerQueryMessage)((BasicUserMessage) msg.message));
				long userTimeStamp = qry.getTimeStamp();
				if(userTimeStamp > timeStamp)
				{
					timeStamp = userTimeStamp;
					player = qry.getPlayerId();
				}
			}
		}
		
		// allow the selected user to answer
		checkAnswer(timeToWait, player);
	}
	
	private void checkAnswer(long timeToWait, UUID userId) throws UnknownHostException, InterruptedException
	{
		TrivaMessage msg;
		UUID answer;
		long maxWait = System.currentTimeMillis() + timeToWait;
		
		if(userId == null)
			return;
		
		server.queueMessage(new TrivaMessage(groupIp, new UserIDMessage(0x03, userId)));
		
		// give some time for responses to queue up
		Thread.sleep(500);
		
		while((maxWait > System.currentTimeMillis()) || !msgQueue.isEmpty())
		{
			// found a response
			if((msg = processQueuedMessage()) != null && msg.message.getOpcode() == 0x03)
			{
				AnswerMessage ansMessage = ((AnswerMessage)((BasicUserMessage) msg.message));
				answer = ansMessage.getAnswerId();
				
				// check the answer
				if(answer.equals(currentProblem.getAnswer()))
				{
					nextQuestion = true;	// move on to the next question
					givePoints(timeToWait, userId, ansMessage.getTimeElapsed());
				}
				else
					givePoints(timeToWait, userId, -1);	// wrong answer
				break;
			}
		}
	}
	
	private void givePoints(long timeToWait, UUID userId, int timeElapsed) throws UnknownHostException, InterruptedException
	{
		int pointsReceived = 0;
		if(timeElapsed <= -1); //wrong answer
		else if(timeElapsed < 6000)
			pointsReceived = getPointMultiplier(currentProblem.getLevel()) * 100;
		else if(timeElapsed < 11000)
			pointsReceived = (int) ((int) (getPointMultiplier(currentProblem.getLevel()) * 100) * 0.75);
		else if(timeElapsed < 16000)
			pointsReceived = (int) ((int) (getPointMultiplier(currentProblem.getLevel()) * 100) * 0.50);
		else if(timeElapsed < 21000)
			pointsReceived = (int) ((int) (getPointMultiplier(currentProblem.getLevel()) * 100) * 0.25);
		else
			pointsReceived = 1;
		
		server.queueMessage(new TrivaMessage(groupIp, new CorrectAnswerMessage(0x04, userId, pointsReceived)));
		playerList.clearReceived();
		
		//confirm 
		while(!confirmReceived((byte)0x04, 5000, userId))
			server.queueMessage(new TrivaMessage(groupIp, new CorrectAnswerMessage(0x04, userId, pointsReceived)));	// try again if it failed
		
		playerList.clearReceived();
		playerList.playerReady(userId, true);	// the user had their chance to answer
	}
	
	private boolean confirmReceived(byte opcode, long timeToWait, UUID playerId) throws InterruptedException
	{
		TrivaMessage msg;
		long maxWait = System.currentTimeMillis() + timeToWait;
		
		while(!playerList.didPlayerReceive(playerId))
		{
			// hit a timeout
			if(maxWait < System.currentTimeMillis())
				return false;
			
			// read all queued messages
			while((msg = processQueuedMessage()) != null)
			{
				// check if the message is the code we are looking for
				if(msg.message.getOpcode() == opcode)
					playerList.setPlayerReceived(((BasicUserMessage) msg.message).getPlayerId(), true);
				
			}
			
			Thread.sleep(1);
		}
		
		return true;
	}
	
	private boolean confirmReceived(byte opcode, long timeToWait) throws InterruptedException 
	{
		TrivaMessage msg;
		long maxWait = System.currentTimeMillis() + timeToWait;
		// give some time for messages to show
		while(!playerList.didAllPlayersReceive())
		{
			// hit a timeout
			if(maxWait < System.currentTimeMillis())
				return false;
			
			// read all queued messages
			while((msg = processQueuedMessage()) != null)
			{
				// check if the message is the code we are looking for
				if(msg.message.getOpcode() == opcode)
					playerList.setPlayerReceived(((BasicUserMessage) msg.message).getPlayerId(), true);
				
			}
			
			Thread.sleep(1);
		}
		
		return true;
	}

	private TrivaMessage processQueuedMessage()
	{
		if(!msgQueue.isEmpty())
		{
			// get the msg and check if it is part of our group
			TrivaMessage msg = msgQueue.poll();
			if(isOurMessage(msg))
			{
				return msg;
			}
		}
		
		return null;
	}
	
	private boolean isOurMessage(TrivaMessage msg)
	{
		return msg.groupIp.equals(groupIp);
	}
	
	private Message createQuestionPacket()
	{
		String[] answers = new String[4];
		UUID[] answerIds = new UUID[4];
		List<Answer> answerList = currentProblem.getAnswers();
		
		for(int index = 0; index < 4; index++)
		{
			answers[index] = answerList.get(index).getAnswer();
			answerIds[index] = answerList.get(index).getAnswerId();
		}
		
		return new QuestionMessage(0x00, currentProblem.getQuestionId(), answerIds, 
				currentProblem.getSubject(), currentProblem.getQuestion(), currentProblem.getLevel(), answers);
	}
	
	private int getPointMultiplier(String level)
	{
		switch(level.toLowerCase())
		{
		case "med":
			return 2;
		case "hard":
			return 3;
		default:
			return 1;
		}
	}
}
