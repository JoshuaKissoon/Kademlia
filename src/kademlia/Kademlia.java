package kademlia;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;
import kademlia.core.DefaultConfiguration;
import kademlia.core.GetParameter;
import kademlia.core.KadConfiguration;
import kademlia.core.KadServer;
import kademlia.dht.DHT;
import kademlia.dht.KadContent;
import kademlia.exceptions.RoutingException;
import kademlia.message.MessageFactory;
import kademlia.node.Node;
import kademlia.node.NodeId;
import kademlia.operation.ConnectOperation;
import kademlia.operation.ContentLookupOperation;
import kademlia.operation.Operation;
import kademlia.operation.KadRefreshOperation;
import kademlia.operation.StoreOperation;
import kademlia.routing.RoutingTable;
import kademlia.serializer.JsonDHTSerializer;
import kademlia.serializer.JsonRoutingTableSerializer;
import kademlia.serializer.JsonSerializer;

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
 * @todo If we're trying to send a message to this node, just cancel the sending process and handle the message right here
 * @todo Keep this node in it's own routing table - it helps for ContentRefresh operation - easy to check whether this node is one of the k-nodes for a content
 * @todo Implement Kademlia.ping() operation.
 *
 */
public class Kademlia
{

    /* Kademlia Attributes */
    private final String ownerId;

    /* Objects to be used */
    private final transient Node localNode;
    private final transient KadServer server;
    private final transient DHT dht;
    private final transient Timer timer;
    private final int udpPort;
    private KadConfiguration config;

    /* Factories */
    private final transient MessageFactory messageFactory;

