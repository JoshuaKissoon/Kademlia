package kademlia.util.serializer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.List;
import kademlia.dht.DHT;
import kademlia.dht.StorageEntryMetadata;

/**
 * A KadSerializer that serializes DHT to JSON format
 * The generic serializer is not working for DHT
 *
 * Why a DHT specific serializer?
 * The DHT structure:
 * - DHT
 * -- StorageEntriesManager
 * --- Map<NodeId, List<StorageEntry>>
 * ---- NodeId:KeyBytes
 * ---- List<StorageEntry>
 * ----- StorageEntry: Key, OwnerId, Type, Hash
 *
 * The above structure seems to be causing some problem for Gson, especially at the Map part.
 *
 * Solution
 * - Make the StorageEntriesManager transient
 * - Simply store all StorageEntry in the serialized object
 * - When reloading, re-add all StorageEntry to the DHT
 *
 * @author Joshua Kissoon
 *
 * @since 20140310
 */
public class JsonDHTSerializer implements KadSerializer<DHT>
{

    private final Gson gson;
    private final Type storageEntriesCollectionType;

    
    {
        gson = new Gson();

        storageEntriesCollectionType = new TypeToken<List<StorageEntryMetadata>>()
        {
        }.getType();
    }

    @Override
    public void write(DHT data, DataOutputStream out) throws IOException
    {
        try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(out)))
        {
            writer.beginArray();

            /* Write the basic DHT */
            gson.toJson(data, DHT.class, writer);

            /* Now Store the Entries  */
            gson.toJson(data.getStorageEntries(), this.storageEntriesCollectionType, writer);

            writer.endArray();
        }

    }

    @Override
    public DHT read(DataInputStream in) throws IOException, ClassNotFoundException
    {
        try (DataInputStream din = new DataInputStream(in);
                JsonReader reader = new JsonReader(new InputStreamReader(in)))
        {
            reader.beginArray();

            /* Read the basic DHT */
            DHT dht = gson.fromJson(reader, DHT.class);
            dht.initialize();

            /* Now get the entries and add them back to the DHT */
            List<StorageEntryMetadata> entries = gson.fromJson(reader, this.storageEntriesCollectionType);
            dht.putStorageEntries(entries);

            reader.endArray();
            return dht;
        }
    }
}
