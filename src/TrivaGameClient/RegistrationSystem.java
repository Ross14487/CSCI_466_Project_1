package TrivaGameClient;


import java.net.InetAddress;
/******************************************************************************
 * Name        :  Ross Mitchell 
 * email       :  RPMitchell@mtech.edu
 * Date        :  10-19-2012
 * Description :  This is the RegistrationSystem Class for CSCI-466 Networks 
 * Project 1, Trivia Game Server.  This class checks if the server is open,
 * registers the player, deregister the player, and reports ready or not ready.
 *      
 ****************************************************************************/
import java.net.UnknownHostException;
import java.util.UUID;

public class RegistrationSystem
{
    private ServiceInterface service;
    private String errorMessage;
    private Message message;
    private UUID playerID;
    
    public RegistrationSystem(ServiceInterface service)
    {
    	this.service = service;
    }//Constructor
    
    public void setAddress(String addr) throws UnknownHostException
    {
    	((UserRegistrationService)service).setAddress(addr);
    }
    
    private Message RegisterUser(String userName) throws UnknownHostException, IllegalArgumentException
    {
       service.sendMessage(new RegisterUserMessage(0x01, userName));
       Message msg = WaitForMessage();
       return msg;
    }//RegisterUser
      
    private Message WaitForMessage()
    {
        Message msg = null;
        while ((msg = service.getQueuedMessage()) == null);
        return msg;
    }//WaitForMessage
    
    public Message Registration (String userName) throws UnknownHostException, IllegalArgumentException
    {
        Message msg;
        service.sendMessage(new OpcodeOnlyMessage(0x00));
        msg = WaitForMessage();
        if(((StatusMessage)msg).getSuccess())
        {
            return RegisterUser(userName); 
        }
        return msg;
    }//Registration
    
    public boolean DeRegisterUser(UUID userID) throws UnknownHostException, IllegalArgumentException
    {
        Message msg = null;
        service.sendMessage(new UserIDMessage(0x02, userID));
        msg = WaitForMessage();
        if(!((StatusMessage)msg).getSuccess())
            errorMessage = ((StatusMessage)msg).getErrorMessage();
        return ((StatusMessage)msg).getSuccess();
    }//DeRegisterUser
    
    public boolean Ready (UUID userID) throws UnknownHostException, IllegalArgumentException
    {
        return service.sendMessage(new UserIDMessage(0x03, userID));
    }//Ready
    
    public boolean NotReady (UUID userID) throws UnknownHostException, IllegalArgumentException
    {
        return service.sendMessage(new UserIDMessage(0x04, userID));
    }//NotReady
    
    public void setPlayerID(UUID playerID)
    {
       this.playerID = playerID; 
    }
    public UUID getPlayerID()
    {
        return playerID;
    }
    
    public String getErrorMessage()
    {
        return errorMessage;
    }//getErrorMessage
}//end RegistrationSystem class
