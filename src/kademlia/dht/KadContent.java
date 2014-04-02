package kademlia.dht;

import kademlia.node.NodeId;

/**
 * Any piece of content that needs to be stored on the DHT
 *
 * @author Joshua Kissoon
 *
 * @since 20140224
 */
public interface KadContent
{

    /**
     * @return NodeId The DHT key for this content
     */
    public NodeId getKey();

    /**
     * @return String The type of content
     */
    public String getType();

    /**
     * Each content will have an created date
     * This allows systems to know when to delete a content form his/her machine
     *
     * @return long The create date of this content
     */
    public long getCreatedTimestamp();

    /**
     * Each content will have an update timestamp
     * This allows the DHT to keep only the latest version of a content
     *
     * @return long The timestamp of when this content was last updated
     */
    public long getLastUpdatedTimestamp();

    /**
     * @return The ID of the owner of this content
     */
    public String getOwnerId();

    /**
     * Each content needs to be in byte format for transporting and storage,
     * this method takes care of that.
     *
     * Each object is responsible for transforming itself to byte format since the
     * structure of methods may differ.
     *
     * @return byte[] The content in byte format
     */
    public byte[] toBytes();

    /**
     * Given the Content in byte format, read it
     *
     * @param data The object in byte format
     *
     * @return A new object from the given byte[]
     */
    public KadContent fromBytes(byte[] data);
}
