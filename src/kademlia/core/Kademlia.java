package kademlia.core;

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
 * @todo Move DHT.getContentStorageFolderName to the Configuration class
 *
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
    private final int udpPort;

    /* Factories */
    private final MessageFactory messageFactory;

    /**
     * Creates a Kademlia DistributedMap using the specified name as filename base.
     * If the id cannot be read from disk the specified defaultId is used.
     * The instance is bootstraped to an existing network by specifying the
     * address of a bootstrap node in the network.
     *
     * @param ownerId   The Name of this node used for storage
     * @param localNode The Local Node for this Kad instance
     * @param udpPort   The UDP port to use for routing messages
     *
     * @throws IOException If an error occurred while reading id or local map
     *                     from disk <i>or</i> a network error occurred while
     *                     attempting to connect to the network
     * */
    public Kademlia(String ownerId, Node localNode, int udpPort, DHT dht) throws IOException
    {
        this.ownerId = ownerId;
        this.udpPort = udpPort;
        this.localNode = localNode;
        this.dht = dht;
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
                Configuration.RESTORE_INTERVAL, Configuration.RESTORE_INTERVAL
        );
    }

    public Kademlia(String ownerId, NodeId defaultId, int udpPort) throws IOException
    {
        this(ownerId, new Node(defaultId, InetAddress.getLocalHost(), udpPort), udpPort, new DHT());
    }

    /**
     * Load Stored state
     *
     * @param ownerId The ID of the owner for the stored state
     *
     * @return A Kademlia instance loaded from a stored state in a file
     *
     * @throws java.io.FileNotFoundException
     *
     * @todo Boot up this Kademlia instance from a saved file state
     */
    public static void loadFromFile(String ownerId) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        /* Setup the file in which we store the state */
        DataInputStream din = new DataInputStream(new FileInputStream(getStateStorageFolderName() + File.separator + ownerId + ".kns"));

        /* Read the UDP Port that this app is running on */
        Integer rPort = new JsonSerializer<Integer>().read(din);

        /* Read the node state */
        // Node rN = new JsonSerializer<Node>().read(din);

        /* Read the DHT */
        DHT rDht = new JsonSerializer<DHT>().read(din);

        //return new Kademlia(ownerId, rN, rPort, rDht);
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
        StoreOperation sop = new StoreOperation(server, localNode, content);
        sop.execute();

        /* Return how many nodes the content was stored on */
        return sop.numNodesStoredAt();
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
     * Allow the user of the System to call refresh even out of the normal Kad refresh timing
     *
     * @throws java.io.IOException
     */
    public void refresh() throws IOException
    {
        new KadRefreshOperation(this.server, this.localNode, this.dht).execute();
    }

    /**
     * @return String The ID of the owner of this local network
     */
    public String getOwnerId()
    {
        return this.ownerId;
    }

    /**
     * Here we handle properly shutting down the Kademlia instance
     *
     * @throws java.io.FileNotFoundException
     */
    public void shutdown() throws FileNotFoundException, IOException, ClassNotFoundException
    {
        /* Shut down the server */
        this.server.shutdown();

        /* Save this Kademlia instance's state if required */
        if (Configuration.SAVE_STATE_ON_SHUTDOWN)
        {
            /* Save the system state */
            this.saveKadState();

        }


        /* Now we store the content locally in a file */
    }

    /**
     * Saves the node state to a text file
     *
     * @throws java.io.FileNotFoundException
     */
    private void saveKadState() throws FileNotFoundException, IOException, ClassNotFoundException
    {
        /* Setup the file in which we store the state */
        DataOutputStream dout;
        dout = new DataOutputStream(new FileOutputStream(getStateStorageFolderName() + File.separator + this.ownerId + ".kns"));

        System.out.println("Saving state");
        /* Save the UDP Port that this app is running on */
        new JsonSerializer<Integer>().write(this.udpPort, dout);

        /* Save the node state */
        dout = new DataOutputStream(new FileOutputStream(getStateStorageFolderName() + File.separator + this.ownerId + ".kns"));
        new JsonSerializer<Node>().write(this.localNode, dout);

        /* Save the DHT */
       // dout = new DataOutputStream(new FileOutputStream(getStateStorageFolderName() + File.separator + this.ownerId + ".kns"));
        //new JsonSerializer<DHT>().write(this.dht, dout);
        
//        System.out.println(dht.getStorageEntries());
//        
//        DataInputStream din = new DataInputStream(new FileInputStream(getStateStorageFolderName() + File.separator + ownerId + ".kns"));
//        DHT dddht = new JsonSerializer<DHT>().read(din);
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println(dddht);
        System.out.println("FInished saving state");

    }

    /**
     * Get the name of the folder for which a content should be stored
     *
     * @return String The name of the folder to store node states
     */
    private static String getStateStorageFolderName()
    {
        String storagePath = System.getProperty("user.home") + File.separator + Configuration.LOCAL_FOLDER;
        File mainStorageFolder = new File(storagePath);

        /* Create the main storage folder if it doesn't exist */
        if (!mainStorageFolder.isDirectory())
        {
            mainStorageFolder.mkdir();
        }

        File contentStorageFolder = new File(mainStorageFolder + File.separator + "nodes");

        /* Create the content folder if it doesn't exist */
        if (!contentStorageFolder.isDirectory())
        {
            contentStorageFolder.mkdir();
        }

        return mainStorageFolder + File.separator + "nodes";
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

        sb.append("\n\n\n");

        return sb.toString();
    }
}
