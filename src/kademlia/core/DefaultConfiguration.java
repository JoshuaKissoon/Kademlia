package kademlia.core;

import java.io.File;

/**
 * A set of Kademlia configuration parameters. Default values are
 * supplied and can be changed by the application as necessary.
 *
 */
public class DefaultConfiguration implements KadConfiguration
{

    private final static long RESTORE_INTERVAL = 20 * 1000; // in milliseconds
    private final static long RESPONSE_TIMEOUT = 1500;
    private final static long OPERATION_TIMEOUT = 3000;
    private final static int CONCURRENCY = 10;
    private final static int K = 5;
    private final static int RCSIZE = 3;
    private final static int STALE = 1;
    private final static String LOCAL_FOLDER = "kademlia";
    
    private final static boolean IS_TESTING = true;

    /**
     * Default constructor to support Gson Serialization
     */
    public DefaultConfiguration()
    {

    }

    @Override
    public long restoreInterval()
    {
        return RESTORE_INTERVAL;
    }

    @Override
    public long responseTimeout()
    {
        return RESPONSE_TIMEOUT;
    }

    @Override
    public long operationTimeout()
    {
        return OPERATION_TIMEOUT;
    }

    @Override
    public int maxConcurrentMessagesTransiting()
    {
        return CONCURRENCY;
    }

    @Override
    public int k()
    {
        return K;
    }

    @Override
    public int replacementCacheSize()
    {
        return RCSIZE;
    }

    @Override
    public int stale()
    {
        return STALE;
    }

    @Override
    public String getNodeDataFolder(String ownerId)
    {
        /* Setup the main storage folder if it doesn't exist */
        String path = System.getProperty("user.home") + File.separator + DefaultConfiguration.LOCAL_FOLDER;
        File folder = new File(path);
        if (!folder.isDirectory())
        {
            folder.mkdir();
        }

        /* Setup subfolder for this owner if it doesn't exist */
        File ownerFolder = new File(folder + File.separator + ownerId);
        if (!ownerFolder.isDirectory())
        {
            ownerFolder.mkdir();
        }

        /* Return the path */
        return ownerFolder.toString();
    }

    @Override
    public boolean isTesting()
    {
        return IS_TESTING;
    }
}
