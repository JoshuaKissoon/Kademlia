package kademlia.simulations;

import java.io.IOException;
import kademlia.KademliaNode;
import kademlia.message.SimpleMessage;
import kademlia.node.KademliaId;
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
            KademliaNode kad1 = new KademliaNode("Joshua", new KademliaId("12345678901234567890"), 7574);
            KademliaNode kad2 = new KademliaNode("Crystal", new KademliaId("12345678901234567891"), 7572);

            kad1.getServer().sendMessage(kad2.getNode(), new SimpleMessage("Some Message"), new SimpleReceiver());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
