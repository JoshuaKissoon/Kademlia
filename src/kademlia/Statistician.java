package kademlia;

/**
 * Class that keeps statistics for this Kademlia instance.
 *
 * These statistics are temporary and will be lost when Kad is shut down.
 *
 * @author Joshua Kissoon
 * @since 20140505
 */
public class Statistician
{

    /* How much data was sent and received by the server over the network */
    private long totalDataSent, totalDataReceived;

    /* Bootstrap timings */
    private long bootstrapTime;

    /* Content lookup operation timing */
    private int numContentLookups;
    private long totalContentLookupTime;

    
    {
        this.totalDataSent = 0;
        this.totalDataReceived = 0;
        this.bootstrapTime = 0;
        this.numContentLookups = 0;
        this.totalContentLookupTime = 0;
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

    /**
     * Sets the bootstrap time for this Kademlia Node
     *
     * @param time The bootstrap time in nanoseconds
     */
    public void setBootstrapTime(long time)
    {
        this.bootstrapTime = time;
    }

    public long getBootstrapTime()
    {
        return this.bootstrapTime;
    }

    /**
     * Add the timing for a new content lookup operation that took place
     *
     * @param time The time the content lookup took in nanoseconds
     */
    public void addContentLookupTime(long time)
    {
        this.numContentLookups++;
        this.totalContentLookupTime += time;
    }

    public int numContentLookups()
    {
        return this.numContentLookups;
    }

    public long totalContentLookupTime()
    {
        return this.totalContentLookupTime;
    }

    /**
     * Compute the average time a content lookup took
     *
     * @return The average time
     */
    public long averageContentLookupTime()
    {
        return this.totalContentLookupTime / this.numContentLookups;
    }
}
