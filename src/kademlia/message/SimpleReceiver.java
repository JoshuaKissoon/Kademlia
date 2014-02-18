/**
 * @author Joshua
 * @created
 * @desc
 */
package kademlia.message;

import java.io.IOException;
import kademlia.message.Message;
import kademlia.operation.Receiver;

public class SimpleReceiver implements Receiver
{

    @Override
    public void receive(Message incoming, int conversationId)
    {
        System.out.println("Received message: " + incoming);
    }

    @Override
    public void timeout(int conversationId) throws IOException
    {
        System.out.println("");
    }
}
