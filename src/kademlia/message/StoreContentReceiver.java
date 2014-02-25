package kademlia.message;

import kademlia.core.KadServer;
import kademlia.node.Node;
import kademlia.operation.Receiver;

/**
 * Receiver for incoming StoreContentMessage
 *
 * @author Joshua Kissoon
 * @since 20140225
 */
public class StoreContentReceiver implements Receiver
{

    private final KadServer server;
    private final Node localNode;

    public StoreContentReceiver(KadServer server, Node localNode)
    {
        this.server = server;
        this.localNode = localNode;
    }

    @Override
    public void receive(Message incoming, int comm)
    {
        /* @todo - Insert the message sender into this node's routing table */
        StoreContentMessage msg = (StoreContentMessage) incoming;
        System.out.println(this.localNode + " - Received a store content message");
        System.out.println(msg);
    }

    @Override
    public void timeout(int comm)
    {
        /* @todo Do something if the request times out */
    }
}
