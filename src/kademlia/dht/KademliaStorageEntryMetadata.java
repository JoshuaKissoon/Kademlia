package kademlia.dht;

import kademlia.node.KademliaId;

/**
 * Keeps track of data for a Content stored in the DHT
 * Used by the StorageEntryManager class
 *
 * @author Joshua Kissoon
 * @since 20140226
 */
public interface KademliaStorageEntryMetadata
{

    /**
     * @return The Kademlia ID of this content
     */
    public KademliaId getKey();

    /**
     * @return The content's owner ID
     */
    public String getOwnerId();

    /**
     * @return The type of this content
     */
    public String getType();

    /**
     * @return A hash of the content
     */
    public int getContentHash();

    /**
     * @return The last time this content was updated
     */
    public long getLastUpdatedTimestamp();

    /**
     * When a node is looking for content, he sends the search criteria in a GetParameter object
     * Here we take this GetParameter object and check if this StorageEntry satisfies the given parameters
     *
     * @param params
     *
     * @return boolean Whether this content satisfies the parameters
     */
    public boolean satisfiesParameters(GetParameter params);

    /**
     * @return The timestamp for the last time this content was republished
     */
    public long lastRepublished();

    /**
     * Whenever we republish a content or get this content from the network, we update the last republished time
     */
    public void updateLastRepublished();
}
