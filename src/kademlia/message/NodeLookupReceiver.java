/**
 * @author Joshua Kissoon
 * @created 20140219
 * @desc Receives a ConnectMessage and sends an AcknowledgeMessage as reply
 */
package kademlia.message;

import java.io.IOException;
import java.util.List;
import kademlia.core.Configuration;
import kademlia.core.KadServer;
import kademlia.node.Node;
import kademlia.operation.Receiver;

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

        System.out.println(this.localNode.getNodeId() + ": Received NodeLookupMessage, sending NodeReplyMessage to: " + origin.getNodeId());

        /* Update the local space by inserting the origin node. */
        this.localNode.getRoutingTable().insert(origin);

        /* Find nodes closest to the LookupId */
        List<Node> nodes = this.localNode.getRoutingTable().findClosest(msg.getLookupId(), Configuration.K);

        System.out.println("\nClosest Nodes: ");
        for (Node n : nodes)
        {
            System.out.println(n.getNodeId());
        }
        System.out.println();

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
