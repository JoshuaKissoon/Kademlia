package kademlia.dht;

/**
 * A StorageEntry interface for the storage entry class used to store a content on the DHT
 *
 * @author Joshua Kissoon
 * @since 20140523
 */
public interface KademliaStorageEntry
{

    /**
     * Add the content to the storage entry
     *
     * @param data The content data in byte[] format
     */
    public void setContent(final byte[] data);

    /**
     * Get the content from this storage entry
     *
     * @return The content in byte format
     */
    public byte[] getContent();

    /**
     * Get the metadata for this storage entry
     *
     * @return the storage entry metadata
     */
    public KademliaStorageEntryMetadata getContentMetadata();
}
