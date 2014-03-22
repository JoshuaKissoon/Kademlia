package kademlia.operation;

import java.io.IOException;
import java.util.List;
import kademlia.core.KadServer;
import kademlia.dht.KadContent;
import kademlia.message.Message;
import kademlia.message.StoreContentMessage;
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
    private final KadContent content;

    /**
     * @param server
     * @param localNode
     * @param content   The content to be stored on the DHT
     */
    public StoreOperation(KadServer server, Node localNode, KadContent content)
    {
        this.server = server;
        this.localNode = localNode;
        this.content = content;
    }

    @Override
    public synchronized void execute() throws IOException
    {
        /* Get the nodes on which we need to store the content */
        NodeLookupOperation ndlo = new NodeLookupOperation(this.server, this.localNode, this.content.getKey());
        ndlo.execute();
        List<Node> nodes = ndlo.getClosestNodes();

        System.out.println("Nodes to put content on: " + nodes);

        /* Create the message */
        Message msg = new StoreContentMessage(this.localNode, this.content);

        /*Store the message on all of the K-Nodes*/
        for (Node n : nodes)
        {
            if (n.equals(this.localNode))
            {
                /* @todo Store the content locally */
            }
            else
            {
                /**
                 * @todo Create a receiver that recieves a store acknowledgement message to count how many nodes a content have been stored at
                 */
                this.server.sendMessage(n, msg, null);
            }
        }
        
        System.out.println("\n\n\n\nSTORE CONTENT FINISHED");
    }

    /**
     * @return The number of nodes that have stored this content
     *
     * @todo Implement this method
     */
    public int numNodesStoredAt()
    {
        return 1;
    }
}
