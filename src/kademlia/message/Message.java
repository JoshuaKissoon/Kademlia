package kademlia.message;

public interface Message extends Streamable {
    /**
     * The unique code for the message type, used to differentiate all messages
     * from each other. Since this is of <code>byte</code> type there can
     * be at most 256 different message types.
     **/
    public byte code();
}
