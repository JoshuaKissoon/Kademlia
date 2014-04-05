package kademlia.dht;

import kademlia.node.NodeId;

/**
 * A GET request can get content based on Key, Owner, Type, etc
 *
 * This is a class containing the parameters to be passed in a GET request
 *
 * We use a class since the number of filtering parameters can change later
 *
 * @author Joshua Kissoon
 * @since 20140224
 */
public class GetParameter
{

    private NodeId key;
    private String ownerId = null;
    private String type = null;

    /**
     * Construct a GetParameter to search for data by NodeId and owner
     *
     * @param key
     * @param type
     */
    public GetParameter(NodeId key, String type)
    {
        this.key = key;
        this.type = type;
    }

    /**
     * Construct a GetParameter to search for data by NodeId, owner, type
     *
     * @param key
     * @param type
     * @param owner
     */
    public GetParameter(NodeId key, String type, String owner)
    {
        this(key, owner);
        this.type = type;
    }

    /**
     * Construct our get parameter from a Content
     *
     * @param c
     */
    public GetParameter(KadContent c)
    {
        this.key = c.getKey();

        if (c.getType() != null)
        {
            this.type = c.getType();
        }

        if (c.getOwnerId() != null)
        {
            this.ownerId = c.getOwnerId();
        }
    }

    /**
     * Construct our get parameter from a StorageEntryMeta data
     *
     * @param md
     */
    public GetParameter(StorageEntryMetadata md)
    {
        this.key = md.getKey();

        if (md.getType() != null)
        {
            this.type = md.getType();
        }

        if (md.getOwnerId() != null)
        {
            this.ownerId = md.getOwnerId();
        }
    }

    public NodeId getKey()
    {
        return this.key;
    }

    public void setOwnerId(String ownerId)
    {
        this.ownerId = ownerId;
    }

    public String getOwnerId()
    {
        return this.ownerId;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return this.type;
    }

    @Override
    public String toString()
    {
        return "GetParameter - [Key: " + key + "][Owner: " + this.ownerId + "][Type: " + this.type + "]";
    }
}
