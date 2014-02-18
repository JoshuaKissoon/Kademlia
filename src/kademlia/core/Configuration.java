package kademlia.core;

/**
 * A set of Kademlia configuration parameters. Default values are
 * supplied and can be changed by the application as necessary.
 * */
public class Configuration
{

    /**
     * Interval in milliseconds between execution of RestoreOperations.
     * */
    public static long RESTORE_INTERVAL = 60 * 60 * 1000;

    /**
     * If no reply received from a node in this period (in milliseconds)
     * consider the node unresponsive.
     * */
    public static long RESPONSE_TIMEOUT = 3000;

    /**
     * Maximum number of milliseconds for performing an operation.
     * */
    public static long OPERATION_TIMEOUT = 10000;

    /**
     * Maximum number of concurrent messages in transit.
     * */
    public static int CONCURRENCY = 2;

    /**
     * Log base exponent.
     * */
    public static int B = 2;

    /**
     * Bucket size.
     * */
    public static int K = 3;

    /**
     * Size of replacement cache.
     * */
    public static int RCSIZE = 3;

    /**
     * Number of times a node can be marked as stale before it is actually removed.
     * */
    public static int STALE = 1;
}
