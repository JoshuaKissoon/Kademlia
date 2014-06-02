package kademlia.operation;

import java.io.IOException;
import java.util.List;
import kademlia.KadConfiguration;
import kademlia.KadServer;
import kademlia.KademliaNode;
import kademlia.dht.KademliaDHT;
import kademlia.dht.KademliaStorageEntryMetadata;
import kademlia.dht.StorageEntryMetadata;
import kademlia.exceptions.ContentNotFoundException;
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
    private final KademliaNode localNode;
    private final KademliaDHT dht;
    private final KadConfiguration config;

    public ContentRefreshOperation(KadServer server, KademliaNode localNode, KademliaDHT dht, KadConfiguration config)
    {
        this.server = server;
        this.localNode = localNode;
        this.dht = dht;
        this.config = config;
    }

    /**
     * For each content stored on this DHT, distribute it to the K closest nodes
 Also delete the content if this node is no longer one of the K closest nodes

 We assume that our JKademliaRoutingTable is updated, and we can get the K closest nodes from that table
     *
     * @throws java.io.IOException
     */
    @Override
    public void execute() throws IOException
    {
        /* Get a list of all storage entries for content */
        List<KademliaStorageEntryMetadata> entries = this.dht.getStorageEntries();

        /* If a content was last republished before this time, then we need to republish it */
        final long minRepublishTime = (System.currentTimeMillis() / 1000L) - this.config.restoreInterval();

        /* For each storage entry, distribute it */
        for (KademliaStorageEntryMetadata e : entries)
        {
            /* Check last update time of this entry and only distribute it if it has been last updated > 1 hour ago */
            if (e.lastRepublished() > minRepublishTime)
            {
                continue;
            }

            /* Set that this content is now republished */
            e.updateLastRepublished();

            /* Get the K closest nodes to this entries */
            List<Node> closestNodes = this.localNode.getRoutingTable().findClosest(e.getKey(), this.config.k());

            /* Create the message */
            Message msg = new StoreContentMessage(this.localNode.getNode(), dht.get(e));

            /*Store the message on all of the K-Nodes*/
            for (Node n : closestNodes)
            {
                /*We don't need to again store the content locally, it's already here*/
                if (!n.equals(this.localNode.getNode()))
                {
                    /* Send a contentstore operation to the K-Closest nodes */
                    this.server.sendMessage(n, msg, null);
                }
            }

            /* Delete any content on this node that this node is not one of the K-Closest nodes to */
            try
            {
                if (!closestNodes.contains(this.localNode.getNode()))
                {
                    this.dht.remove(e);
                }
            }
            catch (ContentNotFoundException cnfe)
            {
                /* It would be weird if the content is not found here */
                System.err.println("ContentRefreshOperation: Removing content from local node, content not found... Message: " + cnfe.getMessage());
            }
        }

    }
}
