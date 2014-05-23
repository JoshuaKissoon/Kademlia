package kademlia.simulations;

import java.util.Timer;
import java.util.TimerTask;
import kademlia.DefaultConfiguration;
import kademlia.KademliaNode;
import kademlia.KadConfiguration;
import kademlia.node.KademliaId;

/**
 * Testing the Kademlia Auto Content and Node table refresh operations
 *
 * @author Joshua Kissoon
 * @since 20140309
 */
public class AutoRefreshOperation implements Simulation
{

    @Override
    public void runSimulation()
    {
        try
        {
            /* Setting up 2 Kad networks */
            final KademliaNode kad1 = new KademliaNode("JoshuaK", new KademliaId("ASF456789djem45674DH"), 12049);
            final KademliaNode kad2 = new KademliaNode("Crystal", new KademliaId("AJDHR678947584567464"), 4585);
            final KademliaNode kad3 = new KademliaNode("Shameer", new KademliaId("AS84k6789KRNS45KFJ8W"), 8104);
            final KademliaNode kad4 = new KademliaNode("Lokesh.", new KademliaId("ASF45678947A845674GG"), 8335);
            final KademliaNode kad5 = new KademliaNode("Chandu.", new KademliaId("AS84kUD894758456dyrj"), 13345);

            /* Connecting nodes */
            System.out.println("Connecting Nodes");
            kad2.bootstrap(kad1.getNode());
            kad3.bootstrap(kad2.getNode());
            kad4.bootstrap(kad2.getNode());
            kad5.bootstrap(kad4.getNode());

            DHTContentImpl c = new DHTContentImpl(new KademliaId("AS84k678947584567465"), kad1.getOwnerId());
            c.setData("Setting the data");

            System.out.println("\n Content ID: " + c.getKey());
            System.out.println(kad1.getNode() + " Distance from content: " + kad1.getNode().getNodeId().getDistance(c.getKey()));
            System.out.println(kad2.getNode() + " Distance from content: " + kad2.getNode().getNodeId().getDistance(c.getKey()));
            System.out.println(kad3.getNode() + " Distance from content: " + kad3.getNode().getNodeId().getDistance(c.getKey()));
            System.out.println(kad4.getNode() + " Distance from content: " + kad4.getNode().getNodeId().getDistance(c.getKey()));
            System.out.println(kad5.getNode() + " Distance from content: " + kad5.getNode().getNodeId().getDistance(c.getKey()));
            System.out.println("\nSTORING CONTENT 1 locally on " + kad1.getOwnerId() + "\n\n\n\n");

            kad1.putLocally(c);

            System.out.println(kad1);
            System.out.println(kad2);
            System.out.println(kad3);
            System.out.println(kad4);
            System.out.println(kad5);

            /* Print the node states every few minutes */
            KadConfiguration config = new DefaultConfiguration();
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
                    config.restoreInterval(), config.restoreInterval()
            );
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
