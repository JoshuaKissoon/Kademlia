package kademlia.serializer;

import java.io.DataInput;
import java.io.DataOutput;
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
     */
    public void write(KadContent content, DataOutput out);

    /**
     * Read a KadContent from a DataInput Stream
     *
     * @param in The InputStream to read the data from
     *
     * @return KadContent
     */
    public KadContent read(DataInput in);
}
