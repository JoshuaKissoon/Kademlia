/**
 * @author Joshua Kissoon
 * @created 20140215
 * @desc A bucket for the DHT Protocol
 */
package kademlia.routing;

import kademlia.node.Node;

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
