package kademlia.routing;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import kademlia.core.KadConfiguration;
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
    private final TreeMap<Contact, Contact> contacts;

    /* A set of last seen contacts that can replace any current contact that is unresponsive */
    private final TreeMap<Contact, Contact> replacementCache;

    private KadConfiguration config;

    
    {
        contacts = new TreeMap<>(new ContactLastSeenComparator());
        replacementCache = new TreeMap<>(new ContactLastSeenComparator());
    }

    /**
     * @param depth  How deep in the routing tree is this bucket
     * @param config
     */
    public KadBucket(int depth, KadConfiguration config)
    {
        this.depth = depth;
        this.config = config;
    }

    @Override
    public void insert(Contact c)
    {
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
            /* If the bucket is filled, so put the contacts in the replacement cache */
            if (contacts.size() >= this.config.k())
            {
                /* If the cache is empty, we check if any contacts are stale and replace the stalest one */
                Contact stalest = null;
                for (Contact tmp : this.contacts.keySet())
                {
                    if (tmp.staleCount() > this.config.stale())
                    {
                        /* Contact is stale */
                        if (stalest == null)
                        {
                            stalest = tmp;
                        }
                        else if (tmp.staleCount() > stalest.staleCount())
                        {
                            stalest = tmp;
                        }
                    }
                }

                /* If we have a stale contact, remove it and add the new contact to the bucket */
                if (stalest != null)
                {
                    this.contacts.remove(stalest);
                    this.insert(c);
                }
                else
                {
                    /* No stale contact, lets insert this into replacement cache */
                    this.insertIntoReplacementCache(c);
                }
            }
            else
            {
                contacts.put(c, c);
            }
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
    public boolean removeContact(Contact c)
    {
        /* If the contact does not exist, then we failed to remove it */
        if (!this.contacts.containsKey(c))
        {
            return false;
        }

        if (!this.replacementCache.isEmpty())
        {
            /* Replace the contact with one from the replacement cache */
            this.contacts.remove(c);
            Contact replacement = this.replacementCache.firstKey();
            this.contacts.put(replacement, replacement);
            this.replacementCache.remove(replacement);
        }
        else
        {
            /* There is no replacement, just increment the contact's stale count */
            this.contacts.get(c).incrementStaleCount();
        }

        return true;
    }

    @Override
    public boolean removeNode(Node n)
    {
        return this.removeContact(new Contact(n));
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

    /**
     * When the bucket is filled, we keep extra contacts in the replacement cache.
     */
    private void insertIntoReplacementCache(Contact c)
    {
        /* Just return if this contact is already in our replacement cache */
        if (this.replacementCache.containsKey(c))
        {
            /**
             * If the contact is already in the bucket, lets update that we've seen it
             * We need to remove and re-add the contact to get the Sorted Set to update sort order
             */
            Contact tmp = this.replacementCache.remove(c);
            tmp.setSeenNow();
            this.replacementCache.put(tmp, tmp);
            return;
        }

        /* if our cache is filled, we remove the least recently seen contact */
        if (this.replacementCache.size() > this.config.k())
        {
            this.replacementCache.remove(this.replacementCache.lastKey());
        }

        this.replacementCache.put(c, c);
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
