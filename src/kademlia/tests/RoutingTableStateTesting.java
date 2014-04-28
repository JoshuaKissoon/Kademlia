package kademlia.tests;

import java.io.IOException;
import kademlia.KademliaNode;
import kademlia.dht.KadContent;
import kademlia.node.NodeId;

/**
 * Testing how the routing table works and it's state after different operations
 *
 * @author Joshua Kissoon
 * @since 20140426
 */
public class RoutingTableStateTesting
{

    KademliaNode kad0, kad1, kad2, kad3, kad4, kad5, kad6, kad7, kad8, kad9;

    public RoutingTableStateTesting()
    {
        try
        {
            /* Setting up 2 Kad networks */
            kad0 = new KademliaNode("user0", new NodeId("HRF456789SD584567460"), 1334);
            kad1 = new KademliaNode("user1", new NodeId("ASF456789475DS567461"), 1209);
            kad2 = new KademliaNode("user2", new NodeId("AFG45678947584567462"), 4585);
            kad3 = new KademliaNode("user3", new NodeId("FSF45J38947584567463"), 8104);
            kad4 = new KademliaNode("user4", new NodeId("ASF45678947584567464"), 8335);
            kad5 = new KademliaNode("user5", new NodeId("GHF4567894DR84567465"), 13345);
            kad6 = new KademliaNode("user6", new NodeId("ASF45678947584567466"), 12049);
            kad7 = new KademliaNode("user7", new NodeId("AE345678947584567467"), 14585);
            kad8 = new KademliaNode("user8", new NodeId("ASAA5678947584567468"), 18104);
            kad9 = new KademliaNode("user9", new NodeId("ASF456789475845674U9"), 18335);

            kad1.bootstrap(kad0.getNode());
            kad2.bootstrap(kad0.getNode());
            kad3.bootstrap(kad0.getNode());
            kad4.bootstrap(kad0.getNode());
            kad5.bootstrap(kad0.getNode());
            kad6.bootstrap(kad0.getNode());
            kad7.bootstrap(kad0.getNode());
            kad8.bootstrap(kad0.getNode());
            kad9.bootstrap(kad0.getNode());

            /* Lets shut down a node and then try putting a content on the network. We'll then see how the un-responsive contacts work */
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public KadContent putContent(String content, KademliaNode owner)
    {
        DHTContentImpl c = null;
        try
        {
            c = new DHTContentImpl(kad2.getOwnerId(), "Some Data");
            owner.put(c);
            return c;
        }
        catch (IOException e)
        {
            System.err.println("Error whiles putting content " + content + " from owner: " + owner.getOwnerId());
        }

        return c;
    }

    public void shutdownKad(KademliaNode kad)
    {
        try
        {
            kad.shutdown(false);
        }
        catch (IOException ex)
        {
            System.err.println("Error whiles shutting down node with owner: " + kad.getOwnerId());
        }
    }

    public void printRoutingTables()
    {
        System.out.println(kad0.getRoutingTable());
        System.out.println(kad1.getRoutingTable());
        System.out.println(kad2.getRoutingTable());
        System.out.println(kad3.getRoutingTable());
        System.out.println(kad4.getRoutingTable());
        System.out.println(kad5.getRoutingTable());
        System.out.println(kad6.getRoutingTable());
        System.out.println(kad7.getRoutingTable());
        System.out.println(kad8.getRoutingTable());
        System.out.println(kad9.getRoutingTable());
    }

    public void printStorage()
    {
        System.out.println(kad0.getDHT());
        System.out.println(kad1.getDHT());
        System.out.println(kad2.getDHT());
        System.out.println(kad3.getDHT());
        System.out.println(kad4.getDHT());
        System.out.println(kad5.getDHT());
        System.out.println(kad6.getDHT());
        System.out.println(kad7.getDHT());
        System.out.println(kad8.getDHT());
        System.out.println(kad9.getDHT());
    }

    public static void main(String[] args)
    {
        RoutingTableStateTesting rtss = new RoutingTableStateTesting();
        rtss.printRoutingTables();

        /* Lets shut down a node to test the node removal operation */
        rtss.shutdownKad(rtss.kad3);

        rtss.putContent("Content owned by kad0", rtss.kad0);
        rtss.printStorage();
    }
}
