package kademlia.exceptions;

/**
 * An exception to be thrown whenever the Kad Server is down
 *
 * @author Joshua Kissoon
 * @created 20140428
 */
public class KadServerDownException extends RoutingException
{

    public KadServerDownException()
    {
        super();
    }

    public KadServerDownException(String message)
    {
        super(message);
    }
}
