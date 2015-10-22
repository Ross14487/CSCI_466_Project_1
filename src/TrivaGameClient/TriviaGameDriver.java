package TrivaGameClient;

import java.io.IOException;
import java.net.InetAddress;
import TrivaGameClient.GamePlayDisplay;
import TrivaGameClient.InitialDisplay;
import TrivaGameServer.TCP_Sock;
import TrivaGameServer.UDP_Sock;

public class TriviaGameDriver
{
	static final int registerPort = 3025;
	
    public static void main(String[] args) throws IOException{
        
        InitialDisplay initDisp = new InitialDisplay(new RegistrationSystem(new UserRegistrationService(registerPort, new TCP_Sock(registerPort, false))));
        GamePlayDisplay gameDisp = null;
        initDisp.go();
            while(true){
            
                
                String playerName = initDisp.getPlayerName();
                if(playerName == ""){
                    playerName = "Anonymous";
                }//if player name is empty
                String serverIP = initDisp.getServerIP();
                boolean isReg = initDisp.getIsRegistered();
                boolean isRead = initDisp.getIsReady();
                
                //TODO need to be sure the serverIP is valid
                if(isRead && isReg && serverIP != ""){
                    
                        initDisp.hideWindow();
                        gameDisp = new GamePlayDisplay(new TriviaGame(new TrivaGameService(serverIP, initDisp.getGroupIp(), initDisp.getPortNum(), new UDP_Sock(initDisp.getPortNum(), true)), InetAddress.getByName(initDisp.getGroupIp()), initDisp.getPlayerId(), initDisp.getPortNum()));
                        gameDisp.go();
                        gameDisp.updateName(playerName);
                        gameDisp.updateScore(0);
                        boolean noQuit = true;
                    
                        while(noQuit){
                        
                            while(noQuit){
                                if(gameDisp.getBuzzed()){
                                    int answer = gameDisp.getAnswerChosen();
                                    break;
                                }//if player buzzed
                                noQuit = !gameDisp.getQuit();
                            }//while noQuit
                            
                            noQuit = !gameDisp.getQuit();
                            if(!noQuit){
                                initDisp.resetVals();
                                gameDisp.resetVals();
                                initDisp.go();
                            }//if not noQuit
                    
                        }//while noQuit
                    
                        
                        
                }//if isRead, isReg, player name is not empty, and serverIP is not empty
    
                if(gameDisp != null && gameDisp.getQuit()){
                    initDisp.resetVals();
                }//if
                initDisp.showWindow();
                
                //TODO if initDisp gets closed out, break
                
            }//while true
        
        }//main
    
}//TriviaGameDriver
