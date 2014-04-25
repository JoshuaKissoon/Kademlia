package kademlia.routing;

import kademlia.node.Node;

/**
 * Keeps information about contacts of the Node; Contacts are stored in the Buckets in the Routing Table.
 *
 * Contacts are used instead of nodes because more information is needed than just the node information.
 * - Information such as
 * -- Last alive time
 *
 * @author Joshua Kissoon
 * @since 20140425
 */
public class Contact
{

    private final Node n;

    public Contact(Node n)
    {
        this.n = n;
    }

    public Node getNode()
    {
        return this.n;
    }
}
