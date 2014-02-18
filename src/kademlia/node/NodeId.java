/**
 * @author Joshua Kissoon
 * @created 20140215
 * @desc Represents a Kademlia Node ID
 */
package kademlia.node;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import kademlia.message.Streamable;

public class NodeId implements Streamable
{

    public final static int ID_LENGTH = 160;
    private byte[] keyBytes;

    /**
     * Construct the NodeId from some string
     *
     * @param data The user generated key string
     *
     * @todo Throw an exception if the key is too short or too long
     */
    public NodeId(String data)
    {
        keyBytes = data.getBytes();
    }

    /**
     * Generate a random key
     */
    public NodeId()
    {
        keyBytes = new byte[ID_LENGTH];
        new Random().nextBytes(keyBytes);
    }

    public NodeId(byte[] bytes)
    {
        this.keyBytes = bytes;
    }

    /**
     * Load the NodeId from a DataInput stream
     *
     * @param in The stream from which to load the NodeId
     *
     * @throws IOException
     */
    public NodeId(DataInput in) throws IOException
    {
        this.fromStream(in);
    }

    public byte[] getBytes()
    {
        return this.keyBytes;
    }

    /**
     * Compares a NodeId to this NodeId
     *
     * @param nid The NodeId to compare to this NodeId
     *
     * @return boolean Whether the 2 NodeIds are equal
     */
    public boolean equals(NodeId nid)
    {
        return Arrays.equals(keyBytes, nid.getBytes());
    }

    /**
     * Checks if a given NodeId is less than this NodeId
     *
     * @param nid The NodeId to compare to this NodeId
     *
     * @return boolean Whether the given NodeId is less than this NodeId
     */
    public boolean lessThan(NodeId nid)
    {
        byte[] nidBytes = nid.getBytes();
        for (int i = 0; i < ID_LENGTH; i++)
        {
            if (this.keyBytes[i] != nidBytes[i])
            {
                return this.keyBytes[i] < nidBytes[i];
            }
        }

        /* We got here means they're equal */
        return false;
    }

    /**
     * Checks the distance between this and another NodeId
     *
     * @param nid
     *
     * @return The distance of this NodeId from the given NodeId
     */
    public NodeId xor(NodeId nid)
    {
        byte[] result = new byte[ID_LENGTH];
        byte[] nidBytes = nid.getBytes();
        for (int i = 0; i < ID_LENGTH / 8; i++)
        {
            result[i] = (byte) (this.keyBytes[i] ^ nidBytes[i]);
        }

        return new NodeId(result);
    }

    /**
     * Checks the number of leading 0's in this NodeId
     *
     * @return int The number of leading 0's
     */
    public int prefixLength()
    {
        int prefixLength = 0;

        for (byte b : this.keyBytes)
        {
            if (b == 0)
            {
                prefixLength++;
            }
            else
            {
                break;
            }
        }

        return prefixLength;
    }

    @Override
    public void toStream(DataOutput out) throws IOException
    {
        /* Add the NodeId to the stream */
        out.write(this.getBytes());
    }

    @Override
    public void fromStream(DataInput in) throws IOException
    {
        byte[] input = new byte[ID_LENGTH / 8];
        in.readFully(input);
        this.keyBytes = input;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("NodeId: ");
        sb.append(new String(this.keyBytes));

        return sb.toString();
    }

}
