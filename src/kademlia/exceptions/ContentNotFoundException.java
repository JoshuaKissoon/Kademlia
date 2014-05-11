package kademlia.exceptions;

/**
 * An exception used to indicate that a content does not exist on the DHT
 *
 * @author Joshua Kissoon
 * @created 20140322
 */
public class ContentNotFoundException extends Exception
{

    public ContentNotFoundException()
    {
        super();
    }

    public ContentNotFoundException(String message)
    {
        super(message);
    }
}
