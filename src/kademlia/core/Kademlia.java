package kademlia.core;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;
import kademlia.dht.DHT;
import kademlia.dht.KadContent;
import kademlia.exceptions.RoutingException;
import kademlia.message.MessageFactory;
import kademlia.node.Node;
import kademlia.node.NodeId;
import kademlia.operation.ConnectOperation;
import kademlia.operation.ContentLookupOperation;
import kademlia.operation.Operation;
import kademlia.operation.RefreshOperation;
import kademlia.operation.StoreOperation;

/**
 * The main Kademlia network management class
 *
 * @author Joshua Kissoon
 * @since 20140215
 *
 * @todo When we receive a store message - if we have a newer version of the content, re-send this newer version to that node so as to update their version
 * @todo Handle IPv6 Addresses
 * @todo Handle compressing data
 * @todo Allow optional storing of content locally using the put method
 * @todo Instead of using a StoreContentMessage to send a store RPC and a ContentMessage to receive a FIND rpc, make them 1 message with different operation type
 */
public class Kademlia
{

    /* Kademlia Attributes */
    private final String ownerId;

    /* Objects to be used */
    private final Node localNode;
    private final KadServer server;
    private final DHT dht;
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
        this.dht = new DHT();
        this.messageFactory = new MessageFactory(localNode, this.dht);
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
     * @return Integer How many nodes the content was stored on
     *
     * @throws java.io.IOException
     *
     */
    public int put(KadContent content) throws IOException
    {
        new StoreOperation(server, localNode, content).execute();
        /*@todo Return how many nodes the content was stored on */
        return 10;
    }

    /**
     * Get some content stored on the DHT
     * The content returned is a JSON String in byte format; this string is parsed into a class
     *
     * @param param         The parameters used to search for the content
     * @param numResultsReq How many results are required from different nodes
     *
     * @return DHTContent The content
     *
     * @throws java.io.IOException
     */
    public List<KadContent> get(GetParameter param, int numResultsReq) throws NoSuchElementException, IOException
    {
        List contentFound;
        if (this.dht.contains(param))
        {
            /* If the content exist in our own DHT, then return it. */
            System.out.println("Found content locally");
            contentFound = new ArrayList<>();
            contentFound.add(this.dht.get(param));
        }
        else
        {
            /* Seems like it doesn't exist in our DHT, get it from other Nodes */
            System.out.println("Looking for content on foreign nodes");
            ContentLookupOperation clo = new ContentLookupOperation(server, localNode, param, numResultsReq);
            clo.execute();
            contentFound = clo.getContentFound();
        }

        return contentFound;
    }

    /**
     * @return String The ID of the owner of this local network
     */
    public String getOwnerId()
    {
        return this.ownerId;
    }
}
