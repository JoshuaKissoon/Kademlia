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
import kademlia.routing.JKademliaRoutingTable;
import java.lang.reflect.Type;
import java.util.List;
import kademlia.KadConfiguration;
import kademlia.routing.Contact;
import kademlia.routing.KademliaRoutingTable;

/**
 * A KadSerializer that serializes routing tables to JSON format
 The generic serializer is not working for routing tables

 Why a JKademliaRoutingTable specific serializer?
 The routing table structure:
 - JKademliaRoutingTable
 -- Buckets[]
 --- Map<NodeId, Node>
 * ---- NodeId:KeyBytes
 * ---- Node: NodeId, InetAddress, Port
 *
 * The above structure seems to be causing some problem for Gson,
 * especially at the Map part.
 *
 * Solution
 - Make the Buckets[] transient
 - Simply store all Nodes in the serialized object
 - When reloading, re-add all nodes to the JKademliaRoutingTable
 *
 * @author Joshua Kissoon
 *
 * @since 20140310
 */
public class JsonRoutingTableSerializer implements KadSerializer<KademliaRoutingTable>
{

    private final Gson gson;

    Type contactCollectionType = new TypeToken<List<Contact>>()
    {
    }.getType();

    private final KadConfiguration config;

    
    {
        gson = new Gson();
    }

    /**
     * Initialize the class
     *
     * @param config
     */
    public JsonRoutingTableSerializer(KadConfiguration config)
    {
        this.config = config;
    }

    @Override
    public void write(KademliaRoutingTable data, DataOutputStream out) throws IOException
    {
        try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(out)))
        {
            writer.beginArray();

            /* Write the basic JKademliaRoutingTable */
            gson.toJson(data, JKademliaRoutingTable.class, writer);

            /* Now Store the Contacts  */
            gson.toJson(data.getAllContacts(), contactCollectionType, writer);

            writer.endArray();
        }
    }

    @Override
    public KademliaRoutingTable read(DataInputStream in) throws IOException, ClassNotFoundException
    {
        try (DataInputStream din = new DataInputStream(in);
                JsonReader reader = new JsonReader(new InputStreamReader(in)))
        {
            reader.beginArray();

            /* Read the basic JKademliaRoutingTable */
            KademliaRoutingTable tbl = gson.fromJson(reader, KademliaRoutingTable.class);
            tbl.setConfiguration(config);
            
            /* Now get the Contacts and add them back to the JKademliaRoutingTable */
            List<Contact> contacts = gson.fromJson(reader, contactCollectionType);
            tbl.initialize();

            for (Contact c : contacts)
            {
                tbl.insert(c);
            }

            reader.endArray();
            /* Read and return the Content*/
            return tbl;
        }
    }
}
