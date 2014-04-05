package kademlia.util.hashing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A class that is used to calculate the hash of strings.
 *
 * @author Joshua Kissoon
 * @since 20140405
 */
public class HashCalculator
{

    public static byte[] sha1Hash(String toHash) throws NoSuchAlgorithmException
    {
        /* Create a MessageDigest */
        MessageDigest md = MessageDigest.getInstance("SHA-1");

        /* Add password bytes to digest */
        md.update(toHash.getBytes());

        /* Get the hashed bytes */
        return md.digest();
    }
}
