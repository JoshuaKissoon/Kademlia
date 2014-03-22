package kademlia.routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kademlia.node.Node;
import kademlia.node.NodeId;

/**
 * A bucket in the Kademlia routing table
 *
 * @author Joshua Kissoon
 * @created 20140215
 */
public class KadBucket implements Bucket
{

    private final int depth;
    private final Map<NodeId, Node> nodes;

    
    {
        nodes = new HashMap<>();
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
        /* @todo Check if the bucket is filled already and handle the situation */
        /* Check if the contact is already in the bucket */
        if (this.nodes.containsKey(n.getNodeId()))
        {
            /* @todo If it is, then move it to the front */
            /* @todo Possibly use a doubly linked list instead of an ArrayList */
        }
        else
        {
            nodes.put(n.getNodeId(), n);
        }
    }

    /**
     * Checks if this bucket contain a node
     *
     * @param n The node to check for
     *
     * @return boolean
     */
    @Override
    public boolean containNode(Node n)
    {
        return this.nodes.containsKey(n.getNodeId());
    }

    /**
     * Remove a node from this bucket
     *
     * @param n The node to remove
     */
    @Override
    public void removeNode(Node n)
    {
        this.nodes.remove(n.getNodeId());
    }

    @Override
    public int numNodes()
    {
        return this.nodes.size();
    }

    @Override
    public int getDepth()
    {
        return this.depth;
    }

    @Override
    public List<Node> getNodes()
    {
        return new ArrayList<>(this.nodes.values());
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("Bucket at depth: ");
        sb.append(this.depth);
        sb.append("\n Nodes: \n");
        for (Node n : this.nodes.values())
        {
            sb.append("Node: ");
            sb.append(n.getNodeId().toString());
            sb.append("\n");
        }

        return sb.toString();
    }
}
