package kademlia.tests;

import kademlia.KademliaNode;
import kademlia.node.KademliaId;
import kademlia.routing.RoutingTable;

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
            KademliaNode kad1 = new KademliaNode("JoshuaK", new KademliaId("ASF45678947584567463"), 12049);
            KademliaNode kad2 = new KademliaNode("Crystal", new KademliaId("ASF45678947584567464"), 4585);
            KademliaNode kad3 = new KademliaNode("Shameer", new KademliaId("ASF45678947584567465"), 8104);
            KademliaNode kad4 = new KademliaNode("Lokesh", new KademliaId("ASF45678947584567466"), 8335);
            KademliaNode kad5 = new KademliaNode("Chandu", new KademliaId("ASF45678947584567467"), 13345);

            RoutingTable rt = kad1.getRoutingTable();
            
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
