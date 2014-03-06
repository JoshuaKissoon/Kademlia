package kademlia.dht;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import kademlia.core.GetParameter;
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
     * @param content The content to store a reference to
     */
    public void put(KadContent content)
    {
        StorageEntry entry = new StorageEntry(content);
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
     * @param param The parameters used to search for a content
     *
     * @return boolean
     */
    public boolean contains(GetParameter param)
    {
        if (this.entries.containsKey(param.getKey()))
        {
            /* Content with this key exist, check if any match the rest of the search criteria */
            for (StorageEntry e : this.entries.get(param.getKey()))
            {
                /* If any entry satisfies the given parameters, return true */
                if (e.satisfiesParameters(param))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if our DHT has a Content for the given criteria
     *
     * @param param The parameters used to search for a content
     *
     * @todo Add finding for content by type and ownerID
     *
     * @return List of content for the specific search parameters
     */
    public StorageEntry get(GetParameter param) throws NoSuchElementException
    {
        if (this.entries.containsKey(param.getKey()))
        {
            /* Content with this key exist, check if any match the rest of the search criteria */
            for (StorageEntry e : this.entries.get(param.getKey()))
            {
                /* If any entry satisfies the given parameters, return true */
                if (e.satisfiesParameters(param))
                {
                    return e;
                }
            }

            /* If we got here, means we didn't find any entry */
            throw new NoSuchElementException();
        }
        else
        {
            throw new NoSuchElementException("No content exist for the given parameters");
        }
    }

    /**
     * @return A list of all storage entries
     */
    public List<StorageEntry> getAllEntries()
    {
        List<StorageEntry> entriesRet = new ArrayList<>();

        for (List<StorageEntry> entrySet : this.entries.values())
        {
            if (entrySet.size() > 0)
            {
                entriesRet.addAll(entrySet);
            }
        }

        return entriesRet;
    }
}
