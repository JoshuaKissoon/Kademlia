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
import kademlia.core.KadConfiguration;
import kademlia.exceptions.ContentExistException;
import kademlia.exceptions.ContentNotFoundException;
import kademlia.node.NodeId;
import kademlia.serializer.JsonSerializer;
import kademlia.serializer.KadSerializer;

/**
 * The main Distributed Hash Table class that manages the entire DHT
 *
 * @author Joshua Kissoon
 * @since 20140226
 */
public class DHT
{

    private transient StorageEntryManager entriesManager;
    private transient KadSerializer<StorageEntry> serializer = null;
    private transient KadConfiguration config;

    private final String ownerId;

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
     * Set a new configuration. Mainly used when we restore the DHT state from a file
     *
     * @param con The new configuration file
     */
    public void setConfiguration(KadConfiguration con)
    {
        this.config = con;
    }

    /**
     * Creates a new Serializer or returns an existing serializer
     *
     * @return The new ContentSerializer
     */
    public KadSerializer<StorageEntry> getSerializer()
    {
        if (null == serializer)
        {
            serializer = new JsonSerializer<>();
        }

        return serializer;
    }

    /**
     * Handle storing content locally
     *
     * @param content The DHT content to store
     *
     * @return boolean true if we stored the content, false if the content already exists and is up to date
     *
     * @throws java.io.IOException
     */
    public boolean store(StorageEntry content) throws IOException
    {
        /* Lets check if we have this content and it's the updated version */
        if (this.entriesManager.contains(content.getContentMetadata()))
        {
            StorageEntryMetadata current = this.entriesManager.get(content.getContentMetadata());
            if (current.getLastUpdatedTimestamp() >= content.getContentMetadata().getLastUpdatedTimestamp())
            {
                /* We have the current content, no need to update it! just leave this method now */
                return false;
            }
            else
            {
                /* We have this content, but not the latest version, lets delete it so the new version will be added below */
                try
                {
                    this.remove(content.getContentMetadata());
                }
                catch (ContentNotFoundException ex)
                {
                    /* @todo Log an error here */
                }
            }
        }

        /**
         * If we got here means we don't have this content, or we need to update the content
         * If we need to update the content, the code above would've already deleted it, so we just need to re-add it
         */
        try
        {
            System.out.println("Adding new content.");
            /* Keep track of this content in the entries manager */
            StorageEntryMetadata sEntry = this.entriesManager.put(content.getContentMetadata());

            /* Now we store the content locally in a file */
            String contentStorageFolder = this.getContentStorageFolderName(content.getContentMetadata().getKey());

            try (FileOutputStream fout = new FileOutputStream(contentStorageFolder + File.separator + sEntry.hashCode() + ".kct");
                    DataOutputStream dout = new DataOutputStream(fout))
            {
                this.getSerializer().write(content, dout);
            }
            return true;
        }
        catch (ContentExistException e)
        {
            /* @todo Content already exist on the DHT, log an error here */
            return false;
        }

    }

    public boolean store(KadContent content) throws IOException
    {
        return this.store(new StorageEntry(content));
    }

    /**
     * Retrieves a Content from local storage
     *
     * @param key      The Key of the content to retrieve
     * @param hashCode The hash code of the content to retrieve
     *
     * @return A KadContent object
     */
    private StorageEntry retrieve(NodeId key, int hashCode) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        String folder = this.getContentStorageFolderName(key);
        DataInputStream din = new DataInputStream(new FileInputStream(folder + File.separator + hashCode + ".kct"));
        return this.getSerializer().read(din);
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
    public StorageEntry get(StorageEntryMetadata entry) throws IOException, NoSuchElementException
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
    public StorageEntry get(GetParameter param) throws NoSuchElementException, IOException
    {
        /* Load a KadContent if any exist for the given criteria */
        try
        {
            StorageEntryMetadata e = this.entriesManager.get(param);
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
        this.remove(new StorageEntryMetadata(content));
    }

    public void remove(StorageEntryMetadata entry) throws ContentNotFoundException
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
    public List<StorageEntryMetadata> getStorageEntries()
    {
        return entriesManager.getAllEntries();
    }

    /**
     * Used to add a list of storage entries for existing content to the DHT.
     * Mainly used when retrieving StorageEntries from a saved state file.
     *
     * @param ientries The entries to add
     */
    public void putStorageEntries(List<StorageEntryMetadata> ientries)
    {
        for (StorageEntryMetadata e : ientries)
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
