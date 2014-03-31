package kademlia.dht;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import kademlia.core.GetParameter;
import kademlia.core.KadConfiguration;
import kademlia.exceptions.ContentExistException;
import kademlia.exceptions.ContentNotFoundException;
import kademlia.node.NodeId;
import kademlia.serializer.JsonSerializer;

/**
 * The main Distributed Hash Table class that manages the entire DHT
 *
 * @author Joshua Kissoon
 * @since 20140226
 */
public class DHT
{

    private transient StorageEntryManager entriesManager;
    private final transient JsonSerializer<KadContent> contentSerializer;
    private final transient KadConfiguration config;

    private final String ownerId;

    
    {
        contentSerializer = new JsonSerializer<>();
    }

    public DHT(String ownerId, KadConfiguration config)
    {
        this.ownerId = ownerId;
        this.config = config;
        this.initialize();
    }

    /**
     * Initialize this DHT to it's default state
     */
    public final void initialize()
    {
        entriesManager = new StorageEntryManager();
    }

    /**
     * Handle storing content locally
     *
     * @param content The DHT content to store
     *
     * @throws java.io.IOException
     */
    public void store(KadContent content) throws IOException
    {
        /* Keep track of this content in the entries manager */
        try
        {
            StorageEntry sEntry = this.entriesManager.put(content);

            /* Now we store the content locally in a file */
            String contentStorageFolder = this.getContentStorageFolderName(content.getKey());
            DataOutputStream dout = new DataOutputStream(new FileOutputStream(contentStorageFolder + File.separator + sEntry.hashCode() + ".kct"));
            contentSerializer.write(content, dout);
        }
        catch (ContentExistException e)
        {
            /* Content already exist on the DHT, no need to do anything here */
        }
    }

    /**
     * Retrieves a Content from local storage
     *
     * @param key      The Key of the content to retrieve
     * @param hashCode The hash code of the content to retrieve
     *
     * @return A KadContent object
     */
    private KadContent retrieve(NodeId key, int hashCode) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        String folder = this.getContentStorageFolderName(key);
        DataInputStream in = new DataInputStream(new FileInputStream(folder + File.separator + hashCode + ".kct"));
        return contentSerializer.read(in);
    }

    /**
     * Check if any content for the given criteria exists in this DHT
     *
     * @param param The content search criteria
     *
     * @return boolean Whether any content exist that satisfy the criteria
     */
    public boolean contains(GetParameter param)
    {
        return this.entriesManager.contains(param);
    }

    /**
     * Retrieve and create a KadContent object given the StorageEntry object
     *
     * @param entry The StorageEntry used to retrieve this content
     *
     * @return KadContent The content object
     *
     * @throws java.io.IOException
     */
    public KadContent get(StorageEntry entry) throws IOException, NoSuchElementException
    {
        try
        {
            return this.retrieve(entry.getKey(), entry.hashCode());
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Error while loading file for content. Message: " + e.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            System.err.println("The class for some content was not found. Message: " + e.getMessage());
        }

        /* If we got here, means we got no entries */
        throw new NoSuchElementException();
    }

    /**
     * Get the StorageEntry for the content if any exist,
     * retrieve the KadContent from the storage system and return it
     *
     * @param param The parameters used to filter the content needed
     *
     * @return KadContent A KadContent found on the DHT satisfying the given criteria
     *
     * @throws java.io.IOException
     */
    public KadContent get(GetParameter param) throws NoSuchElementException, IOException
    {
        /* Load a KadContent if any exist for the given criteria */
        try
        {
            StorageEntry e = this.entriesManager.get(param);
            return this.retrieve(e.getKey(), e.hashCode());
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Error while loading file for content. Message: " + e.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            System.err.println("The class for some content was not found. Message: " + e.getMessage());
        }

        /* If we got here, means we got no entries */
        throw new NoSuchElementException();
    }

    /**
     * Delete a content from local storage
     *
     * @param content The Content to Remove
     *
     *
     * @throws kademlia.exceptions.ContentNotFoundException
     */
    public void remove(KadContent content) throws ContentNotFoundException
    {
        this.remove(new StorageEntry(content));
    }

    public void remove(StorageEntry entry) throws ContentNotFoundException
    {
        String folder = this.getContentStorageFolderName(entry.getKey());
        File file = new File(folder + File.separator + entry.hashCode() + ".kct");

        entriesManager.remove(entry);

        if (file.exists())
        {
            file.delete();
        }
        else
        {
            throw new ContentNotFoundException();
        }
    }

    /**
     * Get the name of the folder for which a content should be stored
     *
     * @param key The key of the content
     *
     * @return String The name of the folder
     */
    private String getContentStorageFolderName(NodeId key)
    {
        /**
         * Each content is stored in a folder named after the first 10 characters of the NodeId
         *
         * The name of the file containing the content is the hash of this content
         */
        String folderName = key.hexRepresentation().substring(0, 10);
        File contentStorageFolder = new File(this.config.getNodeDataFolder(ownerId) + File.separator + folderName);

        /* Create the content folder if it doesn't exist */
        if (!contentStorageFolder.isDirectory())
        {
            contentStorageFolder.mkdir();
        }

        return contentStorageFolder.toString();
    }

    /**
     * @return A List of all StorageEntries for this node
     */
    public List<StorageEntry> getStorageEntries()
    {
        return entriesManager.getAllEntries();
    }

    /**
     * Used to add a list of storage entries for existing content to the DHT.
     * Mainly used when retrieving StorageEntries from a saved state file.
     *
     * @param ientries The entries to add
     */
    public void putStorageEntries(List<StorageEntry> ientries)
    {
        for (StorageEntry e : ientries)
        {
            try
            {
                this.entriesManager.put(e);
            }
            catch (ContentExistException ex)
            {
                /* Entry already exist, no need to store it again */
            }
        }
    }

    @Override
    public String toString()
    {
        return this.entriesManager.toString();
    }
}
