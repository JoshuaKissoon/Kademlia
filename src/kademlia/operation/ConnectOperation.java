/**
 * @author Joshua Kissoon
 * @created 20140218
 * @desc Operation that handles connecting to an existing Kademlia network using a bootstrap node
 */
package kademlia.operation;

import java.io.IOException;
import kademlia.core.Configuration;
import kademlia.core.KadServer;
import kademlia.exceptions.RoutingException;
import kademlia.message.AcknowledgeMessage;
import kademlia.message.ConnectMessage;
import kademlia.message.Message;
import kademlia.node.Node;

public class ConnectOperation implements Operation, Receiver
{

    public static final int MAX_CONNECT_ATTEMPTS = 5;       // Try 5 times to connect to a node

    private final KadServer server;
    private final Node localNode;
    private final Node bootstrapNode;

    private boolean error;
    private int attempts;

    /**
     * @param server    The message server used to send/receive messages
     * @param local     The local node
     * @param bootstrap Node to use to bootstrap the local node onto the network
     */
    public ConnectOperation(KadServer server, Node local, Node bootstrap)
    {
        this.server = server;
        this.localNode = local;
        this.bootstrapNode = bootstrap;
    }

    /**
     * @return null
     */
    @Override
    public synchronized void execute()
    {
        try
        {
            /* Contact the bootstrap node */
            this.error = true;
            this.attempts = 0;
            Message m = new ConnectMessage(this.localNode);

            /* Send a connect message to the bootstrap node */
            server.sendMessage(this.bootstrapNode, m, this);

            /* Wait for a while */
            wait(Configuration.OPERATION_TIMEOUT);

            if (error)
            {
                /* Means the contact failed */
                throw new RoutingException("Bootstrap node did not respond: " + bootstrapNode);
            }

            /* Perform lookup for our own ID to get nodes close to us */
            Operation lookup = new NodeLookupOperation(this.server, this.localNode, this.localNode.getNodeId());
            lookup.execute();

            /**
             * @todo Refresh buckets to get a good routing table
             * I think after the above lookup operation, K buckets will be filled
             * Not sure if this operation is needed here
             */

        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Receives an AcknowledgeMessage from the bootstrap node.
     *
     * @param comm
     */
    @Override
    public synchronized void receive(Message incoming, int comm)
    {
        /* The incoming message will be an acknowledgement message */
        AcknowledgeMessage msg = (AcknowledgeMessage) incoming;
        System.out.println("ConnectOperation now handling Acknowledgement Message: " + msg);

        /* The bootstrap node has responded, insert it into our space */
        this.localNode.getRoutingTable().insert(this.bootstrapNode);

        /* We got a response, so the error is false */
        error = false;

        /* Wake up any waiting thread */
        notify();
    }

    /**
     * Resends a ConnectMessage to the boot strap node a maximum of MAX_ATTEMPTS
     * times.
     *
     * @param comm
     *
     * @throws java.io.IOException
     */
    @Override
    public synchronized void timeout(int comm) throws IOException
    {
        System.out.println("Timeout function called");
        if (++this.attempts < MAX_CONNECT_ATTEMPTS)
        {
            this.server.sendMessage(this.bootstrapNode, new ConnectMessage(this.localNode), this);
        }
        else
        {
            /* We just exit, so notify all other threads that are possibly waiting */
            notify();
        }
    }
}
