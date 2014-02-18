/**
 * @author Joshua Kissoon
 * @created 20140218
 * @desc Serializes a message into a json message
 */
package kademlia.util;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import kademlia.message.Message;

public class JsonSerializer
{

    private final Gson gson;

    public JsonSerializer()
    {
        this.gson = new Gson();
    }

    /**
     * Writes a message to an output stream
     *
     * @param msg The message to write
     * @param out The output stream to write the message to
     */
    public void write(Message msg, OutputStream out)
    {
        try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(out)))
        {
            writer.beginArray();

            this.gson.toJson(msg, msg.getClass(), writer);
            
            writer.endArray();
        }
        catch (IOException e)
        {

        }
    }

}
