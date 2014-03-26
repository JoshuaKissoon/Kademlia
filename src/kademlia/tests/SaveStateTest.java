package kademlia.tests;

import kademlia.core.Kademlia;
import kademlia.node.NodeId;

/**
 * Testing the save and retrieve state operations
 *
 * @author Joshua Kissoon
 * @since 20140309
 */
public class SaveStateTest
{

    public SaveStateTest()
    {
        try
        {
            /* Setting up 2 Kad networks */
            Kademlia kad1 = new Kademlia("JoshuaK", new NodeId("ASF45678947584567463"), 12049);
            Kademlia kad2 = new Kademlia("Crystal", new NodeId("ASF45678947584567464"), 4585);
            Kademlia kad3 = new Kademlia("Shameer", new NodeId("ASF45678947584567465"), 8104);
            Kademlia kad4 = new Kademlia("Lokesh", new NodeId("ASF45678947584567466"), 8335);
            Kademlia kad5 = new Kademlia("Chandu", new NodeId("ASF45678947584567467"), 13345);

            /* Connecting 2 to 1 */
            System.out.println("Connecting Nodes 1 & 2");
            kad2.bootstrap(kad1.getNode());
            System.out.println(kad1);
            System.out.println(kad2);

            kad3.bootstrap(kad2.getNode());
            System.out.println(kad1);
            System.out.println(kad2);
            System.out.println(kad3);

            kad4.bootstrap(kad2.getNode());
            System.out.println(kad1);
            System.out.println(kad2);
            System.out.println(kad3);
            System.out.println(kad4);

            kad5.bootstrap(kad4.getNode());

            System.out.println(kad1);
            System.out.println(kad2);
            System.out.println(kad3);
            System.out.println(kad4);
            System.out.println(kad5);

            synchronized (this)
            {
                System.out.println("\n\n\n\nSTORING CONTENT 1\n\n\n\n");
                DHTContentImpl c = new DHTContentImpl(kad2.getOwnerId(), "Some Data");
                System.out.println(c);
                kad2.put(c);
            }

            synchronized (this)
            {
                System.out.println("\n\n\n\nSTORING CONTENT 2\n\n\n\n");
                DHTContentImpl c2 = new DHTContentImpl(kad2.getOwnerId(), "Some other Data");
                System.out.println(c2);
                kad4.put(c2);
            }

            System.out.println(kad1);
            System.out.println(kad2);
            System.out.println(kad3);
            System.out.println(kad4);
            System.out.println(kad5);

            /* Shutting down kad1 and restarting it */
            System.out.println("\n\n\nShutting down Kad instance");
            System.out.println(kad2);
            kad1.shutdown(true);

            System.out.println("\n\n\nReloading Kad instance from file");
            Kademlia kadR2 = Kademlia.loadFromFile("JoshuaK");
            System.out.println(kadR2);
        }
        
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        new SaveStateTest();
    }
}
