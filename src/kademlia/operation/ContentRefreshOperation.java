package kademlia.operation;

import java.io.IOException;
import java.util.List;
import kademlia.core.Configuration;
import kademlia.core.KadServer;
import kademlia.dht.DHT;
import kademlia.dht.StorageEntry;
import kademlia.message.Message;
import kademlia.message.StoreContentMessage;
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
     *
     * @throws java.io.IOException
     */
    @Override
    public void execute() throws IOException
    {
        /* Get a list of all storage entries for content */
        List<StorageEntry> entries = this.dht.getStorageEntries();

        /* For each storage entry, distribute it */
        for (StorageEntry e : entries)
        {
            /**
             * @todo - Paper improvement 1 -
             * Check last update time of this entry and
             * only distribute it if it has been last updated > 1 hour ago
             */
            /* Get the K closest nodes to this entries */
            List<Node> closestNodes = this.localNode.getRoutingTable().findClosest(e.getKey(), Configuration.K);

            /* Create the message */
            Message msg = new StoreContentMessage(this.localNode, dht.get(e));

            /*Store the message on all of the K-Nodes*/
            for (Node n : closestNodes)
            {
                /*We don't need to again store the content locally, it's already here*/
                if (!n.equals(this.localNode))
                {
                    /* Send a contentstore operation to the K-Closest nodes */
                    this.server.sendMessage(n, msg, null);
                }
            }

            /**
             * @todo Delete any content on this node that this node is not one of the K-Closest nodes to
             */
        }

    }
}
