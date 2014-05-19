package kademlia.simulations;

import java.io.IOException;
import kademlia.dht.GetParameter;
import kademlia.KademliaNode;
import kademlia.dht.StorageEntry;
import kademlia.exceptions.ContentNotFoundException;
import kademlia.node.KademliaId;

/**
 * Testing sending and receiving content between 2 Nodes on a network
 *
 * @author Joshua Kissoon
 * @since 20140224
 */
public class RefreshOperationTest
{

    public static void main(String[] args)
    {
        try
        {
            /* Setting up 2 Kad networks */
            KademliaNode kad1 = new KademliaNode("JoshuaK", new KademliaId("ASF45678947584567467"), 7574);
            KademliaNode kad2 = new KademliaNode("Crystal", new KademliaId("ASERTKJDHGVHERJHGFLK"), 7572);
            kad2.bootstrap(kad1.getNode());

            /* Lets create the content and share it */
            DHTContentImpl c = new DHTContentImpl(kad2.getOwnerId(), "Some Data");
            kad2.put(c);

            /* Lets retrieve the content */
            GetParameter gp = new GetParameter(c.getKey(), DHTContentImpl.TYPE);
            gp.setType(DHTContentImpl.TYPE);
            gp.setOwnerId(c.getOwnerId());
            StorageEntry conte = kad2.get(gp);

            kad2.refresh();
        }
        catch (IOException | ContentNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
