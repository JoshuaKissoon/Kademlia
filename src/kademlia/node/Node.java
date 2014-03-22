package kademlia.node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import kademlia.message.Streamable;
import kademlia.routing.RoutingTable;

/**
 * A Node in the Kademlia network
 *
 * @author Joshua Kissoon
 * @since 20140202
 * @version 0.1
 */
public class Node implements Streamable
{

    private NodeId nodeId;
    private InetAddress inetAddress;
    private int port;
    private final String strRep;

    private transient RoutingTable routingTable;

    
    {
        this.routingTable = new RoutingTable(this);
    }

    public Node(NodeId nid, InetAddress ip, int port)
    {
        this.nodeId = nid;
        this.inetAddress = ip;
        this.port = port;
        this.strRep = this.nodeId.toString();
    }

    /**
     * Load the Node's data from a DataInput stream
     *
     * @param in
     *
     * @throws IOException
     */
    public Node(DataInputStream in) throws IOException
    {
        this.fromStream(in);
        this.strRep = this.nodeId.toString();
    }

    /**
     * Set the InetAddress of this node
     *
     * @param addr The new InetAddress of this node
     */
    public void setInetAddress(InetAddress addr)
    {
        this.inetAddress = addr;
    }

    /**
     * @return The NodeId object of this node
     */
    public NodeId getNodeId()
    {
        return this.nodeId;
    }

    /**
     * Creates a SocketAddress for this node
     *
     * @return
     */
    public SocketAddress getSocketAddress()
    {
        return new InetSocketAddress(this.inetAddress, this.port);
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
        /* Add the NodeId to the stream */
        this.nodeId.toStream(out);

        /* Add the Node's IP address to the stream */
        byte[] a = inetAddress.getAddress();
        if (a.length != 4)
        {
            throw new RuntimeException("Expected InetAddress of 4 bytes, got " + a.length);
        }
        out.write(a);

        /* Add the port to the stream */
        out.writeInt(port);
    }

    @Override
    public final void fromStream(DataInputStream in) throws IOException
    {
        /* Load the NodeId */
        this.nodeId = new NodeId(in);

        /* Load the IP Address */
        byte[] ip = new byte[4];
        in.readFully(ip);
        this.inetAddress = InetAddress.getByAddress(ip);

        /* Read in the port */
        this.port = in.readInt();
    }

    /**
     * @return The RoutingTable of this Node
     */
    public RoutingTable getRoutingTable()
    {
        return this.routingTable;
    }

    /**
     * Sets a new routing table to this node, mainly used when we retrieve the node from a saved state
     *
     * @param tbl The routing table to use
     */
    public void setRoutingTable(RoutingTable tbl)
    {
        this.routingTable = tbl;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Node)
        {
            Node n = (Node) o;
            if (o == this)
            {
                return true;
            }
            return this.getNodeId().equals(((Node) o).getNodeId());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.getNodeId().hashCode();
    }

    @Override
    public String toString()
    {
        return this.getNodeId().toString();
    }
}
