package kademlia;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import kademlia.exceptions.KadServerDownException;
import kademlia.message.KademliaMessageFactory;
import kademlia.message.Message;
import kademlia.message.MessageFactory;
import kademlia.node.Node;
import kademlia.message.Receiver;

/**
 * The server that handles sending and receiving messages between nodes on the Kad Network
 *
 * @author Joshua Kissoon
 * @created 20140215
 */
public class KadServer
{

    /* Maximum size of a Datagram Packet */
    private static final int DATAGRAM_BUFFER_SIZE = 64 * 1024;      // 64KB

    /* Basic Kad Objects */
    private final transient KadConfiguration config;

    /* Server Objects */
    private final DatagramSocket socket;
    private transient boolean isRunning;
    private final Map<Integer, Receiver> receivers;
    private final Timer timer;      // Schedule future tasks
    private final Map<Integer, TimerTask> tasks;    // Keep track of scheduled tasks

    private final Node localNode;

    /* Factories */
    private final KademliaMessageFactory messageFactory;

    private final KadStatistician statistician;

    
    {
        isRunning = true;
        this.tasks = new HashMap<>();
        this.receivers = new HashMap<>();
        this.timer = new Timer(true);
    }

    /**
     * Initialize our KadServer
     *
     * @param udpPort      The port to listen on
     * @param mFactory     Factory used to create messages
     * @param localNode    Local node on which this server runs on
     * @param config
     * @param statistician A statistician to manage the server statistics
     *
     * @throws java.net.SocketException
     */
    public KadServer(int udpPort, MessageFactory mFactory, Node localNode, KadConfiguration config, KadStatistician statistician) throws SocketException
    {
        this.config = config;
        this.socket = new DatagramSocket(udpPort);
        this.localNode = localNode;
        this.messageFactory = mFactory;
        this.statistician = statistician;

        /* Start listening for incoming requests in a new thread */
        this.startListener();
    }

