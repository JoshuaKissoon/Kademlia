package kademlia.operation;

import kademlia.core.KadServer;
import kademlia.dht.DHT;
import kademlia.node.Node;

/**
 * Refresh/Restore the data on this node by sending the data to the K-Closest nodes to the data
 *
 * @author Joshua Kissoon
 * @since 20140306
 */
public class ContentRefreshOperation implements Operation
{

    private final KadServer server;
    private final Node localNode;
    private final DHT dht;

    public ContentRefreshOperation(KadServer server, Node localNode, DHT dht)
    {
        this.server = server;
        this.localNode = localNode;
        this.dht = dht;
    }

    /**
     * For each content stored on this DHT, distribute it to the K closest nodes
     * Also delete the content if this node is no longer one of the K closest nodes
     *
     * We assume that our RoutingTable is updated, and we can get the K closest nodes from that table
     */
    @Override
    public void execute()
    {
        
        /**
         * @todo Delete any content on this node that this node is not one of the K-Closest nodes to
         */

    }
}
