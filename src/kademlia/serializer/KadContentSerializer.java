package kademlia.serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import kademlia.dht.KadContent;

/**
 * A Serializer is used to transform data to and from a specified form.
 *
 * Here we define the structure of any Serializer used in Kademlia
 *
 * @author Joshua Kissoon
 * @since 20140225
 */
public interface KadContentSerializer
{

    /**
     * Write a KadContent to a DataOutput stream
     *
     * @param content The content to write
     * @param out     The output Stream to write to
     *
     * @throws java.io.IOException
     */
    public void write(KadContent content, DataOutputStream out) throws IOException;

    /**
     * Read a KadContent from a DataInput Stream
     *
     * @param in The InputStream to read the data from
     *
     * @return KadContent
     *
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public KadContent read(DataInputStream in) throws IOException, ClassNotFoundException;
}
