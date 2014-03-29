package kademlia.core;

/**
 * Interface that defines a KadConfiguration object
 *
 * @author Joshua Kissoon
 * @since 20140329
 */
public interface KadConfiguration
{

    /**
     * @return Interval in milliseconds between execution of RestoreOperations.
     */
    public long restoreInterval();

    /**
     * If no reply received from a node in this period (in milliseconds)
     * consider the node unresponsive.
     *
     * @return The time it takes to consider a node unresponsive
     */
    public long responseTimeout();

    /**
     * @return Maximum number of milliseconds for performing an operation.
     */
    public long operationTimeout();

    /**
     * @return Maximum number of concurrent messages in transit.
     */
    public int maxConcurrentMessagesTransiting();

    /**
     * @return K-Value used throughout Kademlia
     */
    public int k();

    /**
     * @return Size of replacement cache.
     */
    public int replacementCacheSize();

    /**
     * @return # of times a node can be marked as stale before it is actually removed.
     */
    public int stale();

    /**
     * Creates the folder in which this node data is to be stored
     *
     * @param ownerId
     *
     * @return The folder path
     */
    public String getNodeDataFolder(String ownerId);
}
