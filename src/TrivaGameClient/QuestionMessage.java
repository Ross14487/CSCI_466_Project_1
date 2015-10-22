package TrivaGameClient;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

public class QuestionMessage implements Message 
{
	private byte[] rawMsg;
	private int questionId;
	private UUID[] answerIds;
	private String category, question, difficulty;
	private String[] answers;
	
	public QuestionMessage(byte[] rawMsg)
	{
		this.rawMsg = rawMsg;           
		String[] csv = new String(Arrays.copyOfRange(rawMsg, 69, rawMsg.length), StandardCharsets.UTF_8).split("#");
		ByteBuffer buffer = ByteBuffer.wrap(Arrays.copyOfRange(rawMsg, 1, rawMsg.length));
		questionId = buffer.getInt();
		
		answerIds = new UUID[4];
		for(int x=0; x<4; x++)
			answerIds[x] = new UUID(buffer.getLong(), buffer.getLong());
		
		answers = new String[4];
		
		category = csv[0];
		question = csv[1];
		
		for(int x=0; x<4; x++)
			answers[x] = csv[x+2];
		
		difficulty = csv[6];
	}
	
	public QuestionMessage(int opcode, int questionId, UUID[] answerIds, String category, String question, String difficulty, String[] answers)
	{
	    String strMsg = "";
		String[] csv = new String[7];
		ByteBuffer buffer = ByteBuffer.wrap(new byte[69]);
		
		this.questionId = questionId;
		this.category = category;
		this.question = question;
		this.answers = answers;
		this.answerIds = answerIds;
		this.difficulty = difficulty;
		
		buffer.put((byte) opcode);
		buffer.putInt(questionId);
		
		for(int x=0; x<4; x++)
		{
			buffer.putLong(answerIds[x].getMostSignificantBits());
			buffer.putLong(answerIds[x].getLeastSignificantBits());
		}
		
		csv[0] = category;
		csv[1] = question;
		
		for(int x=0; x<4; x++)
			csv[x+2] = answers[x];
		
		csv[6] = difficulty;
		
		strMsg = String.join("#", csv);
		
		rawMsg = new byte[buffer.array().length + strMsg.length()];
		ByteBuffer msgBuffer = ByteBuffer.wrap(rawMsg);
		
		msgBuffer.put(buffer.array());
		msgBuffer.put(strMsg.getBytes(StandardCharsets.UTF_8));
	}
	
	public int getQuestionId()
	{
		return questionId;
	}
	
	public UUID[] getAnswersId()
	{
		return answerIds;
	}
	
	public String getCategory()
	{
		return category;
	}
	
	public String getQuestion()
	{
		return question;
	}
	
	public String[] getAnswers()
	{
		return answers;
	}
	
	public String getDifficulty()
	{
		return difficulty;
	}
	
	@Override
	public byte[] getMessage() 
	{
		return rawMsg;
	}

	@Override
	public int getOpcode() 
	{
		return rawMsg[0];
	}
}
