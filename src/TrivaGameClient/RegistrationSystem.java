package TrivaGameClient;

import java.net.UnknownHostException;

public class RegistrationSystem
{
    private ServiceInterface service;
    private String errorMessage;
    
    public RegistrationSystem(ServiceInterface service)
    {
       this.service = service;
    }
    
    private Message RegisterUser(String userName) throws UnknownHostException, IllegalArgumentException
    {
       service.sendMessage(new RegisterUserMessage(0x01, userName));
       return WaitForMessage();
    }
    
    
    
    private Message WaitForMessage()
    {
        Message msg = null;
        while ((msg = service.getQueuedMessage()) == null);
        return msg;
    }
    
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
    }
    
    private boolean DeRegisterUser(UUID userID) throws UnknownHostException, IllegalArgumentException
    {
        boolean success = false;
        Message msg = null;
        service.sendMessage(new UserIDMessage(0x02, userID));
        msg = WaitForMessage();
        if(!((StatusMessage)msg).getSuccess())
            errorMessage = ((StatusMessage)msg).getErrorMessage();
        return ((StatusMessage)msg).getSuccess();
    }
}
