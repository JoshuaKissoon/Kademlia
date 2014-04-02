package kademlia.tests;

import java.io.IOException;
import java.util.List;
import kademlia.core.GetParameter;
import kademlia.Kademlia;
import kademlia.dht.StorageEntry;
import kademlia.node.NodeId;

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
            Kademlia kad1 = new Kademlia("JoshuaK", new NodeId("ASF45678947584567467"), 7574);
            Kademlia kad2 = new Kademlia("Crystal", new NodeId("ASERTKJDHGVHERJHGFLK"), 7572);
            kad2.bootstrap(kad1.getNode());

            /* Lets create the content and share it */
            DHTContentImpl c = new DHTContentImpl(kad2.getOwnerId(), "Some Data");
            kad2.put(c);

            /* Lets retrieve the content */
            GetParameter gp = new GetParameter(c.getKey(), DHTContentImpl.TYPE);
            gp.setType(DHTContentImpl.TYPE);
            gp.setOwnerId(c.getOwnerId());
            List<StorageEntry> conte = kad2.get(gp, 1);

            kad2.refresh();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
