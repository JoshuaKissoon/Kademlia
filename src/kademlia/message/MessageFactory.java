/**
 * @author Joshua
 * @created
 * @desc
 */
package kademlia.message;

import java.io.DataInput;
import java.io.IOException;
import kademlia.core.KadServer;
import kademlia.node.Node;
import kademlia.operation.Receiver;

public class MessageFactory
{

    private final Node localNode;

    public MessageFactory(Node local)
    {
        this.localNode = local;
    }

    public Message createMessage(byte code, DataInput in) throws IOException
    {
        switch (code)
        {
            default:
            case SimpleMessage.CODE:
                return new SimpleMessage(in);
            case ConnectMessage.CODE:
                return new ConnectMessage(in);
            case AcknowledgeMessage.CODE:
                return new AcknowledgeMessage(in);
        }
    }

    public Receiver createReceiver(byte code, KadServer server)
    {
        switch (code)
        {
            default:
            case SimpleMessage.CODE:
                return new SimpleReceiver();
            case ConnectMessage.CODE:
                return new ConnectReceiver(server, this.localNode);
        }
    }
}
