package kademlia.tests;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import kademlia.core.Kademlia;
import kademlia.node.Node;
import kademlia.node.NodeId;

/**
 * Had some problems with treemap, so trying out testing treemaps
 *
 * @author Joshua Kissoon
 * @since 20140311
 */
public class TreeMapTest
{

    /* Constants */
    private static final Byte UNASKED = (byte) 0x00;
    private static final Byte AWAITING = (byte) 0x01;
    private static final Byte ASKED = (byte) 0x02;
    private static final Byte FAILED = (byte) 0x03;

    private final SortedMap<Node, Byte> nodes;

    /* Used to sort nodes */
    private final Comparator comparator;

    public TreeMapTest() throws IOException
    {
        /* Setting up 2 Kad networks */
        Kademlia kad1 = new Kademlia("JoshuaK", new NodeId("ASF45678947584567467"), 8888);
        Kademlia kad2 = new Kademlia("Crystal", new NodeId("AfERTKdvHGVHERJHGFdh"), 8889);
        Kademlia kad3 = new Kademlia("Shameer", new NodeId("ASERTKyrHGVHERfHGFsy"), 8890);
        Kademlia kad4 = new Kademlia("Lokesh", new NodeId("AS3RTKJsdjVHERJHGF94"), 8891);
        Kademlia kad5 = new Kademlia("Chandu", new NodeId("ASERT47kfeVHERJHGF15"), 8892);

        this.comparator = new Node.DistanceComparator(kad1.getNode().getNodeId());
        this.nodes = new TreeMap(this.comparator);

        /* Add all nodes as unasked */
        this.nodes.put(kad1.getNode(), ASKED);
        this.nodes.put(kad2.getNode(), UNASKED);
        this.nodes.put(kad3.getNode(), UNASKED);
        this.nodes.put(kad4.getNode(), UNASKED);
        this.nodes.put(kad5.getNode(), UNASKED);
        
        this.printTree();
    }

    private void printTree()
    {
        for (Map.Entry<Node, Byte> e : this.nodes.entrySet())
        {
            System.out.println("Node: " + e.getKey() + "; Value: " + e.getValue());
        }
    }

    public static void main(String[] args)
    {
        try
        {
            new TreeMapTest();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
