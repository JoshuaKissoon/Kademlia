package kademlia.tests;

import kademlia.dht.KadContent;
import kademlia.node.NodeId;

/**
 * A simple DHT Content object to test DHT storage
 *
 * @author Joshua Kissoon
 * @since 20140224
 */
public class DHTContentImpl implements KadContent
{

    private final NodeId key;
    private String data;
    private final String ownerId;
    private final long createTs;

    public static final String TYPE = "DHTContentImpl";

    
    {
        this.createTs = System.currentTimeMillis() / 1000L;
    }

    public DHTContentImpl(String ownerId, String data)
    {
        this.ownerId = ownerId;
        this.data = data;
        this.key = new NodeId();
    }

    public DHTContentImpl(NodeId key, String ownerId)
    {
        this.key = key;
        this.ownerId = ownerId;
    }

    public void setData(String newData)
    {
        this.data = newData;
    }

    public String getData()
    {
        return this.data;
    }

    @Override
    public NodeId getKey()
    {
        return this.key;
    }

    @Override
    public String getType()
    {
        return TYPE;
    }

    @Override
    public String getOwnerId()
    {
        return this.ownerId;
    }

    @Override
    public long getCreatedTimestamp()
    {
        return this.createTs;
    }

    public String toString()
    {
        return "DHTContentImpl[data=" + this.data + "]";
    }
}
