package kademlia.operation;

import java.io.IOException;
import java.util.List;
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

    @Override
    public synchronized void execute() throws IOException
    {
        System.out.println("Bucket Refresh Operation Started");
        
        /* Get a list of NodeIds for each distance from the LocalNode NodeId */
        List<NodeId> refreshIds = this.localNode.getRoutingTable().getRefreshList();

        /* Test whether each nodeId in this list is a different distance from our current NID */
        for (NodeId nid : refreshIds)
        {
            System.out.println(localNode.getNodeId().getDistance(nid));
        }

        /* @todo Do a Node Lookup operation to refresh K-Buckets */
        new NodeLookupOperation(this.server, this.localNode, this.localNode.getNodeId()).execute();

        /**
         * @todo Send data in DHT to closest Nodes if they don't have it
         * This is better than asking closest nodes for data,
         * since the data may not always come from the closest nodes
         */
    }
}
