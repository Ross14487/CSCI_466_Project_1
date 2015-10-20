package TrivaGameClient;


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
    
    public RegistrationSystem(ServiceInterface service)
    {
       this.service = service;
    }//RegistrationSystem
    
    private Message RegisterUser(String userName) throws UnknownHostException, IllegalArgumentException
    {
       service.sendMessage(new RegisterUserMessage(0x01, userName));
       return WaitForMessage();
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
    
    private boolean DeRegisterUser(UUID userID) throws UnknownHostException, IllegalArgumentException
    {
        Message msg = null;
        service.sendMessage(new UserIDMessage(0x02, userID));
        msg = WaitForMessage();
        if(!((StatusMessage)msg).getSuccess())
            errorMessage = ((StatusMessage)msg).getErrorMessage();
        return ((StatusMessage)msg).getSuccess();
    }//DeRegisterUser
    
    private boolean Ready (UUID userID) throws UnknownHostException, IllegalArgumentException
    {
        Message msg = null;
        service.sendMessage(new UserIDMessage(0x03, userID));
        msg = WaitForMessage();
        if(!((StatusMessage)msg).getSuccess())
            errorMessage = ((StatusMessage)msg).getErrorMessage();
        return ((StatusMessage)msg).getSuccess();
    }//Ready
    
    private boolean NotReady (UUID userID) throws UnknownHostException, IllegalArgumentException
    {
        Message msg = null;
        service.sendMessage(new UserIDMessage(0x04, userID));
        msg = WaitForMessage();
        if(!((StatusMessage)msg).getSuccess())
            errorMessage = ((StatusMessage)msg).getErrorMessage();
        return ((StatusMessage)msg).getSuccess();
    }//NotReady
    
    public String getErrorMessage()
    {
        return errorMessage;
    }//getErrorMessage
}//end RegistrationSystem class
