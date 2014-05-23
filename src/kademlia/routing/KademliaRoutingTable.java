package kademlia.routing;

import java.util.List;
import kademlia.KadConfiguration;
import kademlia.node.Node;
import kademlia.node.KademliaId;

/**
 * Specification for Kademlia's Routing Table
 *
 * @author Joshua Kissoon
 * @since 20140501
 */
public interface KademliaRoutingTable
{

    /**
     * Initialize the RoutingTable to it's default state
     */
    public void initialize();

    /**
     * Sets the configuration file for this routing table
     *
     * @param config
     */
    public void setConfiguration(KadConfiguration config);

    /**
     * Adds a contact to the routing table based on how far it is from the LocalNode.
     *
     * @param c The contact to add
     */
    public void insert(Contact c);

    /**
     * Adds a node to the routing table based on how far it is from the LocalNode.
     *
     * @param n The node to add
     */
    public void insert(Node n);

    /**
     * Compute the bucket ID in which a given node should be placed; the bucketId is computed based on how far the node is away from the Local Node.
     *
     * @param nid The NodeId for which we want to find which bucket it belong to
     *
     * @return Integer The bucket ID in which the given node should be placed.
     */
    public int getBucketId(KademliaId nid);

    /**
     * Find the closest set of contacts to a given NodeId
     *
     * @param target           The NodeId to find contacts close to
     * @param numNodesRequired The number of contacts to find
     *
     * @return List A List of contacts closest to target
     */
    public List<Node> findClosest(KademliaId target, int numNodesRequired);

    /**
     * @return List A List of all Nodes in this RoutingTable
     */
    public List getAllNodes();

    /**
     * @return List A List of all Nodes in this RoutingTable
     */
    public List getAllContacts();

    /**
     * @return Bucket[] The buckets in this Kad Instance
     */
    public KadBucket[] getBuckets();

    /**
     * Method used by operations to notify the routing table of any contacts that have been unresponsive.
     *
     * @param contacts The set of unresponsive contacts
     */
    public void setUnresponsiveContacts(List<Node> contacts);

    /**
     * Method used by operations to notify the routing table of any contacts that have been unresponsive.
     *
     * @param n
     */
    public void setUnresponsiveContact(Node n);

}
