package kademlia.operation;

import java.io.IOException;
import kademlia.exceptions.RoutingException;

/**
 * An operation in the Kademlia routing protocol
 *
 * @author Joshua Kissoon
 * @created 20140218
 */
public interface Operation
{

    /**
     * Starts an operation and returns when the operation is finished
     *
     * @throws kademlia.exceptions.RoutingException
     *
     * @todo Remove the Object return type, those operations that return things should have a method to return the data
     */
    public void execute() throws IOException, RoutingException;
}
