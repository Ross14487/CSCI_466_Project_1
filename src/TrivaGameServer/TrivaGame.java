package TrivaGameServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;

public class TrivaGame implements Observer, Runnable 
{
	private Problem currentProblem;
	private TrivaGameServer server;
	private ActivePlayerList playerList;
	private List<Problem> questions;
	private InetAddress groupIp;
	private boolean nextQuestion = true;
	private volatile boolean run = true;
	private Queue<TrivaMessage> msgQueue = new LinkedList<TrivaMessage>();
	private Random randomGenerator = new Random();
	
	public TrivaGame(TrivaGameServer server, ActivePlayerList playerList, List<Problem> questions)
	{
		this.server = server;
		this.playerList = playerList;
		this.questions = questions;
		this.groupIp = playerList.getGroupAddress();
	}

	@Override
	public synchronized void update(Observable arg0, Object arg1) 
	{
		if(arg0 instanceof TrivaGameServer)
		{
			TrivaGameServer caller = (TrivaGameServer)arg0;
			// check if the server is still running
			if(caller.isRunning())
				msgQueue.add(caller.getMessage());	// must be a message so added it to the message queue
			
			else
				run = false;						// the server is done stop running
		}
		
	}
	@Override
	public void run() 
	{
		while(run)
		{
			byte[] msg;
			TrivaMessage receivedMessage;
			playerList.clearReceived();
			// sleep for a sec
			try 
			{
				// send the question
				if(nextQuestion)
				{
					nextQuestion = false;
					msg = createQuestionPacket();
					server.queueMessage(new TrivaMessage(groupIp, msg, true));
					
					while(!confirmReceived((byte)0x00, 5000))
						server.queueMessage(new TrivaMessage(groupIp, msg, true));	// try again if it failed
				}
				
				unlockScreen(5000);
				
				// look for the buzzer
				if((receivedMessage = processQueuedMessage()) != null && receivedMessage.opcode == 0x01)
				{
					// buzzer was pressed start the answer sequence
					// order a freeze
					msg = new byte[1];
					msg[0] = (byte)0x01;
					server.queueMessage(new TrivaMessage(groupIp, msg, true));
					
					checkBuzzer(5000);
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
		server.queueMessage(new TrivaMessage(groupIp, msg, true));
		
		playerList.clearReceived();
		
		//confirm with request
		while(!confirmReceived((byte)0x05, timeToWait))
			server.queueMessage(new TrivaMessage(groupIp, msg, true));	// try again if it failed
		
		playerList.clearReceived();
	}

	private void checkBuzzer(long timeToWait) throws UnknownHostException, InterruptedException
	{
		UUID player = null;
		TrivaMessage msg;
		int timeStamp = -1;
		long maxWait = System.currentTimeMillis() + timeToWait;
		byte[] rawMsg = new byte[1];
		rawMsg[0] = (byte)0x02;
		
		server.queueMessage(new TrivaMessage(groupIp, rawMsg, true));
		
		while(maxWait > System.currentTimeMillis() || !msgQueue.isEmpty())
		{
			// found a response
			if((msg = processQueuedMessage()) != null && msg.opcode == 0x02)
			{
				int userTimeStamp = ByteBuffer.wrap(msg.message).getInt();
				if(userTimeStamp > timeStamp)
				{
					timeStamp = userTimeStamp;
					player = msg.userID;
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
		ByteBuffer rawMsg = ByteBuffer.wrap(new byte[17]);
		long maxWait = System.currentTimeMillis() + timeToWait;
		
		if(userId == null)
			return;
		
		rawMsg.put((byte)0x03);
		rawMsg.putLong(userId.getMostSignificantBits());
		rawMsg.putLong(userId.getLeastSignificantBits());
		
		while(maxWait > System.currentTimeMillis() || !msgQueue.isEmpty())
		{
			// found a response
			if((msg = processQueuedMessage()) != null && msg.opcode == 0x03)
			{
				rawMsg = ByteBuffer.wrap(msg.message);
				answer = new UUID(rawMsg.getLong(), rawMsg.getLong());
				
				// check the answer
				if(answer.equals(currentProblem.getAnswer()))
				{
					nextQuestion = true;	// move on to the next question
					givePoints(timeToWait, userId, rawMsg.getInt());
				}
				else
					givePoints(timeToWait, userId, -1);	// wrong answer
				break;
			}
		}
	}
	
	private void givePoints(long timeToWait, UUID userId, int timeElapsed) throws UnknownHostException, InterruptedException
	{
		ByteBuffer rawMsg = ByteBuffer.wrap(new byte[21]);
		int pointsReceived = 0;
		if(timeElapsed <= -1); //wrong answer
		else if(timeElapsed < 6)
			pointsReceived = getPointMultiplier(currentProblem.getLevel()) * 100;
		else if(timeElapsed < 11)
			pointsReceived = (int) ((int) (getPointMultiplier(currentProblem.getLevel()) * 100) * 0.75);
		else if(timeElapsed < 16)
			pointsReceived = (int) ((int) (getPointMultiplier(currentProblem.getLevel()) * 100) * 0.50);
		else if(timeElapsed < 21)
			pointsReceived = (int) ((int) (getPointMultiplier(currentProblem.getLevel()) * 100) * 0.25);
		else
			pointsReceived = 1;
		
		// build the packet
		rawMsg.put((byte)0x04);
		rawMsg.putLong(userId.getMostSignificantBits());
		rawMsg.putLong(userId.getLeastSignificantBits());
		rawMsg.putInt(pointsReceived);
		
		server.queueMessage(new TrivaMessage(groupIp, rawMsg.array(), true));
		playerList.clearReceived();
		
		//confirm 
		while(!confirmReceived((byte)0x04, 5000))
			server.queueMessage(new TrivaMessage(groupIp, rawMsg.array(), true));	// try again if it failed
		
		playerList.clearReceived();
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
				if(msg.opcode == opcode)
					playerList.setPlayerReceived(msg.userID, true);
				
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
	
	private byte[] createQuestionPacket()
	{
		byte[] rawPacket;
		ByteBuffer buffer;
		List<String> csv = new ArrayList<String>();
		// get a random question from the list
		currentProblem = questions.get(randomGenerator.nextInt(questions.size()));
		
		// populate some of the CSV to be sent
		csv.add(currentProblem.getQuestion());
		csv.add(currentProblem.getSubject());
		csv.add(currentProblem.getLevel());
		
		// create a random question packet
		List<Byte> packet = new ArrayList<Byte>();
		packet.add((byte)0x00);

		// add the question ID
		buffer = ByteBuffer.allocate(4).putInt(currentProblem.getQuestionId());
		for(byte b : buffer.array())
			packet.add(b);
		
		// add the answers UUID
		for(Answer a : currentProblem.getAnswers())
		{
			csv.add(a.getAnswer());
			buffer = ByteBuffer.allocate(16);
			buffer.putLong(a.getAnswerId().getMostSignificantBits());
			buffer.putLong(a.getAnswerId().getLeastSignificantBits());
			
			for(byte b : buffer.array())
				packet.add(b);
		}
		
		// create the csv and store it in the packet
		for(byte letter : String.join(",", csv).getBytes(StandardCharsets.UTF_8))
			packet.add(letter);
		
		// create the raw array
		int index = 0;
		rawPacket = new byte[packet.size()];
		for(byte b : packet)
			rawPacket[index++] = b;
		
		return rawPacket;
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
