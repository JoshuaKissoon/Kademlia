/**
 * @author Joshua Kissoon
 * @created 20140215
 * @desc Represents a Kademlia Node ID
 */
package kademlia.node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
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
     */
    public NodeId(String data)
    {
        keyBytes = data.getBytes();
        if (keyBytes.length != ID_LENGTH / 8)
        {
            throw new IllegalArgumentException("Specified Data need to be " + (ID_LENGTH / 8) + " characters long.");
        }
    }

    /**
     * Generate a random key
     */
    public NodeId()
    {
        keyBytes = new byte[ID_LENGTH / 8];
        new Random().nextBytes(keyBytes);
    }

    public NodeId(byte[] bytes)
    {
        if (bytes.length != ID_LENGTH / 8)
        {
            throw new IllegalArgumentException("Specified Data need to be " + (ID_LENGTH / 8) + " characters long.");
        }
        this.keyBytes = bytes;
    }

    /**
     * Load the NodeId from a DataInput stream
     *
     * @param in The stream from which to load the NodeId
     *
     * @throws IOException
     */
    public NodeId(DataInputStream in) throws IOException
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
     * @param o The NodeId to compare to this NodeId
     *
     * @return boolean Whether the 2 NodeIds are equal
     */
    @Override
    public boolean equals(Object o)
    {

        if (o instanceof NodeId)
        {
            NodeId nid = (NodeId) o;
            return Arrays.equals(this.getBytes(), nid.getBytes());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 83 * hash + Arrays.hashCode(this.keyBytes);
        return hash;
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
        byte[] result = new byte[ID_LENGTH / 8];
        byte[] nidBytes = nid.getBytes();

        for (int i = 0; i < ID_LENGTH / 8; i++)
        {
            result[i] = (byte) (this.keyBytes[i] ^ nidBytes[i]);
            //System.out.println("XOR Result: " + result[i]);
        }

        NodeId resNid = new NodeId(result);

        //System.out.println("Resulting Nid: " + resNid + " First set bit: " + resNid.getFirstSetBitIndex());
        return resNid;
    }

    /**
     * Checks the number of leading 0's in this NodeId
     *
     * @return int The number of leading 0's
     */
    public int getFirstSetBitIndex()
    {
        int prefixLength = 0;

        for (byte b : this.keyBytes)
        {
            if (b == 0)
            {
                prefixLength += 8;
            }
            else
            {
                /* If the byte is not 0, we need to count how many MSBs are 0 */
                int count = 0;
                for (int i = 7; i >= 0; i--)
                {
                    boolean a = (b & (1 << i)) == 0;
                    if (a)
                    {
                        count++;
                    }
                    else
                    {
                        break;   // Reset the count if we encounter a non-zero number
                    }
                }

                /* Add the count of MSB 0s to the prefix length */
                prefixLength += count;

                /* Break here since we've now covered the MSB 0s */
                break;
            }
        }
        return ID_LENGTH - prefixLength;
    }

    @Override
    public void toStream(DataOutputStream out) throws IOException
    {
        /* Add the NodeId to the stream */
        out.write(this.getBytes());
    }

    @Override
    public void fromStream(DataInputStream in) throws IOException
    {
        byte[] input = new byte[ID_LENGTH / 8];
        in.readFully(input);
        this.keyBytes = input;
    }

    public String hexRepresentation()
    {
        /* Returns the hex format of this NodeId */
        BigInteger bi = new BigInteger(1, this.keyBytes);
        return String.format("%0" + (this.keyBytes.length << 1) + "X", bi);
    }

    @Override
    public String toString()
    {
        return this.hexRepresentation();
    }

}
