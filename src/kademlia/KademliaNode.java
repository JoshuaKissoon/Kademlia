package kademlia;

import java.io.IOException;
import java.util.NoSuchElementException;
import kademlia.dht.GetParameter;
import kademlia.dht.DHT;
import kademlia.dht.KadContent;
import kademlia.dht.StorageEntry;
import kademlia.exceptions.ContentNotFoundException;
import kademlia.exceptions.RoutingException;
import kademlia.node.Node;
import kademlia.routing.RoutingTable;

/**
 * The main Kademlia Node on the network, this node manages everything for this local system.
 *
 * @author Joshua Kissoon
 * @since 20140523
 *
 */
public interface KademliaNode
{

    /**
     * Schedule the recurring refresh operation
     */
    public void startRefreshOperation();

    /**
     * Stop the recurring refresh operation
     */
    public void stopRefreshOperation();

    /**
     * @return Node The local node for this system
     */
    public Node getNode();

    /**
     * @return The KadServer used to send/receive messages
     */
    public KadServer getServer();

    /**
     * @return The DHT for this kad instance
     */
    public DHT getDHT();

    /**
     * @return The current KadConfiguration object being used
     */
    public KadConfiguration getCurrentConfiguration();

    /**
     * Connect to an existing peer-to-peer network.
     *
     * @param n The known node in the peer-to-peer network
     *
     * @throws RoutingException      If the bootstrap node could not be contacted
     * @throws IOException           If a network error occurred
     * @throws IllegalStateException If this object is closed
     * */
    public void bootstrap(Node n) throws IOException, RoutingException;

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
    public int put(KadContent content) throws IOException;

    /**
     * Stores the specified value under the given key
     * This value is stored on K nodes on the network, or all nodes if there are > K total nodes in the network
     *
     * @param entry The StorageEntry with the content to put onto the DHT
     *
     * @return Integer How many nodes the content was stored on
     *
     * @throws java.io.IOException
     *
     */
    public int put(StorageEntry entry) throws IOException;

    /**
     * Store a content on the local node's DHT
     *
     * @param content The content to put on the DHT
     *
     * @throws java.io.IOException
     */
    public void putLocally(KadContent content) throws IOException;

    /**
     * Get some content stored on the DHT
     *
     * @param param The parameters used to search for the content
     *
     * @return DHTContent The content
     *
     * @throws java.io.IOException
     * @throws kademlia.exceptions.ContentNotFoundException
     */
    public StorageEntry get(GetParameter param) throws NoSuchElementException, IOException, ContentNotFoundException;

    /**
     * Allow the user of the System to call refresh even out of the normal Kad refresh timing
     *
     * @throws java.io.IOException
     */
    public void refresh() throws IOException;

    /**
     * @return String The ID of the owner of this local network
     */
    public String getOwnerId();

    /**
     * @return Integer The port on which this kad instance is running
     */
    public int getPort();

    /**
     * Here we handle properly shutting down the Kademlia instance
     *
     * @param saveState Whether to save the application state or not
     *
     * @throws java.io.FileNotFoundException
     */
    public void shutdown(final boolean saveState) throws IOException;

    /**
     * Saves the node state to a text file
     *
     * @throws java.io.FileNotFoundException
     */
    public void saveKadState() throws IOException;

    /**
     * @return The routing table for this node.
     */
    public RoutingTable getRoutingTable();

    /**
     * @return The statistician that manages all statistics
     */
    public KadStatistician getStatistician();
}
