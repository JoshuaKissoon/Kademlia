package kademlia.tests;

import kademlia.KademliaNode;
import kademlia.dht.GetParameter;
import kademlia.dht.StorageEntry;
import kademlia.node.KademliaId;

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
            KademliaNode kad1 = new KademliaNode("JoshuaK", new KademliaId("ASF45678947584567463"), 12049);
            KademliaNode kad2 = new KademliaNode("Crystal", new KademliaId("ASF45678947584567464"), 4585);

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
            kad1 = KademliaNode.loadFromFile("JoshuaK");
            kad1.bootstrap(kad2.getNode());
            System.out.println(kad2);

            /* Trying to get a content stored on the restored node */
            GetParameter gp = new GetParameter(c.getKey(), kad2.getOwnerId(), c.getType());
            StorageEntry content = kad2.get(gp);
            DHTContentImpl cc = new DHTContentImpl().fromSerializedForm(content.getContent());
            System.out.println("Content received: " + cc);
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
