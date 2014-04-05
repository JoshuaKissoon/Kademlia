package kademlia.tests;

import java.util.List;
import kademlia.Kademlia;
import kademlia.dht.GetParameter;
import kademlia.dht.StorageEntry;
import kademlia.node.NodeId;

/**
 * Testing the save and retrieve state operations.
 * Here we also try to look for content on a restored node
 *
 * @author Joshua Kissoon
 * @since 20140309
 */
public class SaveStateTest2
{

    public SaveStateTest2()
    {
        try
        {
            /* Setting up 2 Kad networks */
            Kademlia kad1 = new Kademlia("JoshuaK", new NodeId("ASF45678947584567463"), 12049);
            Kademlia kad2 = new Kademlia("Crystal", new NodeId("ASF45678947584567464"), 4585);

            /* Connecting 2 to 1 */
            System.out.println("Connecting Nodes 1 & 2");
            kad2.bootstrap(kad1.getNode());
            System.out.println(kad1);
            System.out.println(kad2);

            DHTContentImpl c;
            synchronized (this)
            {
                System.out.println("\n\n\n\nSTORING CONTENT 1\n\n\n\n");
                c = new DHTContentImpl(kad2.getOwnerId(), "Some Data");
                System.out.println(c);
                kad1.putLocally(c);
            }

            System.out.println(kad1);
            System.out.println(kad2);

            /* Shutting down kad1 and restarting it */
            System.out.println("\n\n\nShutting down Kad 1 instance");
            kad1.shutdown(true);

            System.out.println("\n\n\nReloading Kad instance from file");
            kad1 = Kademlia.loadFromFile("JoshuaK");
            kad1.bootstrap(kad2.getNode());
            System.out.println(kad2);

            /* Trying to get a content stored on the restored node */
            GetParameter gp = new GetParameter(c.getKey(), kad2.getOwnerId(), c.getType());
            List<StorageEntry> content = kad2.get(gp, 1);

            if (!content.isEmpty())
            {
                DHTContentImpl cc = new DHTContentImpl().fromBytes(content.get(0).getContent());
                System.out.println("Content received: " + cc);
            }
            else
            {
                System.out.println("No Content found");
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        new SaveStateTest2();
    }
}
