package kademlia.exceptions;

/**
 * An exception used to indicate an unknown message type or communication identifier
 *
 * @author Joshua Kissoon
 * @created 20140219
 */
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