    /**
     * Creates a Kademlia DistributedMap using the specified name as filename base.
     * If the id cannot be read from disk the specified defaultId is used.
     * The instance is bootstraped to an existing network by specifying the
     * address of a bootstrap node in the network.
     *
     * @param ownerId   The Name of this node used for storage
     * @param localNode The Local Node for this Kad instance
     * @param udpPort   The UDP port to use for routing messages
     * @param dht       The DHT for this instance
     * @param config
     *
     * @throws IOException If an error occurred while reading id or local map
     *                     from disk <i>or</i> a network error occurred while
     *                     attempting to bootstrap to the network
     * */
    public Kademlia(String ownerId, Node localNode, int udpPort, DHT dht, KadConfiguration config) throws IOException
    {
        this.ownerId = ownerId;
        this.udpPort = udpPort;
        this.localNode = localNode;
        this.dht = dht;
        this.config = config;
        this.messageFactory = new MessageFactory(localNode, this.dht, this.config);
        this.server = new KadServer(udpPort, this.messageFactory, this.localNode, this.config);
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
                            /* Runs a DHT RefreshOperation  */
                            Kademlia.this.refresh();
                        }
                        catch (IOException e)
                        {
                            System.err.println("Refresh Operation Failed; Message: " + e.getMessage());
                        }
                    }
                },
                // Delay                        // Interval
                this.config.restoreInterval(), this.config.restoreInterval()
        );
    }

    public Kademlia(String ownerId, NodeId defaultId, int udpPort, KadConfiguration config) throws IOException
    {
        this(ownerId, new Node(defaultId, InetAddress.getLocalHost(), udpPort), udpPort, new DHT(ownerId, config), config);
    }

    public Kademlia(String ownerId, NodeId defaultId, int udpPort) throws IOException
    {
        this(ownerId, new Node(defaultId, InetAddress.getLocalHost(), udpPort), udpPort, new DHT(ownerId, new DefaultConfiguration()), new DefaultConfiguration());
    }

    /**
     * Load Stored state using default configuration
     *
     * @param ownerId The ID of the owner for the stored state
     *
     * @return A Kademlia instance loaded from a stored state in a file
     *
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     *
     * @todo Boot up this Kademlia instance from a saved file state
     */
    public static Kademlia loadFromFile(String ownerId) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        return Kademlia.loadFromFile(ownerId, new DefaultConfiguration());
    }

    /**
     * Load Stored state
     *
     * @param ownerId The ID of the owner for the stored state
     * @param iconfig Configuration information to work with
     *
     * @return A Kademlia instance loaded from a stored state in a file
     *
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     *
     * @todo Boot up this Kademlia instance from a saved file state
     */
    public static Kademlia loadFromFile(String ownerId, KadConfiguration iconfig) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        DataInputStream din;

        /**
         * @section Read Basic Kad data
         */
        din = new DataInputStream(new FileInputStream(getStateStorageFolderName(ownerId, iconfig) + File.separator + "kad.kns"));
        Kademlia ikad = new JsonSerializer<Kademlia>().read(din);

        /**
         * @section Read the routing table
         */
        din = new DataInputStream(new FileInputStream(getStateStorageFolderName(ownerId, iconfig) + File.separator + "routingtable.kns"));
        RoutingTable irtbl = new JsonRoutingTableSerializer().read(din);

        /**
         * @section Read the node state
         */
        din = new DataInputStream(new FileInputStream(getStateStorageFolderName(ownerId, iconfig) + File.separator + "node.kns"));
        Node inode = new JsonSerializer<Node>().read(din);
        inode.setRoutingTable(irtbl);

        /**
         * @section Read the DHT
         */
        din = new DataInputStream(new FileInputStream(getStateStorageFolderName(ownerId, iconfig) + File.separator + "dht.kns"));
        DHT idht = new JsonDHTSerializer().read(din);

        return new Kademlia(ownerId, inode, ikad.getPort(), idht, ikad.getCurrentConfiguration());
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
     * @return The DHT for this kad instance
     */
    public DHT getDHT()
    {
        return this.dht;
    }

    /**
     * @return The current KadConfiguration object being used
     */
    public KadConfiguration getCurrentConfiguration()
    {
        return this.config;
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
    public synchronized final void bootstrap(Node n) throws IOException, RoutingException
    {
        Operation op = new ConnectOperation(this.server, this.localNode, n, this.config);
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
    public synchronized int put(KadContent content) throws IOException
    {
        StoreOperation sop = new StoreOperation(this.server, this.localNode, content, this.dht, this.config);
        sop.execute();

        /* Return how many nodes the content was stored on */
        return sop.numNodesStoredAt();
    }

    /**
     * Store a content on the local node's DHT
     *
     * @param content The content to put on the DHT
     *
     * @throws java.io.IOException
     */
    public synchronized void putLocally(KadContent content) throws IOException
    {
        this.dht.store(content);
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
            contentFound = new ArrayList<>();
            contentFound.add(this.dht.get(param));
        }
        else
        {
            /* Seems like it doesn't exist in our DHT, get it from other Nodes */
            ContentLookupOperation clo = new ContentLookupOperation(server, localNode, param, numResultsReq, this.config);
            clo.execute();
            contentFound = clo.getContentFound();
        }

        return contentFound;
    }

    /**
     * Allow the user of the System to call refresh even out of the normal Kad refresh timing
     *
     * @throws java.io.IOException
     */
    public void refresh() throws IOException
    {
        new KadRefreshOperation(this.server, this.localNode, this.dht, this.config).execute();
    }

    /**
     * @return String The ID of the owner of this local network
     */
    public String getOwnerId()
    {
        return this.ownerId;
    }

    /**
     * @return Integer The port on which this kad instance is running
     */
    public int getPort()
    {
        return this.udpPort;
    }

    /**
     * Here we handle properly shutting down the Kademlia instance
     *
     * @param saveState Whether to save the application state or not
     *
     * @throws java.io.FileNotFoundException
     */
    public void shutdown(final boolean saveState) throws IOException
    {
        /* Shut down the server */
        this.server.shutdown();

        /* Save this Kademlia instance's state if required */
        if (saveState)
        {
            /* Save the system state */
            this.saveKadState();
        }
    }

    /**
     * Saves the node state to a text file
     *
     * @throws java.io.FileNotFoundException
     */
    private void saveKadState() throws IOException
    {
        DataOutputStream dout;

        /**
         * @section Store Basic Kad data
         */
        dout = new DataOutputStream(new FileOutputStream(getStateStorageFolderName(this.ownerId, this.config) + File.separator + "kad.kns"));
        new JsonSerializer<Kademlia>().write(this, dout);

        /**
         * @section Save the node state
         */
        dout = new DataOutputStream(new FileOutputStream(getStateStorageFolderName(this.ownerId, this.config) + File.separator + "node.kns"));
        new JsonSerializer<Node>().write(this.localNode, dout);

        /**
         * @section Save the routing table
         * We need to save the routing table separate from the node since the routing table will contain the node and the node will contain the routing table
         * This will cause a serialization recursion, and in turn a Stack Overflow
         */
        dout = new DataOutputStream(new FileOutputStream(getStateStorageFolderName(this.ownerId, this.config) + File.separator + "routingtable.kns"));
        new JsonRoutingTableSerializer().write(this.localNode.getRoutingTable(), dout);

        /**
         * @section Save the DHT
         */
        dout = new DataOutputStream(new FileOutputStream(getStateStorageFolderName(this.ownerId, this.config) + File.separator + "dht.kns"));
        new JsonDHTSerializer().write(this.dht, dout);

    }

    /**
     * Get the name of the folder for which a content should be stored
     *
     * @return String The name of the folder to store node states
     */
    private static String getStateStorageFolderName(String ownerId, KadConfiguration iconfig)
    {
        /* Setup the nodes storage folder if it doesn't exist */
        String path = iconfig.getNodeDataFolder(ownerId) + File.separator + "nodeState";
        File nodeStateFolder = new File(path);
        if (!nodeStateFolder.isDirectory())
        {
            nodeStateFolder.mkdir();
        }
        return nodeStateFolder.toString();
    }

    /**
     * Creates a string containing all data about this Kademlia instance
     *
     * @return The string representation of this Kad instance
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("\n\nPrinting Kad State for instance with owner: ");
        sb.append(this.ownerId);
        sb.append("\n\n");

        sb.append("\n");
        sb.append("Local Node");
        sb.append(this.localNode);
        sb.append("\n");

        sb.append("\n");
        sb.append("Routing Table: ");
        sb.append(this.localNode.getRoutingTable());
        sb.append("\n");

        sb.append("\n");
        sb.append("DHT: ");
        sb.append(this.dht);
        sb.append("\n");

        sb.append("\n\n\n");

        return sb.toString();
    }
}
