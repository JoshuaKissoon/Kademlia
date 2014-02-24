package kademlia.core;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;
import kademlia.dht.DHTContent;
import kademlia.exceptions.RoutingException;
import kademlia.message.MessageFactory;
import kademlia.node.Node;
import kademlia.node.NodeId;
import kademlia.operation.ConnectOperation;
import kademlia.operation.Operation;
import kademlia.operation.RefreshOperation;

/**
 * The main Kademlia network management class
 *
 * @author Joshua Kissoon
 * @since 20140215
 */
public class Kademlia
{

    /* Kademlia Attributes */
    private final String ownerId;

    /* Objects to be used */
    private final Node localNode;
    private final KadServer server;
    private final Timer timer;

    /* Factories */
    private final MessageFactory messageFactory;

    /**
     * Creates a Kademlia DistributedMap using the specified name as filename base.
     * If the id cannot be read from disk the specified defaultId is used.
     * The instance is bootstraped to an existing network by specifying the
     * address of a bootstrap node in the network.
     *
     * @param ownerId   The Name of this node used for storage
     * @param defaultId Default id for the node
     * @param udpPort   The UDP port to use for routing messages
     *
     * @throws IOException If an error occurred while reading id or local map
     *                     from disk <i>or</i> a network error occurred while
     *                     attempting to connect to the network
     * */
    public Kademlia(String ownerId, NodeId defaultId, int udpPort) throws IOException
    {
        this.ownerId = ownerId;
        this.localNode = new Node(defaultId, InetAddress.getLocalHost(), udpPort);
        this.messageFactory = new MessageFactory(localNode);
        this.server = new KadServer(udpPort, this.messageFactory, this.localNode);
        this.timer = new Timer(true);

        /* Schedule Recurring RestoreOperation */
        timer.schedule(
                new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            /* Runs a RefreshOperation to refresh K-Buckets and stored content */
                            new RefreshOperation(server, localNode).execute();
                        }
                        catch (IOException e)
                        {
                            System.err.println("Refresh Operation Failed; Message: " + e.getMessage());
                        }
                    }
                },
                // Delay                        // Interval
                Configuration.RESTORE_INTERVAL, Configuration.RESTORE_INTERVAL
        );
    }

    /**
     * @return Node The local node for this system
     */
    public Node getNode()
    {
        return this.localNode;
    }

    /**
     * @return The KadServer used to send/receive messages
     */
    public KadServer getServer()
    {
        return this.server;
    }

    /**
     * Connect to an existing peer-to-peer network.
     *
     * @param n The known node in the peer-to-peer network
     *
     * @throws RoutingException      If the bootstrap node could not be contacted
     * @throws IOException           If a network error occurred
     * @throws IllegalStateException If this object is closed
     * */
    public final void connect(Node n) throws IOException, RoutingException
    {
        Operation op = new ConnectOperation(this.server, this.localNode, n);
        op.execute();
    }

    /**
     * Stores the specified value under the given key
     * This value is stored on K nodes on the network, or all nodes if there are > K total nodes in the network
     *
     * @param content The content to put onto the DHT
     *
     */
    public boolean put(DHTContent content)
    {
        
        return false;
    }

    /**
     * Get some content stored on the DHT
     *
     * @param key The key of this content
     *
     * @return DHTContent The content
     */
    public DHTContent get(NodeId key)
    {
        return null;
    }
}
