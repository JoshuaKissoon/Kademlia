/**
 * @author Joshua Kissoon
 * @created 20140215
 * @desc The main Kademlia network management class
 */
package kademlia.core;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;
import kademlia.exceptions.RoutingException;
import kademlia.message.MessageFactory;
import kademlia.node.Node;
import kademlia.node.NodeId;
import kademlia.operation.ConnectOperation;
import kademlia.operation.Operation;

public class Kademlia
{

    /* Kademlia Attributes */
    private final String name;

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
     * @param name      The Name of this node used for storage
     * @param defaultId Default id for the node
     * @param udpPort   The UDP port to use for routing messages
     *
     * @throws IOException If an error occurred while reading id or local map
     *                     from disk <i>or</i> a network error occurred while
     *                     attempting to connect to the network
     * */
    public Kademlia(String name, NodeId defaultId, int udpPort) throws IOException
    {
        this.name = name;
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
                        /**
                         * @todo Create Operation that
                         * Refreshes all buckets and sends HashMessages to all nodes that are
                         * among the K closest to mappings stored at this node. Also deletes any
                         * mappings that this node is no longer among the K closest to.
                         * */
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
}
