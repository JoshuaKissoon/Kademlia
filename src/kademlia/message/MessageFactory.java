package kademlia.message;

import java.io.DataInputStream;
import java.io.IOException;
import kademlia.core.KadConfiguration;
import kademlia.core.KadServer;
import kademlia.dht.DHT;
import kademlia.node.Node;

/**
 * Handles creating messages and receivers
 *
 * @author Joshua Kissoon
 * @since 20140202
 */
public class MessageFactory
{

    private final Node localNode;
    private final DHT dht;
    private final KadConfiguration config;

    public MessageFactory(Node local, DHT dht, KadConfiguration config)
    {
        this.localNode = local;
        this.dht = dht;
        this.config = config;
    }

    public Message createMessage(byte code, DataInputStream in) throws IOException
    {
        switch (code)
        {
            case AcknowledgeMessage.CODE:
                return new AcknowledgeMessage(in);
            case ConnectMessage.CODE:
                return new ConnectMessage(in);
            case ContentMessage.CODE:
                return new ContentMessage(in);
            case ContentLookupMessage.CODE:
                return new ContentLookupMessage(in);
            case NodeLookupMessage.CODE:
                return new NodeLookupMessage(in);
            case NodeReplyMessage.CODE:
                return new NodeReplyMessage(in);
            case SimpleMessage.CODE:
                return new SimpleMessage(in);
            case StoreContentMessage.CODE:
                return new StoreContentMessage(in);
            default:
                System.out.println(this.localNode + " - No Message handler found for message. Code: " + code);
                return new SimpleMessage(in);

        }
    }

    public Receiver createReceiver(byte code, KadServer server)
    {
        switch (code)
        {
            case ConnectMessage.CODE:
                return new ConnectReceiver(server, this.localNode);
            case ContentLookupMessage.CODE:
                return new ContentLookupReceiver(server, this.localNode, this.dht, this.config);
            case NodeLookupMessage.CODE:
                return new NodeLookupReceiver(server, this.localNode, this.config);
            case SimpleMessage.CODE:
                return new SimpleReceiver();
            case StoreContentMessage.CODE:
                return new StoreContentReceiver(server, this.localNode, this.dht);
            default:
                System.out.println("No reveiver found for message. Code: " + code);
                return new SimpleReceiver();
        }
    }
}
