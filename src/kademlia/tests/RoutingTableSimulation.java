package kademlia.tests;

import kademlia.Kademlia;
import kademlia.node.NodeId;
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
            Kademlia kad1 = new Kademlia("JoshuaK", new NodeId("ASF45678947584567463"), 12049);
            Kademlia kad2 = new Kademlia("Crystal", new NodeId("ASF45678947584567464"), 4585);
            Kademlia kad3 = new Kademlia("Shameer", new NodeId("ASF45678947584567465"), 8104);
            Kademlia kad4 = new Kademlia("Lokesh", new NodeId("ASF45678947584567466"), 8335);
            Kademlia kad5 = new Kademlia("Chandu", new NodeId("ASF45678947584567467"), 13345);

            RoutingTable rt = kad1.getNode().getRoutingTable();
            
            rt.insert(kad2.getNode());
            rt.insert(kad3.getNode());
            rt.insert(kad4.getNode());
            System.out.println(rt);
            
            rt.insert(kad5.getNode());            
            System.out.println(rt);
            
            rt.insert(kad3.getNode());            
            System.out.println(rt);
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
