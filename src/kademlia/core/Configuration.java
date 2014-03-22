package kademlia.core;

/**
 * A set of Kademlia configuration parameters. Default values are
 * supplied and can be changed by the application as necessary.
 *
 */
public class Configuration
{

    /**
     * Interval in milliseconds between execution of RestoreOperations.
     * */
    public static long RESTORE_INTERVAL = 60 * 1000;   // Default at 1 hour

    /**
     * If no reply received from a node in this period (in milliseconds)
     * consider the node unresponsive.
     * */
    public static long RESPONSE_TIMEOUT = 1500;

    /**
     * Maximum number of milliseconds for performing an operation.
     * */
    public static long OPERATION_TIMEOUT = 3000;

    /**
     * Maximum number of concurrent messages in transit.
     * */
    public static int CONCURRENCY = 10;

    /**
     * Log base exponent.
     * */
    public static int B = 2;

    /**
     * Bucket size.
     * */
    public static int K = 2;

    /**
     * Size of replacement cache.
     * */
    public static int RCSIZE = 3;

    /**
     * Number of times a node can be marked as stale before it is actually removed.
     * */
    public static int STALE = 1;

    /**
     * Local Storage location - Relative to the user's home folder (Cross-Platform)
     */
    public static String LOCAL_FOLDER = "kademlia";

    /**
     * Should we save the node state when the node is shut down and reload it when the node is re-loaded
     */
    public static boolean SAVE_STATE_ON_SHUTDOWN = true;
}
