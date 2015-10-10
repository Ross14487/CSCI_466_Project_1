package TrivaGameServer;

/*************************************************************************
 * Name        :  Ross Mitchell 
 * email       :  RPMitchell@mtech.edu
 * Date        :  10-8-2012
 * Description :  This is the Answer Class for CSCI-466 Networks Project 1,
 * Trivia Game Server.  This class is used with the Problem Class to keep
 * track of the correct answer from a database of questions/answers.
 *      
 *************************************************************************/


import java.util.UUID;
public class Answer
{
    private UUID answerId;
    private String answer;
    public Answer(String ans)
    {
        answer = ans;
        answerId = UUID.randomUUID();        
    }//constructor
    public String getAnswer()
    {
        return answer;
    }//getAnswer
    public UUID getAnswerId()
    {
        return answerId;
    }//getAnswerId
}//Answer class
