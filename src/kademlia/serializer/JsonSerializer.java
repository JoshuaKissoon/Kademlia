package kademlia.serializer;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import kademlia.dht.KadContent;

/**
 * A KadContentSerializer that serializes content to JSON format
 *
 * @author Joshua Kissoon
 * @since 20140225
 */
public class JsonSerializer implements KadContentSerializer
{

    private final Gson gson;

    
    {
        gson = new Gson();
    }

    @Override
    public void write(KadContent content, DataOutputStream out) throws IOException
    {
        try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(out)))
        {
            writer.beginArray();

            /* Store the content type */
            gson.toJson(content.getClass().getName(), String.class, writer);

            /* Now Store the content */
            gson.toJson(content, content.getClass(), writer);

            writer.endArray();
        }

    }

    @Override
    public KadContent read(DataInputStream in) throws IOException, ClassNotFoundException
    {
        try (DataInputStream din = new DataInputStream(in);
                JsonReader reader = new JsonReader(new InputStreamReader(in)))
        {
            reader.beginArray();

            /* Read the class name */
            String className = gson.fromJson(reader, String.class);

            /* Read and return the Content*/
            return gson.fromJson(reader, Class.forName(className));
        }
    }
}
