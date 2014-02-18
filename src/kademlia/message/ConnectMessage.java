/**
 * @author Joshua Kissoon
 * @created 20140218
 * @desc A message used to connect nodes
 */
package kademlia.message;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import kademlia.node.Node;

public class ConnectMessage implements Message
{

    private Node origin;
    public static final byte CODE = 0x11;

    public ConnectMessage(Node origin)
    {
        this.origin = origin;
    }

    public ConnectMessage(DataInput in) throws IOException
    {
        this.fromStream(in);
    }

    @Override
    public final void fromStream(DataInput in) throws IOException
    {
        this.origin = new Node(in);
    }

    @Override
    public void toStream(DataOutput out) throws IOException
    {
        origin.toStream(out);
    }

    public Node getOrigin()
    {
        return this.origin;
    }

    @Override
    public byte code()
    {
        return CODE;
    }

    @Override
    public String toString()
    {
        return "ConnectMessage[origin NodeId=" + origin.getNodeId() + "]";
    }
}
