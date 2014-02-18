/**
 * @author Joshua Kissoon
 * @created 20140219
 * @desc An exception to be thrown whenever there is a routing problem
 */
package kademlia.exceptions;

import java.io.IOException;

public class RoutingException extends IOException
{

    public RoutingException()
    {
        super();
    }

    public RoutingException(String message)
    {
        super(message);
    }
}
