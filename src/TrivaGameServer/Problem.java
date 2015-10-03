package TrivaGameServer;

import java.util.List;
import java.util.UUID;

public class Problem 
{
	private int questionId;
	private String subject;
	private String question;
	private UUID answer;
	private List<Answer> selectableAnswers;
	private String level;
	
	/*Getters*/
	public int getQuestionId()
	{
		return questionId;
	}
	
	public String getSubject()
	{
		return subject;
	}
	
	public String getQuestion()
	{
		return question;
	}
	
	public UUID getAnswer()
	{
		return answer;
	}
	
	public List<Answer> getSelectableAnswers()
	{
		return selectableAnswers;
	}
	
	public String getQuestionLevel()
	{
		return level;
	}
	
	public Problem(int questionId, String[] args) throws Exception
	{
		// check for a valid number of arguments
		if(args.length != 7)
			throw new Exception("Invalid Number of Arguments");
		
		this.questionId = questionId;
		subject = args[0];
		question = args[1];
		
		// load the possible answers
		for(int index = 2; index < 6; index++)
			selectableAnswers.add(new Answer(args[index]));
		
		level = args[6];
		
		// set the answer
		answer = selectableAnswers.get(0).getAnswerId();
	}
}
