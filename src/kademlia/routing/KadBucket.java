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
    private final Map<NodeId, Contact> contacts;

    
    {
        contacts = new HashMap<>();
    }

    /**
     * @param depth How deep in the routing tree is this bucket
     */
    public KadBucket(int depth)
    {
        this.depth = depth;
    }

    @Override
    public void insert(Contact c)
    {
        /* @todo Check if the bucket is filled already and handle the situation */
        /* Check if the contact is already in the bucket */
        if (this.contacts.containsKey(c.getNode().getNodeId()))
        {
            /* @todo If it is, then move it to the front */
            /* @todo Possibly use a doubly linked list instead of an ArrayList */
        }
        else
        {
            contacts.put(c.getNode().getNodeId(), c);
        }
    }

    @Override
    public void insert(Node n)
    {
        this.insert(new Contact(n));
    }

    @Override
    public boolean containsContact(Contact c)
    {
        return this.contacts.containsKey(c.getNode().getNodeId());
    }

    @Override
    public boolean containsNode(Node n)
    {
        return this.contacts.containsKey(n.getNodeId());
    }

    @Override
    public void removeContact(Contact c)
    {
        this.contacts.remove(c.getNode().getNodeId());
    }

    @Override
    public void removeNode(Node n)
    {
        this.contacts.remove(n.getNodeId());
    }

    @Override
    public int numContacts()
    {
        return this.contacts.size();
    }

    @Override
    public int getDepth()
    {
        return this.depth;
    }

    @Override
    public List<Contact> getContacts()
    {
        return new ArrayList<>(this.contacts.values());
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("Bucket at depth: ");
        sb.append(this.depth);
        sb.append("\n Nodes: \n");
        for (Contact n : this.contacts.values())
        {
            sb.append("Node: ");
            sb.append(n.getNode().getNodeId().toString());
            sb.append("\n");
        }

        return sb.toString();
    }
}
