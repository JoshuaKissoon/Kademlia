package kademlia.tests;

import java.io.IOException;
import kademlia.core.Kademlia;
import kademlia.node.NodeId;

/**
 * Testing sending and receiving content between 2 Nodes on a network
 *
 * @author Joshua Kissoon
 * @since 20140224
 */
public class ContentSendingTest
{

    public static void main(String[] args)
    {
        try
        {
            /* Setting up 2 Kad networks */
            Kademlia kad1 = new Kademlia("JoshuaK", new NodeId("ASF45678947584567467"), 7574);
            System.out.println("Created Node Kad 1: " + kad1.getNode().getNodeId());
            Kademlia kad2 = new Kademlia("Crystal", new NodeId("ASERTKJDHGVHERJHGFLK"), 7572);
            System.out.println("Created Node Kad 2: " + kad2.getNode().getNodeId());
            kad2.connect(kad1.getNode());

            /**
             * Lets create the content and share it
             */
            DHTContentImpl c = new DHTContentImpl(kad2.getOwnerId(), "Some Data");
            kad2.put(c);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
