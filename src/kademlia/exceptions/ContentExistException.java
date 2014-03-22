package kademlia.exceptions;

/**
 * An exception used to indicate that a content already exist on the DHT
 *
 * @author Joshua Kissoon
 * @created 20140322
 */
public class ContentExistException extends Exception
{

    public ContentExistException()
    {
        super();
    }

    public ContentExistException(String message)
    {
        super(message);
    }
}
