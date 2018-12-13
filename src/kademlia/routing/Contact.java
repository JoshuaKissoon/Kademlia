package kademlia.routing;

import kademlia.node.KademliaId;
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
     * Stale as described by Kademlia paper page 64
     * When a contact fails to respond, if the replacement cache is empty and there is no replacement for the contact,
     * just mark it as stale.
     *
     * Now when a new contact is added, if the contact is stale, it is removed.
     */
    private int staleCount;

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

    @Override
    public boolean equals(Object c)
    {
        if (c instanceof Contact)
        {
            return ((Contact) c).getNode().equals(this.getNode());
        }

        return false;
    }

    /**
     * Increments the amount of times this count has failed to respond to a request.
     */
    public void incrementStaleCount()
    {
        staleCount++;
    }

    /**
     * @return Integer Stale count
     */
    public int staleCount()
    {
        return this.staleCount;
    }

    /**
     * Reset the stale count of the contact if it's recently seen
     */
    public void resetStaleCount()
    {
        this.staleCount = 0;
    }

    @Override
    public int compareTo(Contact o)
    {
        if (this.getNode().equals(o.getNode()))
        {
            return 0;
        }

        if (this.lastSeen() == o.lastSeen()) {

            for (int i = 0; i < KademliaId.ID_LENGTH / 8; i++) {
                int compare = Byte.compare(this.getNode().getNodeId().getBytes()[i], o.getNode().getNodeId().getBytes()[i]);
                if (compare != 0) {
                    return compare;
                }
            }

        }

        return (this.lastSeen() > o.lastSeen()) ? 1 : -1;
    }

    @Override
    public int hashCode()
    {
        return this.getNode().hashCode();
    }

}