    /**
     * Starts the listener to listen for incoming messages
     */
    private void startListener()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                listen();
            }
        }.start();
    }

    /**
     * Sends a message
     *
     * @param msg  The message to send
     * @param to   The node to send the message to
     * @param recv The receiver to handle the response message
     *
     * @return Integer The communication ID of this message
     *
     * @throws IOException
     * @throws kademlia.exceptions.KadServerDownException
     */
    public synchronized int sendMessage(Node to, Message msg, Receiver recv) throws IOException, KadServerDownException
    {
        if (!isRunning)
        {
            throw new KadServerDownException(this.localNode + " - Kad Server is not running.");
        }

        /* Generate a random communication ID */
        int comm = new Random().nextInt();

        /* If we have a receiver */
        if (recv != null)
        {
            try
            {
                /* Setup the receiver to handle message response */
                receivers.put(comm, recv);
                TimerTask task = new TimeoutTask(comm, recv);
                timer.schedule(task, this.config.responseTimeout());
                tasks.put(comm, task);
            }
            catch (IllegalStateException ex)
            {
                /* The timer is already cancelled so we cannot do anything here really */
            }
        }

        /* Send the message */
        sendMessage(to, msg, comm);

        return comm;
    }

    /**
     * Method called to reply to a message received
     *
     * @param to   The Node to send the reply to
     * @param msg  The reply message
     * @param comm The communication ID - the one received
     *
     * @throws java.io.IOException
     */
    public synchronized void reply(Node to, Message msg, int comm) throws IOException
    {
        if (!isRunning)
        {
            throw new IllegalStateException("Kad Server is not running.");
        }
        sendMessage(to, msg, comm);
    }

    /**
     * Internal sendMessage method called by the public sendMessage method after a communicationId is generated
     */
    private void sendMessage(Node to, Message msg, int comm) throws IOException
    {
        /* Use a try-with resource to auto-close streams after usage */
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); DataOutputStream dout = new DataOutputStream(bout);)
        {
            /* Setup the message for transmission */
            dout.writeInt(comm);
            dout.writeByte(msg.code());
            msg.toStream(dout);
            dout.close();

            byte[] data = bout.toByteArray();

            if (data.length > DATAGRAM_BUFFER_SIZE)
            {
                throw new IOException("Message is too big");
            }

            /* Everything is good, now create the packet and send it */
            DatagramPacket pkt = new DatagramPacket(data, 0, data.length);
            pkt.setSocketAddress(to.getSocketAddress());
            socket.send(pkt);

            /* Lets inform the statistician that we've sent some data */
            this.statistician.sentData(data.length);
        }
    }

    /**
     * Listen for incoming messages in a separate thread
     */
    private void listen()
    {
        try
        {
            while (isRunning)
            {
                try
                {
                    /* Wait for a packet */
                    byte[] buffer = new byte[DATAGRAM_BUFFER_SIZE];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    /* Lets inform the statistician that we've received some data */
                    this.statistician.receivedData(packet.getLength());

                    if (this.config.isTesting())
                    {
                        /**
                         * Simulating network latency
                         * We pause for 1 millisecond/100 bytes
                         */
                        int pause = packet.getLength() / 100;
                        try
                        {
                            Thread.sleep(pause);
                        }
                        catch (InterruptedException ex)
                        {

                        }
                    }

                    /* We've received a packet, now handle it */
                    try (ByteArrayInputStream bin = new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength());
                            DataInputStream din = new DataInputStream(bin);)
                    {

                        /* Read in the conversation Id to know which handler to handle this response */
                        int comm = din.readInt();
                        byte messCode = din.readByte();

                        Message msg = messageFactory.createMessage(messCode, din);
                        din.close();

                        /* Get a receiver for this message */
                        Receiver receiver;
                        if (this.receivers.containsKey(comm))
                        {
                            /* If there is a reciever in the receivers to handle this */
                            synchronized (this)
                            {
                                receiver = this.receivers.remove(comm);
                                TimerTask task = (TimerTask) tasks.remove(comm);
                                if (task != null)
                                {
                                    task.cancel();
                                }
                            }
                        }
                        else
                        {
                            /* There is currently no receivers, try to get one */
                            receiver = messageFactory.createReceiver(messCode, this);
                        }

                        /* Invoke the receiver */
                        if (receiver != null)
                        {
                            receiver.receive(msg, comm);
                        }
                    }
                }
                catch (IOException e)
                {
                    //this.isRunning = false;
                    System.err.println("Server ran into a problem in listener method. Message: " + e.getMessage());
                }
            }
        }
        finally
        {
            if (!socket.isClosed())
            {
                socket.close();
            }
            this.isRunning = false;
        }
    }

    /**
     * Remove a conversation receiver
     *
     * @param comm The id of this conversation
     */
    private synchronized void unregister(int comm)
    {
        receivers.remove(comm);
        this.tasks.remove(comm);
    }

    /**
     * Stops listening and shuts down the server
     */
    public synchronized void shutdown()
    {
        this.isRunning = false;
        this.socket.close();
        timer.cancel();
    }

    /**
     * Task that gets called by a separate thread if a timeout for a receiver occurs.
     * When a reply arrives this task must be canceled using the <code>cancel()</code>
     * method inherited from <code>TimerTask</code>. In this case the caller is
     * responsible for removing the task from the <code>tasks</code> map.
     * */
    class TimeoutTask extends TimerTask
    {

        private final int comm;
        private final Receiver recv;

        public TimeoutTask(int comm, Receiver recv)
        {
            this.comm = comm;
            this.recv = recv;
        }

        @Override
        public void run()
        {
            if (!KadServer.this.isRunning)
            {
                return;
            }

            try
            {
                unregister(comm);
                recv.timeout(comm);
            }
            catch (IOException e)
            {
                System.err.println("Cannot unregister a receiver. Message: " + e.getMessage());
            }
        }
    }

    public void printReceivers()
    {
        for (Integer r : this.receivers.keySet())
        {
            System.out.println("Receiver for comm: " + r + "; Receiver: " + this.receivers.get(r));
        }
    }

    public boolean isRunning()
    {
        return this.isRunning;
    }

}
