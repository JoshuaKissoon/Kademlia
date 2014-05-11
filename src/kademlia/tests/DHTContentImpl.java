package kademlia.tests;

import com.google.gson.Gson;
import kademlia.dht.KadContent;
import kademlia.node.KademliaId;

/**
 * A simple DHT Content object to test DHT storage
 *
 * @author Joshua Kissoon
 * @since 20140224
 */
public class DHTContentImpl implements KadContent
{

    public static final transient String TYPE = "DHTContentImpl";

    private KademliaId key;
    private String data;
    private String ownerId;
    private final long createTs;
    private long updateTs;

    
    {
        this.createTs = this.updateTs = System.currentTimeMillis() / 1000L;
    }

    public DHTContentImpl()
    {

    }

    public DHTContentImpl(String ownerId, String data)
    {
        this.ownerId = ownerId;
        this.data = data;
        this.key = new KademliaId();
    }

    public DHTContentImpl(KademliaId key, String ownerId)
    {
        this.key = key;
        this.ownerId = ownerId;
    }

    public void setData(String newData)
    {
        this.data = newData;
        this.setUpdated();
    }

    @Override
    public KademliaId getKey()
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

    /**
     * Set the content as updated
     */
    public void setUpdated()
    {
        this.updateTs = System.currentTimeMillis() / 1000L;
    }

    @Override
    public long getCreatedTimestamp()
    {
        return this.createTs;
    }

    @Override
    public long getLastUpdatedTimestamp()
    {
        return this.updateTs;
    }

    @Override
    public byte[] toBytes()
    {
        Gson gson = new Gson();
        return gson.toJson(this).getBytes();
    }

    @Override
    public DHTContentImpl fromBytes(byte[] data)
    {
        Gson gson = new Gson();
        DHTContentImpl val = gson.fromJson(new String(data), DHTContentImpl.class);
        return val;
    }

    @Override
    public String toString()
    {
        return "DHTContentImpl[{data=" + this.data + "{ {key:" + this.key + "}]";
    }
}
