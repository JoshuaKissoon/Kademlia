package kademlia.util;

import java.util.Collection;
import java.util.HashMap;
import kademlia.node.Node;

/**
 * Class that helps compute the route length taken to complete an operation.
 *
 * Only used for routing operations - mainly the NodeLookup and ContentLookup Operations.
 *
 * Idea:
 * - Add the original set of nodes with route length 0;
 * - When we get a node reply with a set of nodes, we add those nodes and set the route length to their sender route length + 1
 *
 * @author Joshua Kissoon
 * @since 20140510
 */
public class RouteLengthChecker
{

    /* Store the nodes and their route length (RL) */
    private final HashMap<Node, Integer> nodes;

    /* Lets cache the max route length instead of having to go and search for it later */
    private int maxRouteLength;

    
    {
        this.nodes = new HashMap<>();
        this.maxRouteLength = 1;
    }

    /**
     * Add the initial nodes in the routing operation
     *
     * @param initialNodes The set of initial nodes
     */
    public void addInitialNodes(Collection<Node> initialNodes)
    {
        for (Node n : initialNodes)
        {
            this.nodes.put(n, 1);
        }
    }

    /**
     * Add any nodes that we get from a node reply.
     *
     * The route length of these nodes will be their sender + 1;
     *
     * @param inputSet The set of nodes we receive
     * @param sender   The node who send the set
     */
    public void addNodes(Collection<Node> inputSet, Node sender)
    {
        if (!this.nodes.containsKey(sender))
        {
            return;
        }

        /* Get the route length of the input set - sender RL + 1 */
        int inputSetRL = this.nodes.get(sender) + 1;

        if (inputSetRL > this.maxRouteLength)
        {
            this.maxRouteLength = inputSetRL;
        }

        /* Add the nodes to our set */
        for (Node n : inputSet)
        {
            /* We only add if the node is not already there... */
            if (!this.nodes.containsKey(n))
            {
                this.nodes.put(n, inputSetRL);
            }
        }
    }

    /**
     * Get the route length of the operation!
     *
     * It will be the max route length of all the nodes here.
     *
     * @return The route length
     */
    public int getRouteLength()
    {
        return this.maxRouteLength;
    }
}
