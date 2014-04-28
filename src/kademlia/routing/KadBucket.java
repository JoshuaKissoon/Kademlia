package kademlia.routing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import kademlia.node.Node;

/**
 * A bucket in the Kademlia routing table
 *
 * @author Joshua Kissoon
 * @created 20140215
 */
public class KadBucket implements Bucket
{

    /* How deep is this bucket in the Routing Table */
    private final int depth;

    /* Contacts stored in this routing table */
    private final Map<Contact, Contact> contacts;

    /* A set of last seen contacts that can replace any current contact that is unresponsive */
    private final Map<Contact, Contact> replacementCache;

    
    {
        contacts = new TreeMap<>(new ContactLastSeenComparator());
        replacementCache = new TreeMap<>(new ContactLastSeenComparator());
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
        if (this.contacts.containsKey(c))
        {
            /**
             * If the contact is already in the bucket, lets update that we've seen it
             * We need to remove and re-add the contact to get the Sorted Set to update sort order
             */
            Contact tmp = this.contacts.remove(c);
            tmp.setSeenNow();
            this.contacts.put(tmp, tmp);
        }
        else
        {
            contacts.put(c, c);
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
        return this.contacts.containsKey(c);
    }

    @Override
    public boolean containsNode(Node n)
    {
        return this.containsContact(new Contact(n));
    }

    @Override
    public void removeContact(Contact c)
    {
        this.contacts.remove(c);
    }

    @Override
    public void removeNode(Node n)
    {
        this.removeContact(new Contact(n));
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
    public synchronized List<Contact> getContacts()
    {
        return (this.contacts.isEmpty()) ? new ArrayList<>() : new ArrayList<>(this.contacts.values());
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
