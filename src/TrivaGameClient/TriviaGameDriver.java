package TCPTestClient;

import TrivaGameClient.GamePlayDisplay;
import TrivaGameClient.InitialDisplay;

public class TriviaGameDriver
{
    public static void main(String[] args){
        
        InitialDisplay initDisp = new InitialDisplay();
        GamePlayDisplay gameDisp = new GamePlayDisplay();
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
    
                if(gameDisp.getQuit()){
                    initDisp.resetVals();
                }//if
                initDisp.showWindow();
                
                //TODO if initDisp gets closed out, break
                
            }//while true
        
        }//main
    
}//TriviaGameDriver
