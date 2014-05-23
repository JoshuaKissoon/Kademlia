package kademlia.dht;

/**
 * A StorageEntry interface for the storage entry class used to store a content on the DHT
 *
 * @author Joshua Kissoon
 * @since 20140523
 */
public interface KademliaStorageEntry
{

    public void setContent(final byte[] data);

    public byte[] getContent();

    public StorageEntryMetadata getContentMetadata();
}
