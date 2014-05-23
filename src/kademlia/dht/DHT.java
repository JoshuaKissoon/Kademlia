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
import kademlia.KadConfiguration;
import kademlia.exceptions.ContentExistException;
import kademlia.exceptions.ContentNotFoundException;
import kademlia.node.KademliaId;
import kademlia.util.serializer.JsonSerializer;
import kademlia.util.serializer.KadSerializer;

/**
 * The main Distributed Hash Table class that manages the entire DHT
 *
 * @author Joshua Kissoon
 * @since 20140226
 */
public class DHT implements KademliaDHT
{

    private transient StoredContentManager contentManager;
    private transient KadSerializer<KademliaStorageEntry> serializer = null;
    private transient KadConfiguration config;

    private final String ownerId;

    public DHT(String ownerId, KadConfiguration config)
    {
        this.ownerId = ownerId;
        this.config = config;
        this.initialize();
    }

    @Override
    public final void initialize()
    {
        contentManager = new StoredContentManager();
    }

    @Override
    public void setConfiguration(KadConfiguration con)
    {
        this.config = con;
    }

    @Override
    public KadSerializer<KademliaStorageEntry> getSerializer()
    {
        if (null == serializer)
        {
            serializer = new JsonSerializer<>();
        }

        return serializer;
    }

    @Override
    public boolean store(KademliaStorageEntry content) throws IOException
    {
        /* Lets check if we have this content and it's the updated version */
        if (this.contentManager.contains(content.getContentMetadata()))
        {
            StorageEntryMetadata current = this.contentManager.get(content.getContentMetadata());

            /* update the last republished time */
            current.updateLastRepublished();

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
                    //System.out.println("Removing older content to update it");
                    this.remove(content.getContentMetadata());
                }
                catch (ContentNotFoundException ex)
                {
                    /* This won't ever happen at this point since we only get here if the content is found, lets ignore it  */
                }
            }
        }

        /**
         * If we got here means we don't have this content, or we need to update the content
         * If we need to update the content, the code above would've already deleted it, so we just need to re-add it
         */
        try
        {
            //System.out.println("Adding new content.");
            /* Keep track of this content in the entries manager */
            StorageEntryMetadata sEntry = this.contentManager.put(content.getContentMetadata());

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
            /**
             * Content already exist on the DHT
             * This won't happen because above takes care of removing the content if it's older and needs to be updated,
             * or returning if we already have the current content version.
             */
            return false;
        }
    }

    @Override
    public boolean store(KadContent content) throws IOException
    {
        return this.store(new StorageEntry(content));
    }

    @Override
    public KademliaStorageEntry retrieve(KademliaId key, int hashCode) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        String folder = this.getContentStorageFolderName(key);
        DataInputStream din = new DataInputStream(new FileInputStream(folder + File.separator + hashCode + ".kct"));
        return this.getSerializer().read(din);
    }

    @Override
    public boolean contains(GetParameter param)
    {
        return this.contentManager.contains(param);
    }

    @Override
    public KademliaStorageEntry get(StorageEntryMetadata entry) throws IOException, NoSuchElementException
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

    @Override
    public KademliaStorageEntry get(GetParameter param) throws NoSuchElementException, IOException
    {
        /* Load a KadContent if any exist for the given criteria */
        try
        {
            StorageEntryMetadata e = this.contentManager.get(param);
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

    @Override
    public void remove(KadContent content) throws ContentNotFoundException
    {
        this.remove(new StorageEntryMetadata(content));
    }

    @Override
    public void remove(StorageEntryMetadata entry) throws ContentNotFoundException
    {
        String folder = this.getContentStorageFolderName(entry.getKey());
        File file = new File(folder + File.separator + entry.hashCode() + ".kct");

        contentManager.remove(entry);

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
    private String getContentStorageFolderName(KademliaId key)
    {
        /**
         * Each content is stored in a folder named after the first 2 characters of the NodeId
         *
         * The name of the file containing the content is the hash of this content
         */
        String folderName = key.hexRepresentation().substring(0, 2);
        File contentStorageFolder = new File(this.config.getNodeDataFolder(ownerId) + File.separator + folderName);

        /* Create the content folder if it doesn't exist */
        if (!contentStorageFolder.isDirectory())
        {
            contentStorageFolder.mkdir();
        }

        return contentStorageFolder.toString();
    }

    @Override
    public List<StorageEntryMetadata> getStorageEntries()
    {
        return contentManager.getAllEntries();
    }

    @Override
    public void putStorageEntries(List<StorageEntryMetadata> ientries)
    {
        for (StorageEntryMetadata e : ientries)
        {
            try
            {
                this.contentManager.put(e);
            }
            catch (ContentExistException ex)
            {
                /* Entry already exist, no need to store it again */
            }
        }
    }

    @Override
    public synchronized String toString()
    {
        return this.contentManager.toString();
    }
}
