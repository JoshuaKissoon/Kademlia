/**
 * @author Joshua Kissoon
 * @created 20140217
 * @desc A simple message used for testing the system
 */
package kademlia.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SimpleMessage implements Message
{

    /* Message constants */
    public static final byte CODE = 0x07;

    private String content;

    public SimpleMessage(String message)
    {
        this.content = message;
    }

    public SimpleMessage(DataInputStream in)
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
    public void toStream(DataOutputStream out)
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
    public final void fromStream(DataInputStream in)
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
