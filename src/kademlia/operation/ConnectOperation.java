/**
 * @author Joshua Kissoon
 * @created 20140218
 * @desc Operation that handles connecting to an existing Kademlia network using a bootstrap node
 */
package kademlia.operation;

import java.io.IOException;
import kademlia.core.KadConfiguration;
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
    private final KadConfiguration config;

    private boolean error;
    private int attempts;

    /**
     * @param server    The message server used to send/receive messages
     * @param local     The local node
     * @param bootstrap Node to use to bootstrap the local node onto the network
     * @param config
     */
    public ConnectOperation(KadServer server, Node local, Node bootstrap, KadConfiguration config)
    {
        this.server = server;
        this.localNode = local;
        this.bootstrapNode = bootstrap;
        this.config = config;
    }

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

            /* If we haven't finished as yet, wait for a maximum of config.operationTimeout() time */
            int totalTimeWaited = 0;
            int timeInterval = 50;     // We re-check every 300 milliseconds
            while (totalTimeWaited < this.config.operationTimeout())
            {
                if (error)
                {
                    wait(timeInterval);
                    totalTimeWaited += timeInterval;
                }
                else
                {
                    break;
                }
            }
            if (error)
            {
                /* If we still haven't received any responses by then, do a routing timeout */
                throw new RoutingException("ConnectOperation: Bootstrap node did not respond: " + bootstrapNode);
            }

            /* Perform lookup for our own ID to get nodes close to us */
            Operation lookup = new NodeLookupOperation(this.server, this.localNode, this.localNode.getNodeId(), this.config);
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
