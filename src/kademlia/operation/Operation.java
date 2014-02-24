/**
 * @author Joshua Kissoon
 * @created 20140218
 * @desc Interface for different Kademlia operations
 */
package kademlia.operation;

import java.io.IOException;
import kademlia.exceptions.RoutingException;

public interface Operation
{

    /**
     * Starts an operation and returns when the operation is finished
     *
     * @return The return value can differ per operation
     *
     * @throws kademlia.exceptions.RoutingException
     * 
     * @todo Remove the Object return type, those operations that return things should have a method to return the data
     */
    public Object execute() throws IOException, RoutingException;
}
