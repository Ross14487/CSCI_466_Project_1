package TrivaGameClient;

import java.net.UnknownHostException;

import Message;

public class TriviaGame
{
    private ServiceInterface service; 
    
    public TriviaGame(ServiceInterface service)
    {
        this.service = service;
    }
    
    private Message WaitForMessage()
    {
        Message msg = null;
        while ((msg = service.getQueuedMessage()) == null);
        return msg;
    }//WaitForMessage
    
    switch (WaitForMessage())
    {
        case 0x00:
            //get questions and ??
            service.sendMessage(new BasicUserMessage(0x00, groupIP, UUID));
            break;
        case 0x01:
             //receiving a buzzer ...service.sendMessage(new )      
    }
    
 
}
