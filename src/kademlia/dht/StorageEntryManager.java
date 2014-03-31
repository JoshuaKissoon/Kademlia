package kademlia.dht;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import kademlia.core.GetParameter;
import kademlia.exceptions.ContentExistException;
import kademlia.exceptions.ContentNotFoundException;
import kademlia.node.NodeId;

/**
 * It would be infeasible to keep all content in memory to be send when requested
 * Instead we store content into files
 * We use this Class to keep track of all content stored
 *
 * @author Joshua Kissoon
 * @since 20140226
 */
class StorageEntryManager
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
    public StorageEntry put(KadContent content) throws ContentExistException
    {
        return this.put(new StorageEntry(content));
    }

    /**
     * Add a new entry to our storage
     *
     * @param entry The StorageEntry to store
     */
    public StorageEntry put(StorageEntry entry) throws ContentExistException
    {
        if (!this.entries.containsKey(entry.getKey()))
        {
            this.entries.put(entry.getKey(), new ArrayList<StorageEntry>());
        }

        /* If this entry doesn't already exist, then we add it */
        if (!this.contains(entry))
        {
            this.entries.get(entry.getKey()).add(entry);

            return entry;
        }
        else
        {
            throw new ContentExistException("Content already exists on this DHT");
        }
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
            System.out.println("Does contain the key");
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
        else
        {
            System.out.println("Does not contain the key");
            System.out.println(this);
        }
        return false;
    }

    /**
     * Check if a content exist in the DHT
     */
    public boolean contains(KadContent content)
    {
        return this.contains(new StorageEntry(content));
    }

    /**
     * Check if a StorageEntry exist on this DHT
     */
    private boolean contains(StorageEntry entry)
    {
        if (this.entries.containsKey(entry.getKey()))
        {
            return this.entries.get(entry.getKey()).contains(entry);
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

    public void remove(KadContent content) throws ContentNotFoundException
    {
        this.remove(new StorageEntry(content));
    }

    public void remove(StorageEntry entry) throws ContentNotFoundException
    {
        if (contains(entry))
        {
            this.entries.get(entry.getKey()).remove(entry);
        }
        else
        {
            throw new ContentNotFoundException("This content does not exist in the Storage Entries");
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("Stored Content: \n");
        for (List<StorageEntry> es : this.entries.values())
        {
            if (entries.size() < 1)
            {
                continue;
            }

            for (StorageEntry e : es)
            {
                sb.append(e);
                sb.append("\n");
            }
        }

        sb.append("\n");
        return sb.toString();
    }
}
