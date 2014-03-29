package kademlia.tests;

import java.util.Timer;
import java.util.TimerTask;
import kademlia.core.Configuration;
import kademlia.core.Kademlia;
import kademlia.node.NodeId;

/**
 * Testing the Kademlia Auto Content and Node table refresh operations
 *
 * @author Joshua Kissoon
 * @since 20140309
 */
public class AutoRefreshOperationTest
{

    public AutoRefreshOperationTest()
    {
        try
        {
            /* Setting up 2 Kad networks */
            final Kademlia kad1 = new Kademlia("JoshuaK", new NodeId("ASF45678947584567463"), 12049);
            final Kademlia kad2 = new Kademlia("Crystal", new NodeId("ASF45678947584567464"), 4585);
            final Kademlia kad3 = new Kademlia("Shameer", new NodeId("ASF45678947584567465"), 8104);
            final Kademlia kad4 = new Kademlia("Lokesh", new NodeId("ASF45678947584567466"), 8335);
            final Kademlia kad5 = new Kademlia("Chandu", new NodeId("ASF45678947584567467"), 13345);

            /* Connecting nodes */
            System.out.println("Connecting Nodes");
            kad2.bootstrap(kad1.getNode());
            kad3.bootstrap(kad2.getNode());
            kad4.bootstrap(kad2.getNode());
            kad5.bootstrap(kad4.getNode());

            DHTContentImpl c = new DHTContentImpl(kad1.getOwnerId(), "Some Data");
            kad1.putLocally(c);
            System.out.println("\nSTORING CONTENT 1 locally on " + kad1.getOwnerId() + "\n\n\n\n");

            System.out.println(kad1);
            System.out.println(kad2);
            System.out.println(kad3);
            System.out.println(kad4);
            System.out.println(kad5);

            /* Print the node states every few minutes */
            Timer timer = new Timer(true);
            timer.schedule(
                    new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            System.out.println(kad1);
                            System.out.println(kad2);
                            System.out.println(kad3);
                            System.out.println(kad4);
                            System.out.println(kad5);
                        }
                    },
                    // Delay                        // Interval
                    Configuration.RESTORE_INTERVAL, Configuration.RESTORE_INTERVAL
            );
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        new AutoRefreshOperationTest();
    }
}
