///**
// * @author Joshua Kissoon
// * @created 20140218
// * @desc Implementation of the Kademlia Ping operation
// */
//package kademlia.operation;
//
//import kademlia.core.KadServer;
//import kademlia.node.Node;
//
//public class PingOperation implements Operation
//{
//
//    private final KadServer server;
//    private final Node localNode;
//    private final Node toPing;
//
//    /**
//     * @param server The Kademlia server used to send & receive messages
//     * @param local  The local node
//     * @param toPing The node to send the ping message to
//     */
//    public PingOperation(KadServer server, Node local, Node toPing)
//    {
//        this.server = server;
//        this.localNode = local;
//        this.toPing = toPing;
//    }
//
//    @Override
//    public Object execute()
//    {
//        /* @todo Create a pingmessage and send this message to the toPing node, 
//         then handle the reply from this node using a reciever */
//    }
//}
