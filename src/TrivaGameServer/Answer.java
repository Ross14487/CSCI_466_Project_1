package TrivaGameServer;

import java.util.UUID;

public class Answer 
{
	private String answer;
	private UUID answerId;
	
	/* Getters */
	public String getAnswer()
	{
		return answer;
	}
	
	public UUID getAnswerId()
	{
		return answerId;
	}
	
	public Answer(String answer)
	{
		this.answer = answer;
		answerId = UUID.randomUUID();
	}
}
