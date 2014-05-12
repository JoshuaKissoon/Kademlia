package kademlia.dht;

/**
 * A StorageEntry class that is used to store a content on the DHT
 *
 * @author Joshua Kissoon
 * @since 20140402
 */
public class StorageEntry
{

    private String content;
    private final StorageEntryMetadata metadata;

    public StorageEntry(final KadContent content)
    {
        this(content, new StorageEntryMetadata(content));
    }

    public StorageEntry(final KadContent content, final StorageEntryMetadata metadata)
    {
        this.setContent(content.toSerializedForm());
        this.metadata = metadata;
    }

    public final void setContent(final byte[] data)
    {
        this.content = new String(data);
    }

    public final byte[] getContent()
    {
        return this.content.getBytes();
    }

    public final StorageEntryMetadata getContentMetadata()
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
