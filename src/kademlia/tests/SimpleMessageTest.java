package kademlia.tests;

import java.io.IOException;
import kademlia.Kademlia;
import kademlia.message.SimpleMessage;
import kademlia.node.NodeId;
import kademlia.message.SimpleReceiver;

/**
 * Test 1: Try sending a simple message between nodes
 *
 * @author Joshua Kissoon
 * @created 20140218
 */
public class SimpleMessageTest
{

    public static void main(String[] args)
    {
        try
        {
            Kademlia kad1 = new Kademlia("Joshua", new NodeId("12345678901234567890"), 7574);
            Kademlia kad2 = new Kademlia("Crystal", new NodeId("12345678901234567891"), 7572);

            kad1.getServer().sendMessage(kad2.getNode(), new SimpleMessage("Some Message"), new SimpleReceiver());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
