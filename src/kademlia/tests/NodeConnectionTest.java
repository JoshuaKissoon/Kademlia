/**
 * @author Joshua Kissoon
 * @created 20140219
 * @desc Testing connecting 2 nodes
 */
package kademlia.tests;

import java.io.IOException;
import kademlia.core.Kademlia;
import kademlia.node.NodeId;

public class NodeConnectionTest
{

    public static void main(String[] args)
    {
        try
        {
            /* Setting up 2 Kad networks */
            Kademlia kad1 = new Kademlia("Joshua", new NodeId("12345678901234567890"), 7574);
            System.out.println("Kad 1 Before: ");
            System.out.println(kad1.getNode().getRoutingTable());

            Kademlia kad2 = new Kademlia("Crystal", new NodeId("12345678901234567891"), 7572);
            System.out.println("Kad 2 Before: ");
            System.out.println(kad2.getNode().getRoutingTable());

            /* Connecting 2 to 1 */
            kad1.connect(kad2.getNode());
            System.out.println("Kad 1 After: ");
            System.out.println(kad1.getNode().getRoutingTable());
            System.out.println("Kad 2 After: ");
            System.out.println(kad2.getNode().getRoutingTable());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
