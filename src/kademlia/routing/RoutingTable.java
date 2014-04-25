package kademlia.routing;

import java.util.ArrayList;
import java.util.List;
import kademlia.node.Node;
import kademlia.node.NodeId;

/**
 * Implementation of a Kademlia routing table
 *
 * @author Joshua Kissoon
 * @created 20140215
 */
public class RoutingTable
{

    private final Node localNode;  // The current node
    private transient Bucket[] buckets;

    public RoutingTable(Node localNode)
    {
        this.localNode = localNode;

        /* Initialize all of the buckets to a specific depth */
        this.initialize();

        /* Insert the local node */
        this.insert(localNode);
    }

    /**
     * Initialize the RoutingTable to it's default state
     */
    public final void initialize()
    {
        this.buckets = new Bucket[NodeId.ID_LENGTH];
        for (int i = 0; i < NodeId.ID_LENGTH; i++)
        {
            buckets[i] = new KadBucket(i);
        }
    }

    /**
     * Adds a contact to the routing table based on how far it is from the LocalNode.
     *
     * @param c The contact to add
     */
    public final void insert(Contact c)
    {
        this.buckets[this.getBucketId(c.getNode().getNodeId())].insert(c);
    }

    /**
     * Adds a node to the routing table based on how far it is from the LocalNode.
     *
     * @param n The node to add
     */
    public final void insert(Node n)
    {
        this.buckets[this.getBucketId(n.getNodeId())].insert(n);
    }

    /**
     * Remove a node from the routing table.
     *
     * @param n The node to remove
     */
    public final void remove(Node n)
    {
        int bucketId = this.getBucketId(n.getNodeId());

        /* If the bucket has the contact, remove it */
        if (this.buckets[bucketId].containsNode(n))
        {
            this.buckets[bucketId].removeNode(n);
        }
    }

    /**
     * Compute the bucket ID in which a given node should be placed; the bucketId is computed based on how far the node is away from the Local Node.
     *
     * @param nid The NodeId for which we want to find which bucket it belong to
     *
     * @return Integer The bucket ID in which the given node should be placed.
     */
    public final int getBucketId(NodeId nid)
    {
        int bId = this.localNode.getNodeId().getDistance(nid) - 1;

        /* If we are trying to insert a node into it's own routing table, then the bucket ID will be -1, so let's just keep it in bucket 0 */
        return bId < 0 ? 0 : bId;
    }

    /**
     * Find the closest set of contacts to a given NodeId
     *
     * @param target           The NodeId to find contacts close to
     * @param numNodesRequired The number of contacts to find
     *
     * @return List A List of contacts closest to target
     */
    public final List<Node> findClosest(NodeId target, int numNodesRequired)
    {
        List<Node> closest = new ArrayList<>(numNodesRequired);

        /* Get the bucket index to search for closest from */
        int bucketIndex = this.getBucketId(target);

        /* Add the contacts from this bucket to the return contacts */
        for (Contact c : this.buckets[bucketIndex].getContacts())
        {
            if (closest.size() < numNodesRequired)
            {
                closest.add(c.getNode());
            }
            else
            {
                break;
            }
        }

        if (closest.size() >= numNodesRequired)
        {
            return closest;
        }

        /**
         * We still need more nodes
         * Lets add from nodes closer to localNode since they are the ones that will be closer to the given nid
         */
        for (int i = 1; (bucketIndex - i) >= 0; i++)
        {
            for (Contact c : this.buckets[bucketIndex - i].getContacts())
            {
                if (closest.size() < numNodesRequired)
                {
                    closest.add(c.getNode());
                }
                else
                {
                    break;
                }
            }

            /* If we have enough contacts, then stop adding */
            if (closest.size() >= numNodesRequired)
            {
                break;
            }
        }

        if (closest.size() >= numNodesRequired)
        {
            return closest;
        }

        /**
         * We still need more nodes, add from nodes farther to localNode
         */
        for (int i = 1; (bucketIndex + i) < NodeId.ID_LENGTH; i++)
        {
            for (Contact c : this.buckets[bucketIndex + i].getContacts())
            {
                if (closest.size() < numNodesRequired)
                {
                    closest.add(c.getNode());
                }
                else
                {
                    break;
                }
            }

            /* If we have enough contacts, then stop adding */
            if (closest.size() >= numNodesRequired)
            {
                break;
            }
        }

        return closest;
    }

    /**
     * @return List A List of all Nodes in this RoutingTable
     */
    public final List getAllNodes()
    {
        List<Node> nodes = new ArrayList<>();

        for (Bucket b : this.buckets)
        {
            for (Contact c : b.getContacts())
            {
                nodes.add(c.getNode());
            }
        }

        return nodes;
    }

    /**
     * @return List A List of all Nodes in this RoutingTable
     */
    public final List getAllContacts()
    {
        List<Contact> contacts = new ArrayList<>();

        for (Bucket b : this.buckets)
        {
            contacts.addAll(b.getContacts());
        }

        return contacts;
    }

    /**
     * @return Bucket[] The buckets in this Kad Instance
     */
    public final Bucket[] getBuckets()
    {
        return this.buckets;
    }

    /**
     * Set the KadBuckets of this routing table, mainly used when retrieving saved state
     *
     * @param buckets
     */
    public final void setBuckets(Bucket[] buckets)
    {
        this.buckets = buckets;
    }

    @Override
    public final String toString()
    {
        StringBuilder sb = new StringBuilder("\nPrinting Routing Table Started ***************** \n");
        for (Bucket b : this.buckets)
        {
            if (b.numContacts() > 0)
            {
                sb.append("# nodes in Bucket with depth ");
                sb.append(b.getDepth());
                sb.append(": ");
                sb.append(b.numContacts());
                sb.append("\n");
                sb.append(b.toString());
                sb.append("\n");
            }
        }
        sb.append("Printing Routing Table Ended ******************** ");

        return sb.toString();
    }

}
