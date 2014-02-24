package kademlia.node;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Comparator;
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

    private final RoutingTable routingTable;

    
    {
        this.routingTable = new RoutingTable(this);
    }

    public Node(NodeId nid, InetAddress ip, int port)
    {
        this.nodeId = nid;
        this.inetAddress = ip;
        this.port = port;
    }

    /**
     * Load the Node's data from a DataInput stream
     *
     * @param in
     *
     * @throws IOException
     */
    public Node(DataInput in) throws IOException
    {
        this.fromStream(in);
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
    public void toStream(DataOutput out) throws IOException
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
    public final void fromStream(DataInput in) throws IOException
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

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Node)
        {
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

    /**
     * A DistanceComparator is used to compare Node objects based on their closeness
     * */
    public static class DistanceComparator implements Comparator
    {

        private final NodeId nodeId;

        /**
         * The NodeId relative to which the distance should be measured.
         *
         * @param nodeId
         * */
        public DistanceComparator(NodeId nodeId)
        {
            this.nodeId = nodeId;
        }

        /**
         * Compare two objects which must both be of type <code>Node</code>
         * and determine which is closest to the identifier specified in the
         * constructor.
         * */
        @Override
        public int compare(Object o1, Object o2)
        {
            Node n1 = (Node) o1;
            Node n2 = (Node) o2;

            /* Check if they are equal and return 0 */
            if (n1.getNodeId().equals(n2.getNodeId()))
            {
                return 0;
            }

            //System.out.println("\n **************** Compare Starting **************** ");
            //System.out.println("Comparing to: " + this.nodeId);
            int index1 = nodeId.xor(n1.getNodeId()).getFirstSetBitIndex();
            //System.out.println("Node " + n1.getNodeId() + " distance: " + index1);
            int index2 = nodeId.xor(n2.getNodeId()).getFirstSetBitIndex();
            //System.out.println("Node " + n2.getNodeId() + " distance: " + index2);

            int retval;
            if (index1 < index2)
            {
                /* If the first node is closer to the given node, return 1 */
                retval = 1;
            }
            else
            {
                /**
                 * If the first node is farther to the given node, return 1
                 *
                 * @note -1 will also be returned if both nodes are the same distance away
                 * This really don't make a difference though, since they need to be sorted.
                 */
                retval = -1;
            }

            //System.out.println("Returned: " + retval);
            //System.out.println("**************** Compare Ended ***************** \n");
            return retval;
        }
    }
}
