package kademlia.routing;

import kademlia.node.Node;

/**
 * Keeps information about contacts of the Node; Contacts are stored in the Buckets in the Routing Table.
 *
 * Contacts are used instead of nodes because more information is needed than just the node information.
 * - Information such as
 * -- Last seen time
 *
 * @author Joshua Kissoon
 * @since 20140425
 * @updated 20140426
 */
public class Contact implements Comparable<Contact>
{

    private final Node n;
    private long lastSeen;

    /**
     * Create a contact object
     *
     * @param n The node associated with this contact
     */
    public Contact(Node n)
    {
        this.n = n;
        this.lastSeen = System.currentTimeMillis() / 1000L;
    }

    public Node getNode()
    {
        return this.n;
    }

    /**
     * When a Node sees a contact a gain, the Node will want to update that it's seen recently,
     * this method updates the last seen timestamp for this contact.
     */
    public void setSeenNow()
    {
        this.lastSeen = System.currentTimeMillis() / 1000L;
    }

    /**
     * When last was this contact seen?
     *
     * @return long The last time this contact was seen.
     */
    public long lastSeen()
    {
        return this.lastSeen;
    }

    public boolean equals(Contact c)
    {
        return c.getNode().equals(this.getNode());
    }

    @Override
    public int compareTo(Contact o)
    {
        if (this.getNode().equals(o.getNode()))
        {
            return 0;
        }

        return (this.lastSeen() > o.lastSeen()) ? 1 : -1;
    }

}
