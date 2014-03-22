package kademlia.message;

import java.io.IOException;
import java.util.List;
import kademlia.core.Configuration;
import kademlia.core.KadServer;
import kademlia.node.Node;
import kademlia.operation.Receiver;

/**
 * Receives a NodeLookupMessage and sends a NodeReplyMessage as reply with the K-Closest nodes to the ID sent.
 *
 * @author Joshua Kissoon
 * @created 20140219
 */
public class NodeLookupReceiver implements Receiver
{

    private final KadServer server;
    private final Node localNode;

    public NodeLookupReceiver(KadServer server, Node local)
    {
        this.server = server;
        this.localNode = local;
    }

    /**
     * Handle receiving a NodeLookupMessage
     * Find the set of K nodes closest to the lookup ID and return them
     *
     * @param comm
     *
     * @throws java.io.IOException
     */
    @Override
    public void receive(Message incoming, int comm) throws IOException
    {
        NodeLookupMessage msg = (NodeLookupMessage) incoming;

        Node origin = msg.getOrigin();

        /* Update the local space by inserting the origin node. */
        this.localNode.getRoutingTable().insert(origin);

        /* Find nodes closest to the LookupId */
        List<Node> nodes = this.localNode.getRoutingTable().findClosest(msg.getLookupId(), Configuration.K);

        /* Respond to the NodeLookupMessage */
        Message reply = new NodeReplyMessage(this.localNode, nodes);

        /* Let the Server send the reply */
        this.server.reply(origin, reply, comm);
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
