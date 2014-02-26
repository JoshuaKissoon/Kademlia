package kademlia.dht;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kademlia.node.NodeId;

/**
 * It would be infeasible to keep all content in memory to be send when requested
 * Instead we store content into files
 * We use this Class to keep track of all content stored
 *
 * @author Joshua Kissoon
 * @since 20140226
 */
public class StorageEntryManager
{

    private final Map<NodeId, List<StorageEntry>> entries;

    
    {
        entries = new HashMap<>();
    }

    /**
     * Add a new entry to our storage
     *
     * @param entry
     */
    public void put(StorageEntry entry)
    {
        if (!this.entries.containsKey(entry.getKey()))
        {
            this.entries.put(entry.getKey(), new ArrayList<StorageEntry>());
        }
        
        this.entries.get(entry.getKey()).add(entry);
    }

    /**
     * Checks if our DHT has a Content for the given criteria
     *
     * @todo Add searching for content by type and ownerID
     *
     * @param key
     *
     * @return boolean
     */
    public boolean contains(NodeId key)
    {
        return this.entries.containsKey(key);
    }

    /**
     * Checks if our DHT has a Content for the given criteria
     *
     * @todo Add finding for content by type and ownerID
     *
     * @param key
     *
     * @return List of content for the specific search parameters
     */
    public List<StorageEntry> get(NodeId key)
    {
        return this.entries.get(key);
    }

}
