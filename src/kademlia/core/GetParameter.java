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
    private String owner = null;
    private String type = null;

    public GetParameter(NodeId key)
    {
        this.key = key;
    }

    public GetParameter(NodeId key, String owner)
    {
        this(key);
        this.owner = owner;
    }

    public GetParameter(NodeId key, String owner, String type)
    {
        this(key, owner);
        this.type = type;
    }

}
