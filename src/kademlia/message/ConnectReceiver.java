/**
 * @author Joshua Kissoon
 * @created 20140219
 * @desc Receives a ConnectMessage and sends an AcknowledgeMessage as reply
 */
package kademlia.message;

import java.io.IOException;
import kademlia.core.KadServer;
import kademlia.node.Node;
import kademlia.operation.Receiver;

public class ConnectReceiver implements Receiver
{

    private final KadServer server;
    private final Node localNode;

    public ConnectReceiver(KadServer server, Node local)
    {
        this.server = server;
        this.localNode = local;
    }

    /**
     * Handle receiving a ConnectMessage
     *
     * @param comm
     *
     * @throws java.io.IOException
     */
    @Override
    public void receive(Message incoming, int comm) throws IOException
    {
        System.out.println("Received incoming connect message, sending acknowledgement message.");
        ConnectMessage mess = (ConnectMessage) incoming;

        /* Update the local space by inserting the origin node. */
        this.localNode.getRoutingTable().insert(mess.getOrigin());

        /* Respond to the connect request */
        AcknowledgeMessage msg = new AcknowledgeMessage(this.localNode);

        /* Reply to the connect message with an Acknowledgement */
        this.server.reply(mess.getOrigin(), msg, comm);
    }

    /**
     * We don't need to do anything here
     *
     * @param comm
     *
     * @throws java.io.IOException
     */
    @Override
    public void timeout(int comm) throws IOException
    {
    }
}
