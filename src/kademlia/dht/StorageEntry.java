package kademlia.dht;

/**
 * A StorageEntry class that is used to store a content on the DHT
 *
 * @author Joshua Kissoon
 * @since 20140402
 */
public class StorageEntry
{

    private byte[] content;
    private final StorageEntryMetadata metadata;

    public StorageEntry(KadContent content)
    {
        this(content, new StorageEntryMetadata(content));
    }

    public StorageEntry(KadContent content, StorageEntryMetadata metadata)
    {
        this.content = content.toBytes();
        this.metadata = metadata;
    }

    public void setContent(byte[] data)
    {
        this.content = data;
    }

    public byte[] getContent()
    {
        return this.content;
    }

    public StorageEntryMetadata getContentMetadata()
    {
        return this.metadata;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("[StorageEntry: ");

        sb.append("[Content: ");
        sb.append(this.getContent());
        sb.append("]");

        sb.append(this.getContentMetadata());

        sb.append("]");

        return sb.toString();
    }
}
