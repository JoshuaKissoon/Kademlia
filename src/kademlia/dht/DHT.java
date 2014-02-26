package kademlia.dht;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import kademlia.core.Configuration;
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
        this.entriesManager.put(new StorageEntry(content));

        /**
         * Now we store the content locally in a file
         * Each content is stored in a folder named after the first 10 characters of the NodeId
         *
         * The name of the file containing the content is the hash of this content
         */
        String storagePath = System.getProperty("user.home") + File.separator + Configuration.localFolder;
        File mainStorageFolder = new File(storagePath);

        /* Create the folder if it doesn't exist */
        if (!mainStorageFolder.isDirectory())
        {
            mainStorageFolder.mkdir();
        }

        /* Check if a folder after the first 10 characters of Hex(nodeId) exist, if not, create it */
        String folderName = content.getKey().hexRepresentation().substring(0, 20);
        File contentStorageFolder = new File(mainStorageFolder + File.separator + folderName);
        if (!contentStorageFolder.isDirectory())
        {
            contentStorageFolder.mkdir();
        }

        /* Write the content to a file and store it in the folder */
        File contentFile = new File(String.valueOf(content.hashCode()) + ".kct");
        DataOutputStream dout = new DataOutputStream(new FileOutputStream(contentStorageFolder + File.separator + contentFile));
        new JsonSerializer().write(content, dout);
    }
}
