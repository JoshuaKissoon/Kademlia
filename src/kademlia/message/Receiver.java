package kademlia.message;

import java.io.IOException;

/**
 * A receiver waits for incoming messages and perform some action when the message is received
 *
 * @author Joshua Kissoon
 * @created 20140218
 */
public interface Receiver
{

    /**
     * Message is received, now handle it
     *
     * @param conversationId The ID of this conversation, used for further conversations
     * @param incoming       The incoming
     *
     * @throws java.io.IOException
     */
    public void receive(Message incoming, int conversationId) throws IOException;

    /**
     * If no reply is received in <code>MessageServer.TIMEOUT</code> seconds for the
     * message with communication id <code>comm</code>, the MessageServer calls this method
     *
     * @param conversationId The conversation ID of this communication
     *
     * @throws IOException if an I/O error occurs
     * */
    public void timeout(int conversationId) throws IOException;
}
