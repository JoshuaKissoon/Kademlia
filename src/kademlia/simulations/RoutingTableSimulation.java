package kademlia.simulations;

import kademlia.JKademliaNode;
import kademlia.node.KademliaId;
import kademlia.routing.KademliaRoutingTable;

/**
 * Testing how the routing table works and checking if everything works properly
 *
 * @author Joshua Kissoon
 * @since 20140426
 */
public class RoutingTableSimulation
{

    public RoutingTableSimulation()
    {
        try
        {
            /* Setting up 2 Kad networks */
            JKademliaNode kad1 = new JKademliaNode("JoshuaK", new KademliaId("ASF45678947584567463"), 12049);
            JKademliaNode kad2 = new JKademliaNode("Crystal", new KademliaId("ASF45678947584567464"), 4585);
            JKademliaNode kad3 = new JKademliaNode("Shameer", new KademliaId("ASF45678947584567465"), 8104);
            JKademliaNode kad4 = new JKademliaNode("Lokesh", new KademliaId("ASF45678947584567466"), 8335);
            JKademliaNode kad5 = new JKademliaNode("Chandu", new KademliaId("ASF45678947584567467"), 13345);

            KademliaRoutingTable rt = kad1.getRoutingTable();
            
            rt.insert(kad2.getNode());
            rt.insert(kad3.getNode());
            rt.insert(kad4.getNode());
            System.out.println(rt);
            
            rt.insert(kad5.getNode());            
            System.out.println(rt);
            
            rt.insert(kad3.getNode());            
            System.out.println(rt);
            
            
            /* Lets shut down a node and then try putting a content on the network. We'll then see how the un-responsive contacts work */
        }
        catch (IllegalStateException e)
        {

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        new RoutingTableSimulation();
    }
}
