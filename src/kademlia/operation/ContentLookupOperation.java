package kademlia.operation;

import kademlia.core.KadServer;
import kademlia.node.Node;
import kademlia.node.NodeId;

/**
 * Looks up a specified identifier and returns the value associated with it
 *
 * @author Joshua Kissoon
 * @since 20140226
 */
public class ContentLookupOperation implements Operation
{

    private final KadServer server;
    private final Node localNode;
    private final NodeId key;

    /**
     * @param server
     * @param localNode
     * @param key       The key for the content which we need to find
     */
    public ContentLookupOperation(KadServer server, Node localNode, NodeId key)
    {
        this.server = server;
        this.localNode = localNode;
        this.key = key;
    }
}
