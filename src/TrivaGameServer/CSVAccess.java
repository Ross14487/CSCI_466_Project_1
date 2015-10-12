package TrivaGameServer;

/*************************************************************************
 * Name        :  Josh Lee and Ross Mitchell 
 * email       :  JELee@mtech.edu RPMitchell@mtech.edu
 * Date        :  10-9-2012
 * Description :  This is the CSVAccess Class for CSCI-466 Networks Project 1,
 * Trivia Game Server.  This class reads in the file from from a database of 
 * questions/answers and creates an object.   
 *      
 *************************************************************************/


import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CSVAccess implements TrivaDataAccessInterface 
{
    private static List<Problem> problems = new ArrayList<Problem>();
	private static CSVAccess instance = new CSVAccess();
	
	private CSVAccess()
	{}
	
    public static CSVAccess getInstance()
	{
		return instance;
	}
	
//	public void setProblem(Problem problemInput)
//	{
//	    this.problem = problemInput;
//	}
	@SuppressWarnings("null")
    public static boolean loadFile(String fileName)
	{
	    boolean success = false;
        String[] theRestOfLine = null; 
		try
		{

		    for (String line:Files.readAllLines(Paths.get(fileName), Charset.defaultCharset()))
		    {
		        String arr[] = line.split(",");
		        String firstWord = arr[0]; 
		        int probNum = Integer.parseInt(firstWord);
		        for (int i = 1; i < arr.length; i ++) 
		        {
		            theRestOfLine[i] = arr[i];
		        }
		        Problem problem = new Problem(probNum, theRestOfLine);   
		        problems.add(problem);
		    }
		    success = true;
		}
		catch (IOException e)
		{
		    e.printStackTrace();
		}
	    
	    return success;
	}
	
	@Override
	public List<Problem> getProblems() 
	{
		return problems;
	}

}
