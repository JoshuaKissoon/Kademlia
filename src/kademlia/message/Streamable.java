package kademlia.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A Streamable object is able to write it's state to an output stream and
 * a class implementing Streamable must be able to recreate an instance of
 * the class from an input stream. No information about class name is written
 * to the output stream so it must be known what class type is expected when
 * reading objects back in from an input stream. This gives a space
 * advantage over Serializable.
 * <p>
 * Since the exact class must be known anyway prior to reading, it is incouraged
 * that classes implementing Streamble also provide a constructor of the form:
 * <p>
 * <code>Streamable(DataInput in) throws IOException;</code>
 * */
public interface Streamable
{

    /**
     * Writes the internal state of the Streamable object to the output stream
     * in a format that can later be read by the same Streamble class using
     * the {@link #fromStream} method.
     *
     * @param out
     *
     * @throws java.io.IOException
     */
    public void toStream(DataOutputStream out) throws IOException;

    /**
     * Reads the internal state of the Streamable object from the input stream.
     *
     * @param out
     *
     * @throws java.io.IOException
     */
    public void fromStream(DataInputStream out) throws IOException;
}
