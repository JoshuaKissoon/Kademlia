package kademlia.core;

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
     * Construct a GetParameter to search for data by NodeId
     *
     * @param key
     */
    public GetParameter(NodeId key)
    {
        this.key = key;
    }

    /**
     * Construct a GetParameter to search for data by NodeId and owner
     *
     * @param key
     * @param owner
     */
    public GetParameter(NodeId key, String owner)
    {
        this(key);
        this.ownerId = owner;
    }

    /**
     * Construct a GetParameter to search for data by NodeId, owner, type
     *
     * @param key
     * @param owner
     * @param type
     */
    public GetParameter(NodeId key, String owner, String type)
    {
        this(key, owner);
        this.type = type;
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
