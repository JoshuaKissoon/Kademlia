package kademlia.routing;

import java.util.Comparator;

/**
 * A Comparator to compare 2 contacts by their last seen time
 *
 * @author Joshua Kissoon
 * @since 20140426
 */
public class ContactLastSeenComparator implements Comparator<Contact>
{

    /**
     * Compare two contacts to determine their order in the Bucket,
     * Contacts are ordered by their last seen timestamp.
     *
     * @param c1 Contact 1
     * @param c2 Contact 2
     */
    @Override
    public int compare(Contact c1, Contact c2)
    {
        if (c1.getNode().equals(c2.getNode()))
        {
            return 0;
        }
        else
        {
            /* We may have 2 different contacts with same last seen values so we can't return 0 here */
            return c1.lastSeen() > c2.lastSeen() ? 1 : -1;
        }
    }
}
