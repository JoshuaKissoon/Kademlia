package kademlia.dht;

/**
 * A StorageEntry class that is used to store a content on the DHT
 *
 * @author Joshua Kissoon
 * @since 20140402
 */
public class StorageEntry
{

    private final String content;
    private final StorageEntryMetadata metadata;

    public StorageEntry(KadContent content)
    {
        this(content, new StorageEntryMetadata(content));
    }

    public StorageEntry(KadContent content, StorageEntryMetadata metadata)
    {
        this.content = new String(content.toBytes());
        this.metadata = metadata;
    }
    
    public String getContent()
    {
        return this.content;
    }

    public StorageEntryMetadata getContentMetadata()
    {
        return this.metadata;
    }
}
