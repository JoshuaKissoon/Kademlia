package kademlia.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import kademlia.dht.StorageEntry;
import kademlia.node.Node;
import kademlia.serializer.JsonSerializer;

/**
 * A Message used to send content between nodes
 *
 * @author Joshua Kissoon
 * @since 20140226
 */
public class ContentMessage implements Message
{

    public static final byte CODE = 0x04;

    private StorageEntry content;
    private Node origin;

    /**
     * @param origin  Where the message came from
     * @param content The content to be stored
     *
     */
    public ContentMessage(Node origin, StorageEntry content)
    {
        this.content = content;
        this.origin = origin;
    }

    public ContentMessage(DataInputStream in) throws IOException
    {
        this.fromStream(in);
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
        this.origin.toStream(out);

        /* Serialize the KadContent, then send it to the stream */
        new JsonSerializer<StorageEntry>().write(content, out);
    }

    @Override
    public final void fromStream(DataInputStream in) throws IOException
    {
        this.origin = new Node(in);

        try
        {
            this.content = new JsonSerializer<StorageEntry>().read(in);
        }
        catch (ClassNotFoundException e)
        {
            System.err.println("ClassNotFoundException when reading StorageEntry; Message: " + e.getMessage());
        }
    }

    public Node getOrigin()
    {
        return this.origin;
    }

    public StorageEntry getContent()
    {
        return this.content;
    }

    @Override
    public byte code()
    {
        return CODE;
    }

    @Override
    public String toString()
    {
        return "ContentMessage[origin=" + origin + ",content=" + content + "]";
    }
}
