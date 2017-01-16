package detector.NetwPrimitives.TrafficFlow;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Describes the traffic during 1 second
 */
public class MomentaryTrafficFlow
{

    private long momentumId;
    private volatile int totalPayload = 0;
    private volatile int totalPackets = 0;

    private ConcurrentMap<IPv4Address, Integer> ipPayload   // payload in bytes of each detected DST IPs
            = new ConcurrentHashMap<IPv4Address, Integer>(32);
    private ConcurrentMap<Port, Integer> portPayload        // payload in bytes of each detected SRC PORTs
            = new ConcurrentHashMap<Port, Integer>(32);


    public MomentaryTrafficFlow()
    {
        momentumId = System.currentTimeMillis() / 1000;
    }


    public boolean isCurrentMoment()
    {
        return momentumId == (System.currentTimeMillis() / 1000);
    }


    public boolean isMomentOutdated(int forLastSeconds)
    {
        return ( (System.currentTimeMillis()/1000) - momentumId ) > forLastSeconds;
    }


    public void addPacket(int packetSize)
    {
        totalPayload += packetSize;
        totalPackets++;
    }


    public void addTraffic(IPv4Address ip, int payload)
    {
        ipPayload.putIfAbsent(ip, 0);
        ipPayload.put(ip, ipPayload.get(ip) + payload);
    }


    public void addTraffic(Port port, int payload)
    {
        portPayload.putIfAbsent(port, 0);
        portPayload.put(port, portPayload.get(port) + payload);
    }


    public void merge(MomentaryTrafficFlow moment)
    {
        if(moment == null)
            return;

        totalPayload += moment.totalPayload;
        totalPackets += moment.totalPackets;

        for(IPv4Address ip : moment.ipPayload.keySet())
        {
            ipPayload.putIfAbsent(ip, 0);
            ipPayload.put(ip, ipPayload.get(ip) + moment.ipPayload.get(ip));
        }
        for(Port port : moment.portPayload.keySet())
        {
            portPayload.putIfAbsent(port, 0);
            portPayload.put(port, portPayload.get(port) + moment.portPayload.get(port));
        }
    }


    public int getTotalPayload()
    {
        return totalPayload;
    }

    public int getTotalPackets()
    {
        return totalPackets;
    }


    public int getTrafficForIp(IPv4Address ip)
    {
        return ipPayload.containsKey(ip) ? ipPayload.get(ip) : 0;
    }

    public int getTrafficForPort(Port port)
    {
        return portPayload.containsKey(port) ? portPayload.get(port) : 0;
    }


    @Override
    public int hashCode()
    {
        return (int) momentumId;
    }


    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof MomentaryTrafficFlow))
            return false;

        return obj.hashCode() == this.hashCode();
    }


    @Override
    public String toString() {
        return momentumId+" - "+totalPayload;
    }
}

