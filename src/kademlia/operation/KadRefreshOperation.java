package kademlia.operation;

import java.io.IOException;
import kademlia.core.KadServer;
import kademlia.dht.DHT;
import kademlia.node.Node;

/**
 * An operation that handles refreshing the entire Kademlia Systems including buckets and content
 *
 * @author Joshua Kissoon
 * @since 20140306
 */
public class KadRefreshOperation implements Operation
{

    private final KadServer server;
    private final Node localNode;
    private final DHT dht;

    public KadRefreshOperation(KadServer server, Node localNode, DHT dht)
    {
        this.server = server;
        this.localNode = localNode;
        this.dht = dht;
    }

    @Override
    public void execute() throws IOException
    {
        /* Run our BucketRefreshOperation to refresh buckets */
        new BucketRefreshOperation(server, localNode).execute();

        /* After buckets have been refreshed, we refresh content */
        new ContentRefreshOperation(server, localNode, dht).execute();
    }
}
