package kademlia;

/**
 * Class that keeps statistics for this Kademlia instance.
 *
 * These statistics are temporary and will be lost when Kad is shut down.
 *
 * @author Joshua Kissoon
 * @since 20140505
 */
public class Statistics
{

    /* How much data was sent and received by the server over the network */
    private long totalDataSent, totalDataReceived;

    
    {
        this.totalDataSent = 0;
        this.totalDataReceived = 0;
    }

    /**
     * Used to indicate some data is sent
     *
     * @param size The size of the data sent
     */
    public void sentData(long size)
    {
        this.totalDataSent += size;
    }

    /**
     * @return The total data sent
     */
    public long getTotalDataSent()
    {
        return this.totalDataSent;
    }

    /**
     * Used to indicate some data was received
     *
     * @param size The size of the data received
     */
    public void receivedData(long size)
    {
        this.totalDataReceived += size;
    }

    /**
     * @return The total data received
     */
    public long getTotalDataReceived()
    {
        return this.totalDataReceived;
    }
}
