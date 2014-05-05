package kademlia.message;

import java.io.IOException;

/**
 * Default receiver if none other is called
 *
 * @author Joshua Kissoon
 * @created 20140202
 */
public class SimpleReceiver implements Receiver
{

    @Override
    public void receive(Message incoming, int conversationId)
    {
        
    }

    @Override
    public void timeout(int conversationId) throws IOException
    {
        
    }
}
