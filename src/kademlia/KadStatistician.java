package kademlia;

/**
 * Specification for class that keeps statistics for a Kademlia instance.
 *
 * These statistics are temporary and will be lost when Kad is shut down.
 *
 * @author Joshua Kissoon
 * @since 20140507
 */
public interface KadStatistician
{

    /**
     * Used to indicate some data is sent
     *
     * @param size The size of the data sent
     */
    public void sentData(long size);

    /**
     * @return The total data sent in KiloBytes
     */
    public long getTotalDataSent();

    /**
     * Used to indicate some data was received
     *
     * @param size The size of the data received
     */
    public void receivedData(long size);

    /**
     * @return The total data received in KiloBytes
     */
    public long getTotalDataReceived();

    /**
     * Sets the bootstrap time for this Kademlia Node
     *
     * @param time The bootstrap time in nanoseconds
     */
    public void setBootstrapTime(long time);

    /**
     * @return How long the system took to bootstrap in milliseconds
     */
    public long getBootstrapTime();

    /**
     * Add the timing for a new content lookup operation that took place
     *
     * @param time        The time the content lookup took in nanoseconds
     * @param routeLength The length of the route it took to get the content
     */
    public void addContentLookup(long time, int routeLength);

    /**
     * @return The total number of content lookups performed.
     */
    public int numContentLookups();

    /**
     * @return The total time spent on content lookups.
     */
    public long totalContentLookupTime();

    /**
     * Compute the average time a content lookup took
     *
     * @return The average time in milliseconds
     */
    public double averageContentLookupTime();

    /**
     * Compute the average route length of content lookup operations.
     *
     * @return The average route length
     */
    public double averageContentLookupRouteLength();
}
