package kademlia.serializer;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * A KadSerializer that serializes content to JSON format
 *
 * @param <T> The type of content to serialize
 *
 * @author Joshua Kissoon
 *
 * @since 20140225
 */
public class JsonSerializer<T> implements KadSerializer<T>
{

    private final Gson gson;

    
    {
        gson = new Gson();
    }

    @Override
    public void write(T data, DataOutputStream out) throws IOException
    {
        try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(out)))
        {
            writer.beginArray();

            /* Store the content type */
            gson.toJson(data.getClass().getName(), String.class, writer);

            /* Now Store the content */
            gson.toJson(data, data.getClass(), writer);

            writer.endArray();
        }
    }

    @Override
    public T read(DataInputStream in) throws IOException, ClassNotFoundException
    {
        try (DataInputStream din = new DataInputStream(in);
                JsonReader reader = new JsonReader(new InputStreamReader(in)))
        {
            reader.beginArray();

            /* Read the class name */
            String className = gson.fromJson(reader, String.class);

            /* Read and return the Content*/
            T ret = gson.fromJson(reader, Class.forName(className));
            
            reader.endArray();
            
            return ret;
        }
    }
}
