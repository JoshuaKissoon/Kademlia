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
import kademlia.core.Configuration;
import kademlia.core.GetParameter;
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

    private final StorageEntryManager entriesManager;
    private final JsonSerializer<KadContent> contentSerializer;

    
    {
        entriesManager = new StorageEntryManager();
        contentSerializer = new JsonSerializer<>();
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
        this.entriesManager.put(content);

        /* Now we store the content locally in a file */
        String contentStorageFolder = this.getContentStorageFolderName(content.getKey());
        DataOutputStream dout = new DataOutputStream(new FileOutputStream(contentStorageFolder + File.separator + content.hashCode() + ".kct"));
        contentSerializer.write(content, dout);
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
            return this.retrieve(e.getKey(), e.getContentHash());

        }
        catch (FileNotFoundException e)
        {
            System.err.println("Error while loading file for content.");
        }
        catch (ClassNotFoundException e)
        {
            System.err.println("The class for some content was not found.");
        }

        /* If we got here, means we got no entries */
        throw new NoSuchElementException();
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
        String storagePath = System.getProperty("user.home") + File.separator + Configuration.localFolder;
        File mainStorageFolder = new File(storagePath);

        /* Create the main storage folder if it doesn't exist */
        if (!mainStorageFolder.isDirectory())
        {
            mainStorageFolder.mkdir();
        }

        String folderName = key.hexRepresentation().substring(0, 10);
        File contentStorageFolder = new File(mainStorageFolder + File.separator + folderName);

        /* Create the content folder if it doesn't exist */
        if (!contentStorageFolder.isDirectory())
        {
            contentStorageFolder.mkdir();
        }

        return mainStorageFolder + File.separator + folderName;
    }

    /**
     * @return A List of all StorageEntries for this node
     */
    public List<StorageEntry> getStorageEntries()
    {
        return entriesManager.getAllEntries();
    }
}
