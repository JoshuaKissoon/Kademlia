/**
 * @author Joshua Kissoon
 * @created 20140215
 * @desc A bucket in the Kademlia routing table
 */
package kademlia.routing;

import java.util.ArrayList;
import kademlia.node.Node;

public class KadBucket implements Bucket
{

    private final int depth;
    private final ArrayList<Node> nodes;

    
    {
        nodes = new ArrayList<>();
    }

    /**
     * @param depth How deep in the routing tree is this bucket
     */
    public KadBucket(int depth)
    {
        this.depth = depth;
    }

    @Override
    public void insert(Node n)
    {
        /*@todo Check if the bucket is filled already and handle this */
        /* Check if the contact is already in the bucket */
        if (this.nodes.contains(n))
        {
            /* @todo If it is, then move it to the front */
            /* @todo Possibly use a doubly linked list instead of an ArrayList */
        }
        else
        {
            nodes.add(n);
        }
    }

    public int numNodes()
    {
        return this.nodes.size();
    }

    public int getDepth()
    {
        return this.depth;
    }

    @Override
    public void markDead(Node n)
    {
        this.nodes.remove(n);
    }

    public ArrayList<Node> getNodes()
    {
        return this.nodes;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("Printing bucket at depth: ");
        sb.append(this.depth);
        sb.append("\n Nodes: \n");
        for (Node n : this.nodes)
        {
            sb.append("Node: ");
            sb.append(n.getNodeId().toString());
            sb.append("\n");
        }

        return sb.toString();
    }
}
