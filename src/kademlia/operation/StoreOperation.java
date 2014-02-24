package kademlia.operation;

import java.io.IOException;
import java.util.ArrayList;
import kademlia.core.KadServer;
import kademlia.dht.DHTContent;
import kademlia.node.Node;

/**
 * Operation that stores a DHT Content onto the K closest nodes to the content Key
 *
 * @author Joshua Kissoon
 * @since 20140224
 */
public class StoreOperation implements Operation
{

    private final KadServer server;
    private final Node localNode;
    private final DHTContent content;

    /**
     * @param server
     * @param localNode
     * @param content   The content to be stored on the DHT
     */
    public StoreOperation(KadServer server, Node localNode, DHTContent content)
    {
        this.server = server;
        this.localNode = localNode;
        this.content = content;
    }

    @Override
    public synchronized Object execute() throws IOException
    {
        /* Get the nodes on which we need to store the content */
        ArrayList<Node> nodes = new NodeLookupOperation(this.server, this.localNode, this.content.getKey()).execute();
        return null;
    }
}
