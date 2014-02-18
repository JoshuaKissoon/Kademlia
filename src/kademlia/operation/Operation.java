/**
 * @author Joshua Kissoon
 * @created 20140218
 * @desc Interface for different Kademlia operations
 */
package kademlia.operation;

public interface Operation
{

    /**
     * Starts an operation and returns when the operation is finished
     *
     * @return The return value can differ per operation
     */
    public Object execute();
}
