package kademlia.routing;

import java.util.List;
import kademlia.node.Node;

/**
 * A bucket used to store Nodes in the routing table.
 *
 * @author Joshua Kissoon
 * @created 20140215
 */
public interface Bucket
{

    /**
     * Adds a new node to the bucket
     *
     * @param n the new node
     */
    public void insert(Node n);

    /**
     * Checks if this bucket contain a node
     *
     * @param n The node to check for
     *
     * @return boolean
     */
    public boolean containNode(Node n);

    /**
     * Remove a node from this bucket
     *
     * @param n The node to remove
     */
    public void removeNode(Node n);

    /**
     * Counts the number of nodes in this bucket.
     *
     * @return Integer The number of nodes in this bucket
     */
    public int numNodes();

    /**
     * @return Integer The depth of this bucket in the RoutingTable
     */
    public int getDepth();

    /**
     * @return An Iterable structure with all nodes in this bucket
     */
    public List<Node> getNodes();
}
