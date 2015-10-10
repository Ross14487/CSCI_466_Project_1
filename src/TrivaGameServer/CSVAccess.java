package TrivaGameServer;

import java.util.ArrayList;
import java.util.List;

public class CSVAccess implements TrivaDataAccessInterface 
{
	private static List<Problem> problems = new ArrayList<>();
	private static CSVAccess instance = new CSVAccess();
	
	private CSVAccess()
	{}
	
	static CSVAccess getInstance()
	{
		return instance;
	}
	
	static boolean loadFile(String file)
	{
		return false;
	}
	
	@Override
	public List<Problem> getProblems() 
	{
		return problems;
	}

}
