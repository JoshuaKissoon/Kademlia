package kademlia.tests;

import java.io.IOException;
import kademlia.core.Kademlia;
import kademlia.node.NodeId;

/**
 * Testing connecting 2 nodes
 *
 * @author Joshua Kissoon
 * @created 20140219
 */
public class NodeConnectionTest
{

    public static void main(String[] args)
    {
        try
        {
            /* Setting up 2 Kad networks */
            Kademlia kad1 = new Kademlia("JoshuaK", new NodeId("ASF45678947584567467"), 7574);
            System.out.println("Created Node Kad 1: " + kad1.getNode().getNodeId());

            Kademlia kad2 = new Kademlia("Crystal", new NodeId("ASERTKJDHGVHERJHGFLK"), 7572);
            //NodeId diff12 = kad1.getNode().getNodeId().xor(kad2.getNode().getNodeId());
            System.out.println("Created Node Kad 2: " + kad2.getNode().getNodeId());
//            System.out.println(kad1.getNode().getNodeId() + " ^ " + kad2.getNode().getNodeId() + " = " + diff12);
//            System.out.println("Kad 1 - Kad 2 distance: " + diff12.getFirstSetBitIndex());

            /* Connecting 2 to 1 */
            System.out.println("Connecting Kad 1 and Kad 2");
            kad1.bootstrap(kad2.getNode());

//            System.out.println("Kad 1: ");
//            System.out.println(kad1.getNode().getRoutingTable());
//            System.out.println("Kad 2: ");
//            System.out.println(kad2.getNode().getRoutingTable());

            /* Creating a new node 3 and connecting it to 1, hoping it'll get onto 2 also */
            Kademlia kad3 = new Kademlia("Jessica", new NodeId("ASERTKJDOLKMNBVFR45G"), 7783);
            System.out.println("\n\n\n\n\n\nCreated Node Kad 3: " + kad3.getNode().getNodeId());

            System.out.println("Connecting Kad 3 and Kad 2");
            kad3.bootstrap(kad2.getNode());

//            NodeId diff32 = kad3.getNode().getNodeId().xor(kad2.getNode().getNodeId());
//            NodeId diff31 = kad1.getNode().getNodeId().xor(kad3.getNode().getNodeId());
//            System.out.println("Kad 3 - Kad 1 distance: " + diff31.getFirstSetBitIndex());
//            System.out.println("Kad 3 - Kad 2 distance: " + diff32.getFirstSetBitIndex());
            Kademlia kad4 = new Kademlia("Sandy", new NodeId("ASERTK85OLKMN85FR4SS"), 7789);
            System.out.println("\n\n\n\n\n\nCreated Node Kad 4: " + kad4.getNode().getNodeId());

            System.out.println("Connecting Kad 4 and Kad 2");
            kad4.bootstrap(kad2.getNode());

            System.out.println("\n\nKad 1: " + kad1.getNode().getNodeId() + " Routing Table: ");
            System.out.println(kad1.getNode().getRoutingTable());
            System.out.println("\n\nKad 2: " + kad2.getNode().getNodeId() + " Routing Table: ");
            System.out.println(kad2.getNode().getRoutingTable());
            System.out.println("\n\nKad 3: " + kad3.getNode().getNodeId() + " Routing Table: ");
            System.out.println(kad3.getNode().getRoutingTable());
            System.out.println("\n\nKad 4: " + kad4.getNode().getNodeId() + " Routing Table: ");
            System.out.println(kad4.getNode().getRoutingTable());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
