package TrivaGameClient;

import java.io.IOException;
import TrivaGameClient.InitialDisplay;
import TrivaGameServer.TCP_Sock;

public class TriviaGameDriver
{
	static final int registerPort = 3025;
	
    public static void main(String[] args) throws IOException{
    	
        InitialDisplay initDisp = new InitialDisplay(new RegistrationSystem(new UserRegistrationService(registerPort, new TCP_Sock(registerPort, false))));

        initDisp.go();
        initDisp.showWindow();
        
        }//main
    
}//TriviaGameDriver
