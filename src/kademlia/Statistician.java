package kademlia;

import java.text.DecimalFormat;

/**
 * Class that keeps statistics for this Kademlia instance.
 *
 * These statistics are temporary and will be lost when Kad is shut down.
 *
 * @author Joshua Kissoon
 * @since 20140505
 */
public class Statistician implements KadStatistician
{

    /* How much data was sent and received by the server over the network */
    private long totalDataSent, totalDataReceived;
    private long numDataSent, numDataReceived;

    /* Bootstrap timings */
    private long bootstrapTime;

    /* Content lookup operation timing & route length */
    private int numContentLookups, numFailedContentLookups;
    private long totalContentLookupTime;
    private long totalRouteLength;

    
    {
        this.totalDataSent = 0;
        this.totalDataReceived = 0;
        this.bootstrapTime = 0;
        this.numContentLookups = 0;
        this.totalContentLookupTime = 0;
        this.totalRouteLength = 0;
    }

    @Override
    public void sentData(long size)
    {
        this.totalDataSent += size;
        this.numDataSent++;
    }

    @Override
    public long getTotalDataSent()
    {
        if (this.totalDataSent == 0)
        {
            return 0L;
        }
        
        return this.totalDataSent / 1000L;
    }

    @Override
    public void receivedData(long size)
    {
        this.totalDataReceived += size;
        this.numDataReceived++;
    }

    @Override
    public long getTotalDataReceived()
    {
        if (this.totalDataReceived == 0)
        {
            return 0L;
        }
        return this.totalDataReceived / 1000L;
    }

    @Override
    public void setBootstrapTime(long time)
    {
        this.bootstrapTime = time;
    }

    @Override
    public long getBootstrapTime()
    {
        return this.bootstrapTime / 1000000L;
    }

    @Override
    public void addContentLookup(long time, int routeLength, boolean isSuccessful)
    {
        if (isSuccessful)
        {
            this.numContentLookups++;
            this.totalContentLookupTime += time;
            this.totalRouteLength += routeLength;
        }
        else
        {
            this.numFailedContentLookups++;
        }
    }

    @Override
    public int numContentLookups()
    {
        return this.numContentLookups;
    }

    @Override
    public int numFailedContentLookups()
    {
        return this.numFailedContentLookups;
    }

    @Override
    public long totalContentLookupTime()
    {
        return this.totalContentLookupTime;
    }

    @Override
    public double averageContentLookupTime()
    {
        if (this.numContentLookups == 0)
        {
            return 0D;
        }

        double avg = (double) ((double) this.totalContentLookupTime / (double) this.numContentLookups) / 1000000D;
        DecimalFormat df = new DecimalFormat("#.00");
        return new Double(df.format(avg));
    }

    @Override
    public double averageContentLookupRouteLength()
    {
        if (this.numContentLookups == 0)
        {
            return 0D;
        }
        double avg = (double) ((double) this.totalRouteLength / (double) this.numContentLookups);
        DecimalFormat df = new DecimalFormat("#.00");
        return new Double(df.format(avg));
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("Statistician: [");

        sb.append("Bootstrap Time: ");
        sb.append(this.getBootstrapTime());
        sb.append("; ");

        sb.append("Data Sent: ");
        sb.append("(");
        sb.append(this.numDataSent);
        sb.append(") ");
        sb.append(this.getTotalDataSent());
        sb.append(" bytes; ");

        sb.append("Data Received: ");
        sb.append("(");
        sb.append(this.numDataReceived);
        sb.append(") ");
        sb.append(this.getTotalDataReceived());
        sb.append(" bytes; ");

        sb.append("Num Content Lookups: ");
        sb.append(this.numContentLookups());
        sb.append("; ");

        sb.append("Avg Content Lookup Time: ");
        sb.append(this.averageContentLookupTime());
        sb.append("; ");

        sb.append("Avg Content Lookup Route Lth: ");
        sb.append(this.averageContentLookupRouteLength());
        sb.append("; ");

        sb.append("]");

        return sb.toString();
    }
}
