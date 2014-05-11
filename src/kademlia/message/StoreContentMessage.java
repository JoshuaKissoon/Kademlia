package kademlia.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import kademlia.dht.StorageEntry;
import kademlia.node.Node;
import kademlia.util.serializer.JsonSerializer;

/**
 * A StoreContentMessage used to send a store message to a node
 *
 * @author Joshua Kissoon
 * @since 20140225
 */
public class StoreContentMessage implements Message
{

    public static final byte CODE = 0x08;

    private StorageEntry content;
    private Node origin;

    /**
     * @param origin  Where the message came from
     * @param content The content to be stored
     *
     */
    public StoreContentMessage(Node origin, StorageEntry content)
    {
        this.content = content;
        this.origin = origin;
    }

    public StoreContentMessage(DataInputStream in) throws IOException
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
            e.printStackTrace();
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
        return "StoreContentMessage[origin=" + origin + ",content=" + content + "]";
    }
}
