/*************************************************************************
 * Name        :  Ross Mitchell 
 * email       :  RPMitchell@mtech.edu
 * Date        :  10-8-2012
 * Description :  This is the Problem Class for CSCI-466 Networks Project 1,
 * Trivia Game Server.  This class will take the answers from a database of 
 * questions/answers and creates an object for each one.  The 'getter' for
 * the answers will scramble the order of the answers to ensure we do not 
 * have patterns developing that would allow the players to deduce the answer
 * based on their relative position on the display. 
 *      
 *************************************************************************/

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Random;
import java.lang.Math;

public class Problem
{
    private UUID answer;
    private int questionId;
    private String subject, question, level;
    private Random generator;
    int randomIndex;
    
    List<Answer> selectableAnswers = new ArrayList<Answer>();
    List<Answer> inOrderSelectableAnswers = new ArrayList<Answer>();
    private Problem(int quesId, String[] arguments)
    {
        questionId = quesId;
        subject = arguments[0];
        question = arguments[1];
        for (int i = 2; i <= 5; i++)
        {
            inOrderSelectableAnswers.add(new Answer(arguments[i]));
        }
        level = arguments[6];
        answer = inOrderSelectableAnswers.get(0).getAnswerId();
    }//constructor
    
    private int getRandomIndex()
    {
        generator = new Random(System.currentTimeMillis());
        int randomIndex = generator.nextInt(3) + 1;
        return randomIndex;
    }//randomIndexGenerator
    
    public int getQuestionId()
    {
        return questionId;
    }//getQuestionId
    
    public String getSubject()
    {
        return subject;
    }//getSubject
    
    public String getQuestion()
    {
       return question; 
    }//getQuestion
    
    public List<Answer> getAnswers()
    {
        int [] indexArray = new int[4];
        for (int i = 0; i < 4; i++)
        {
           indexArray[i] = -1;
        }//for
        
        int count = 0;
        while (count < 5)
        {
            int index = getRandomIndex();
            if (indexArray[index] == -1)
            {
                indexArray[index] = count;
                count ++;
            }//if
        }//while count
        for (int i = 0; i < 4; i++)
        {
            selectableAnswers.add(inOrderSelectableAnswers.get(indexArray[i]));
        }//for
      return selectableAnswers;
    }//getAnswers
    
    public String getLevel()
    {
        return level;
    }//getLevel
    
    public UUID getAnswer()
    {
        return answer;
    }//UUID   
}// end class Problem
