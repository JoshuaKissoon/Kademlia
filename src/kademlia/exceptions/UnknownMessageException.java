/**
 * @author Joshua Kissoon
 * @created 20140219
 * @desc An exception used to indicate an unknown message type or communication identifier
 */
package kademlia.exceptions;

public class UnknownMessageException extends RuntimeException
{

    public UnknownMessageException()
    {
        super();
    }

    public UnknownMessageException(String message)
    {
        super(message);
    }
}
