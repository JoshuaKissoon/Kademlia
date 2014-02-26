package kademlia.operation;

import java.io.IOException;
import kademlia.core.KadServer;
import kademlia.node.Node;

/**
 * At each time interval t, nodes need to refresh their K-Buckets and their Data Storage
 * This Operation will manage refreshing the K-Buckets and data storage
 *
 * @author Joshua Kissoon
 * @created 20140224
 */
public class RefreshOperation implements Operation
{

    private final KadServer server;
    private final Node localNode;

    public RefreshOperation(KadServer server, Node localNode)
    {
        this.server = server;
        this.localNode = localNode;
    }

    @Override
    public synchronized void execute() throws IOException
    {
        /* @todo Do a Node Lookup operation to refresh K-Buckets */
        new NodeLookupOperation(this.server, this.localNode, this.localNode.getNodeId()).execute();

        /**
         * @todo Send data in DHT to closest Nodes if they don't have it
         * This is better than asking closest nodes for data,
         * since the data may not always come from the closest nodes
         */
        /**
         * @todo Delete any content on this node that this node is not one of the K-Closest nodes to
         * @todo Delete any expired content
         */
    }
}
