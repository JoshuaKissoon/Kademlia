package kademlia.routing;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import kademlia.KadConfiguration;
import kademlia.node.Node;

/**
 * A bucket in the Kademlia routing table
 *
 * @author Joshua Kissoon
 * @created 20140215
 */
public class KadBucketImpl implements KadBucket
{

    /* How deep is this bucket in the Routing Table */
    private final int depth;

    /* Contacts stored in this routing table */
    private final TreeSet<Contact> contacts;

    /* A set of last seen contacts that can replace any current contact that is unresponsive */
    private final TreeSet<Contact> replacementCache;

    private final KadConfiguration config;

    
    {
        contacts = new TreeSet<>();
        replacementCache = new TreeSet<>();
    }

    /**
     * @param depth  How deep in the routing tree is this bucket
     * @param config
     */
    public KadBucketImpl(int depth, KadConfiguration config)
    {
        this.depth = depth;
        this.config = config;
    }

    @Override
    public synchronized void insert(Contact c)
    {
        if (this.contacts.contains(c))
        {
            /**
             * If the contact is already in the bucket, lets update that we've seen it
             * We need to remove and re-add the contact to get the Sorted Set to update sort order
             */
            Contact tmp = this.removeFromContacts(c.getNode());
            tmp.setSeenNow();
            tmp.resetStaleCount();
            this.contacts.add(tmp);
        }
        else
        {
            /* If the bucket is filled, so put the contacts in the replacement cache */
            if (contacts.size() >= this.config.k())
            {
                /* If the cache is empty, we check if any contacts are stale and replace the stalest one */
                Contact stalest = null;
                for (Contact tmp : this.contacts)
                {
                    if (tmp.staleCount() >= this.config.stale())
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
                    this.contacts.add(c);
                }
                else
                {
                    /* No stale contact, lets insert this into replacement cache */
                    this.insertIntoReplacementCache(c);
                }
            }
            else
            {
                this.contacts.add(c);
            }
        }
    }

    @Override
    public synchronized void insert(Node n)
    {
        this.insert(new Contact(n));
    }

    @Override
    public synchronized boolean containsContact(Contact c)
    {
        return this.contacts.contains(c);
    }

    @Override
    public synchronized boolean containsNode(Node n)
    {
        return this.containsContact(new Contact(n));
    }

    @Override
    public synchronized boolean removeContact(Contact c)
    {
        /* If the contact does not exist, then we failed to remove it */
        if (!this.contacts.contains(c))
        {
            return false;
        }

        /* Contact exist, lets remove it only if our replacement cache has a replacement */
        if (!this.replacementCache.isEmpty())
        {
            /* Replace the contact with one from the replacement cache */
            this.contacts.remove(c);
            Contact replacement = this.replacementCache.first();
            this.contacts.add(replacement);
            this.replacementCache.remove(replacement);
        }
        else
        {
            /* There is no replacement, just increment the contact's stale count */
            this.getFromContacts(c.getNode()).incrementStaleCount();
        }

        return true;
    }

    private synchronized Contact getFromContacts(Node n)
    {
        for (Contact c : this.contacts)
        {
            if (c.getNode().equals(n))
            {
                return c;
            }
        }

        /* This contact does not exist */
        throw new NoSuchElementException("The contact does not exist in the contacts list.");
    }

    private synchronized Contact removeFromContacts(Node n)
    {
        for (Contact c : this.contacts)
        {
            if (c.getNode().equals(n))
            {
                this.contacts.remove(c);
                return c;
            }
        }

        /* We got here means this element does not exist */
        throw new NoSuchElementException("Node does not exist in the replacement cache. ");
    }

    @Override
    public synchronized boolean removeNode(Node n)
    {
        return this.removeContact(new Contact(n));
    }

    @Override
    public synchronized int numContacts()
    {
        return this.contacts.size();
    }

    @Override
    public synchronized int getDepth()
    {
        return this.depth;
    }

    @Override
    public synchronized List<Contact> getContacts()
    {
        final ArrayList<Contact> ret = new ArrayList<>();

        /* If we have no contacts, return the blank arraylist */
        if (this.contacts.isEmpty())
        {
            return ret;
        }

        /* We have contacts, lets copy put them into the arraylist and return */
        for (Contact c : this.contacts)
        {
            ret.add(c);
        }

        return ret;
    }

    /**
     * When the bucket is filled, we keep extra contacts in the replacement cache.
     */
    private synchronized void insertIntoReplacementCache(Contact c)
    {
        /* Just return if this contact is already in our replacement cache */
        if (this.replacementCache.contains(c))
        {
            /**
             * If the contact is already in the bucket, lets update that we've seen it
             * We need to remove and re-add the contact to get the Sorted Set to update sort order
             */
            Contact tmp = this.removeFromReplacementCache(c.getNode());
            tmp.setSeenNow();
            this.replacementCache.add(tmp);
        }
        else if (this.replacementCache.size() > this.config.k())
        {
            /* if our cache is filled, we remove the least recently seen contact */
            this.replacementCache.remove(this.replacementCache.last());
            this.replacementCache.add(c);
        }
        else
        {
            this.replacementCache.add(c);
        }
    }

    private synchronized Contact removeFromReplacementCache(Node n)
    {
        for (Contact c : this.replacementCache)
        {
            if (c.getNode().equals(n))
            {
                this.replacementCache.remove(c);
                return c;
            }
        }

        /* We got here means this element does not exist */
        throw new NoSuchElementException("Node does not exist in the replacement cache. ");
    }

    @Override
    public synchronized String toString()
    {
        StringBuilder sb = new StringBuilder("Bucket at depth: ");
        sb.append(this.depth);
        sb.append("\n Nodes: \n");
        for (Contact n : this.contacts)
        {
            sb.append("Node: ");
            sb.append(n.getNode().getNodeId().toString());
            sb.append(" (stale: ");
            sb.append(n.staleCount());
            sb.append(")");
            sb.append("\n");
        }

        return sb.toString();
    }
}
