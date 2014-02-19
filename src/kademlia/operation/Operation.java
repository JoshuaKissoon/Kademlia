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
     */
    public Object execute() throws IOException, RoutingException;
}
