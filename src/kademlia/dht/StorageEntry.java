package kademlia.dht;

import java.util.Objects;
import kademlia.node.NodeId;

/**
 * Keeps track of data for a Content stored in the DHT
 * Used by the StorageEntryManager class
 *
 * @author Joshua Kissoon
 * @since 20140226
 */
public class StorageEntry
{

    private final NodeId key;
    private final String ownerId;
    private final String type;

    public StorageEntry(KadContent content)
    {
        this.key = content.getKey();
        this.ownerId = content.getOwnerId();
        this.type = content.getType();
    }

    public NodeId getKey()
    {
        return this.key;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof StorageEntry)
        {
            return this.hashCode() == o.hashCode();
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.key);
        hash = 23 * hash + Objects.hashCode(this.ownerId);
        hash = 23 * hash + Objects.hashCode(this.type);
        return hash;
    }
}
