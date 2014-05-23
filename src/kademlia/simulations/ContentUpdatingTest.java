package kademlia.simulations;

import java.io.IOException;
import kademlia.dht.GetParameter;
import kademlia.JKademliaNode;
import kademlia.dht.StorageEntry;
import kademlia.exceptions.ContentNotFoundException;
import kademlia.node.KademliaId;

/**
 * Testing sending and receiving content between 2 Nodes on a network
 *
 * @author Joshua Kissoon
 * @since 20140224
 */
public class ContentUpdatingTest
{

    public static void main(String[] args)
    {
        try
        {
            /* Setting up 2 Kad networks */
            JKademliaNode kad1 = new JKademliaNode("JoshuaK", new KademliaId("ASF45678947584567467"), 7574);
            System.out.println("Created Node Kad 1: " + kad1.getNode().getNodeId());
            JKademliaNode kad2 = new JKademliaNode("Crystal", new KademliaId("ASERTKJDHGVHERJHGFLK"), 7572);
            System.out.println("Created Node Kad 2: " + kad2.getNode().getNodeId());
            kad2.bootstrap(kad1.getNode());

            /* Lets create the content and share it */
            DHTContentImpl c = new DHTContentImpl(kad2.getOwnerId(), "Some Data");
            kad2.put(c);

            /* Lets retrieve the content */
            System.out.println("Retrieving Content");
            GetParameter gp = new GetParameter(c.getKey(), DHTContentImpl.TYPE, c.getOwnerId());
            
            System.out.println("Get Parameter: " + gp);
            StorageEntry conte = kad2.get(gp);
            System.out.println("Content Found: " + new DHTContentImpl().fromSerializedForm(conte.getContent()));
            System.out.println("Content Metadata: " + conte.getContentMetadata());

            /* Lets update the content and put it again */
            c.setData("Some New Data");
            kad2.put(c);

            /* Lets retrieve the content */
            System.out.println("Retrieving Content Again");
            conte = kad2.get(gp);
            System.out.println("Content Found: " + new DHTContentImpl().fromSerializedForm(conte.getContent()));
            System.out.println("Content Metadata: " + conte.getContentMetadata());

        }
        catch (IOException | ContentNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
