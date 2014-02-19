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
            Kademlia kad1 = new Kademlia("Joshua", new NodeId("12345678947584567467"), 7574);
            Kademlia kad2 = new Kademlia("Crystal", new NodeId("12345678999324567838"), 7572);

            /* Connecting 2 to 1 */
            kad1.connect(kad2.getNode());

            System.out.println("Kad 1: ");
            System.out.println(kad1.getNode().getRoutingTable());
            System.out.println("Kad 2: ");
            System.out.println(kad2.getNode().getRoutingTable());

            /* Creating a new node 3 and connecting it to 1, hoping it'll get onto 2 also */
            Kademlia kad3 = new Kademlia("Jessica", new NodeId("88888736882323647625"), 7783);
            kad3.connect(kad1.getNode());
            System.out.println("Kad 3: ");
            System.out.println(kad3.getNode().getRoutingTable());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
