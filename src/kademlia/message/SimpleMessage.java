/**
 * @author Joshua Kissoon
 * @created 20140217
 * @desc A simple message used for testing the system
 */
package kademlia.message;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SimpleMessage implements Message
{

    /* Message constants */
    public static final byte CODE = 0x05;

    private String content;

    public SimpleMessage(String message)
    {
        this.content = message;
    }

    public SimpleMessage(DataInput in)
    {
        System.out.println("Creating message from input stream.");
        this.fromStream(in);
    }

    @Override
    public byte code()
    {
        return CODE;
    }

    @Override
    public void toStream(DataOutput out)
    {
        try
        {
            out.writeInt(this.content.length());
            out.writeBytes(this.content);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public final void fromStream(DataInput in)
    {
        try
        {
            byte[] buff = new byte[in.readInt()];
            in.readFully(buff);

            this.content = new String(buff);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        return this.content;
    }
}
