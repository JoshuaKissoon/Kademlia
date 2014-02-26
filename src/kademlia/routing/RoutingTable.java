/**
 * @author Joshua Kissoon
 * @created 20140215
 * @desc Implementation of a Kademlia routing table
 */
package kademlia.routing;

import java.util.ArrayList;
import java.util.List;
import kademlia.node.Node;
import kademlia.node.NodeId;

public class RoutingTable
{

    private final Node localNode;  // The current node
    private final KadBucket[] buckets;

    
    {
        buckets = new KadBucket[NodeId.ID_LENGTH];  // 160 buckets; 1 for each level in the tree
    }

    public RoutingTable(Node localNode)
    {
        this.localNode = localNode;

        /* Initialize all of the buckets to a specific depth */
        for (int i = 0; i < NodeId.ID_LENGTH; i++)
        {
            buckets[i] = new KadBucket(i);
        }
    }

    /**
     * Adds a new node to the routing table
     *
     * @param n The contact to add
     */
    public void insert(Node n)
    {
        /* Find the first set bit: how far this node is away from the contact node */
        NodeId id = this.localNode.getNodeId().xor(n.getNodeId());
        //System.out.println(" First Bit Set: " + id.getFirstSetBitIndex());
        int bucketId = id.getFirstSetBitIndex();

        System.out.println(this.localNode.getNodeId() + " Adding Node " + n.getNodeId() + " to bucket at depth: " + bucketId);

        /* Put this contact to the bucket that stores contacts prefixLength distance away */
        this.buckets[bucketId].insert(n);
    }

    /**
     * Remove a node from the routing table
     *
     * @param n The node to remove
     */
    public void remove(Node n)
    {
        /* Find the first set bit: how far this node is away from the contact node */
        int bucketId = this.localNode.getNodeId().xor(n.getNodeId()).getFirstSetBitIndex();

        /* If the bucket has the contact, remove it */
        if (this.buckets[bucketId].containNode(n))
        {
            this.buckets[bucketId].removeNode(n);
        }
    }

    /**
     * Find the closest set of contacts to a given NodeId
     *
     * @param target The NodeId to find contacts close to
     * @param num    The number of contacts to find
     *
     * @return List<Contact> A List of num contacts closest to target
     */
    public List<Node> findClosest(NodeId target, int num)
    {
        List<Node> closest = new ArrayList<>(num);

        /* Get the bucket number to search for closest from */
        int bucketNumber = this.localNode.getNodeId().xor(target).getFirstSetBitIndex() - 1;

        /* Add the contacts from this bucket to the return contacts */
        for (Node c : this.buckets[bucketNumber].getNodes())
        {
            if (closest.size() < num)
            {
                closest.add(c);
            }
            else
            {
                break;
            }
        }

        if (closest.size() >= num)
        {
            return closest;
        }

        /* If we still need more nodes, we add from buckets on either side of the closest bucket */
        for (int i = 1; ((bucketNumber - i) >= 0 || (bucketNumber + i) < NodeId.ID_LENGTH); i++)
        {
            /* Check the bucket on the left side */
            if (bucketNumber - i > 0)
            {
                for (Node c : this.buckets[bucketNumber - i].getNodes())
                {
                    if (closest.size() < num)
                    {
                        closest.add(c);
                    }
                    else
                    {
                        break;
                    }
                }
            }

            /* Check the bucket on the right side */
            if (bucketNumber + i < NodeId.ID_LENGTH)
            {
                for (Node c : this.buckets[bucketNumber + i].getNodes())
                {
                    if (closest.size() < num)
                    {
                        closest.add(c);
                    }
                    else
                    {
                        break;
                    }
                }
            }

            /* If we have enough contacts, then stop adding */
            if (closest.size() >= num)
            {
                break;
            }
        }

        return closest;
    }

    public List<Node> getAllNodes()
    {
        List<Node> nodes = new ArrayList<>();

        for (KadBucket b : this.buckets)
        {
            nodes.addAll(b.getNodes());
        }

        return nodes;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("\nPrinting Routing Table Started ***************** \n");
        for (KadBucket b : this.buckets)
        {
            // System.out.println("Bucket: " + b);
            if (b.numNodes() > 0)
            {
                sb.append("# nodes in Bucket with depth ");
                sb.append(b.getDepth());
                sb.append(": ");
                sb.append(b.numNodes());
                sb.append("\n");
                sb.append(b.toString());
                sb.append("\n");
            }
        }
        sb.append("\nPrinting Routing Table Ended ******************** ");

        return sb.toString();
    }

}
