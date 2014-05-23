package kademlia.message;

import java.io.IOException;
import kademlia.KadServer;
import kademlia.KademliaNode;
import kademlia.dht.KademliaDHT;

/**
 * Receiver for incoming StoreContentMessage
 *
 * @author Joshua Kissoon
 * @since 20140225
 */
public class StoreContentReceiver implements Receiver
{

    private final KadServer server;
    private final KademliaNode localNode;
    private final KademliaDHT dht;

    public StoreContentReceiver(KadServer server, KademliaNode localNode, KademliaDHT dht)
    {
        this.server = server;
        this.localNode = localNode;
        this.dht = dht;
    }

    @Override
    public void receive(Message incoming, int comm)
    {
        /* It's a StoreContentMessage we're receiving */
        StoreContentMessage msg = (StoreContentMessage) incoming;

        /* Insert the message sender into this node's routing table */
        this.localNode.getRoutingTable().insert(msg.getOrigin());

        try
        {
            /* Store this Content into the DHT */
            this.dht.store(msg.getContent());
        }
        catch (IOException e)
        {
            System.err.println("Unable to store received content; Message: " + e.getMessage());
        }

    }

    @Override
    public void timeout(int comm)
    {
        /**
         * This receiver only handles Receiving content when we've received the message,
         * so no timeout will happen with this receiver.
         */
    }
}
