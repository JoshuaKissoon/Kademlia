package kademlia.operation;

import java.io.IOException;
import kademlia.core.KadServer;
import kademlia.node.Node;
import kademlia.node.NodeId;

/**
 * At each time interval t, nodes need to refresh their K-Buckets
 * This operation takes care of refreshing this node's K-Buckets
 *
 * @author Joshua Kissoon
 * @created 20140224
 */
public class BucketRefreshOperation implements Operation
{

    private final KadServer server;
    private final Node localNode;

    public BucketRefreshOperation(KadServer server, Node localNode)
    {
        this.server = server;
        this.localNode = localNode;
    }

    /**
     * Each bucket need to be refreshed at every time interval t.
     * Find an identifier in each bucket's range, use it to look for nodes closest to this identifier
     * allowing the bucket to be refreshed.
     *
     * Then Do a NodeLookupOperation for each of the generated NodeIds,
     * This will find the K-Closest nodes to that ID, and update the necessary K-Bucket
     *
     * @throws java.io.IOException
     */
    @Override
    public synchronized void execute() throws IOException
    {
        for (int i = 1; i < NodeId.ID_LENGTH; i++)
        {
            /* Construct a NodeId that is i bits away from the current node Id */
            NodeId current = this.localNode.getNodeId().generateNodeIdByDistance(i);

            // System.out.println("Distance: " + localNode.getNodeId().getDistance(current) + " - ID: " + current);

            /* Run the Node Lookup Operation */
            new NodeLookupOperation(this.server, this.localNode, this.localNode.getNodeId()).execute();
        }
    }
}
