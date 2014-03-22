package kademlia.routing;

import kademlia.node.Node;

/**
 * A bucket used to store Nodes in the routing table.
 *
 * @todo Update this interface and use this as parameter type, etc... instead of the KadBucket implementation used throughout the application
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
     * Marks a node as dead: the dead node will be replace if
     * insert was invoked
     *
     * @param n the dead node
     */
    public void markDead(Node n);
}
