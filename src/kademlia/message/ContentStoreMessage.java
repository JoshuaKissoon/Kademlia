package kademlia.message;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import kademlia.dht.KadContent;
import kademlia.node.Node;

/**
 * A Message used to send a content store request to another DHT
 *
 * @author Joshua Kissoon
 * @since 20140224
 */
public class ContentStoreMessage implements Message
{

    private final Node origin;
    private final KadContent content;

    public final static byte CODE = 0x23;

    /**
     * @param origin  Where did this content come from - it'll always be the local node
     * @param content The Content to send
     */
    public ContentStoreMessage(Node origin, KadContent content)
    {
        this.origin = origin;
        this.content = content;
    }

    @Override
    public byte code()
    {
        return CODE;
    }

    @Override
    public void fromStream(DataInputStream in)
    {

    }

    @Override
    public void toStream(DataOutputStream out)
    {
        /* @todo write the origin and the content to the stream */
    }

    public Node getOrigin()
    {
        return this.origin;
    }

    public KadContent getContent()
    {
        return this.content;
    }
}
