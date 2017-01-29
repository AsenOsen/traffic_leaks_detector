package detector.NetwPrimitives.TrafficFlow;

import detector.Db.DB_ProcessInfo;
import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Packet;
import detector.NetwPrimitives.Port;
import detector.OsProcessesPrimitives.NetProcess;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/*************************************************************************
 * This class contains a single traffic flow.
 * TrafficFlow - is a traffic attached to the concrete:
 *    1) IP
 *    2) Port
 *    3) Process
* ***********************************************************************/
public class TrafficFlow {

    protected volatile int totalPayload = 0;
    protected int totalPackets = 0;      // amount of addPayload() calls
    protected long firstActMillis = 0;   // time when the first byte was added
    protected long lastActMillis = 0;    // time of the last addPayload() call

    protected ConcurrentMap<IPv4Address, Integer> ipPayload   // payload in bytes of each detected DST IPs
            = new ConcurrentHashMap<IPv4Address, Integer>(32);
    protected ConcurrentMap<Port, Integer> portPayload        // payload in bytes of each detected SRC PORTs
            = new ConcurrentHashMap<Port, Integer>(32);
    //private ConcurrentMap<NetProcess, Integer> psPayload     // payload in bytes of each detected OS Process
    //        = new ConcurrentHashMap<NetProcess, Integer>(4);


    public TrafficFlow()
    {
        firstActMillis = System.currentTimeMillis();
    }


    /*
    * Adds new packet to a traffic flow
    * */
    public synchronized void addPayload(Packet additional)
    {
        if(totalPayload == 0)
            firstActMillis = System.currentTimeMillis();
        lastActMillis = System.currentTimeMillis();

        increaseTraffic(additional);
    }


    /*
    * Merges one TrafficFlow instance with another
    * */
    public synchronized void mergeWith(TrafficFlow traffic)
    {
        totalPayload += traffic.totalPayload;
        totalPackets += traffic.totalPackets;
        firstActMillis = Math.min(firstActMillis, traffic.firstActMillis);
        lastActMillis = Math.max(lastActMillis, traffic.lastActMillis);

        for(IPv4Address ip : traffic.ipPayload.keySet())
        {
            ipPayload.putIfAbsent(ip, 0);
            ipPayload.put(ip, ipPayload.get(ip) + traffic.ipPayload.get(ip));
        }
        for(Port port : traffic.portPayload.keySet())
        {
            portPayload.putIfAbsent(port, 0);
            portPayload.put(port, portPayload.get(port) + traffic.portPayload.get(port));
        }
    }


    /*
    *  Extracts from packet`s header an additional data.
     * This is needed for storing the information about
     * active ports/IPs/etc. for traffic flow.
    * */
    protected void increaseTraffic(Packet packet)
    {
        IPv4Address dstPacketIp = packet.getDestinationAddress();
        Port srcPacketPort = packet.getSourcePort();
        int payload = packet.getPayloadSize();

        totalPayload += payload;
        totalPackets++;

        ipPayload.putIfAbsent(dstPacketIp, 0);
        ipPayload.put(dstPacketIp, ipPayload.get(dstPacketIp)+payload);

        portPayload.putIfAbsent(srcPacketPort, 0);
        portPayload.put(srcPacketPort, portPayload.get(srcPacketPort)+payload);
    }


    /*
    * Return total payload of a traffic flow in bytes
    * */
    public int getBytes()
    {
        return totalPayload;
    }


    /*
    * Returns the time of monitoring specific traffic
    * */
    public float getInactivityTimeSec()
    {
        return (System.currentTimeMillis() - lastActMillis) / 1000f;
    }


    /*
    * Return time in seconds during which traffic have been collected
    * */
    public float getActivityTimeSec()
    {
        return (lastActMillis - firstActMillis) / 1000f;
    }


    /*
    * Returns the destination IP traffic was mostly generated by
    * */
    public IPv4Address getDominantDstAddr()
    {
        // Check each DST IP on dominance
        for(IPv4Address member : ipPayload.keySet())
            if(ipPayload.get(member) >= getDominancePayloadSize())
                return member;

        return null;
    }


    /*
    * Returns the source PORT traffic was mostly generated by
    * */
    public Port getDominantSrcPort()
    {
        // Check each SRC PORT on dominance
        for(Port member : portPayload.keySet())
            if(portPayload.get(member) >= getDominancePayloadSize())
                return member;

        return null;
    }


    /*
    * Returns most probable process-initiator of the traffic flow
    * */
    public NetProcess getDominantProcess()
    {
        HashMap<NetProcess, Integer> processPayload = new HashMap<NetProcess, Integer>();

        // make up table netProcess:trafficFlow
        for(Map.Entry<Port, Integer> entry : portPayload.entrySet())
        {
            NetProcess portOwner = entry.getKey().getOwnerProcess();
            if(portOwner == null)
                continue;

            int payload = entry.getValue();
            if( processPayload.containsKey(portOwner) )
                processPayload.put(portOwner, processPayload.get(portOwner) + payload);
            else
                processPayload.put(portOwner, payload);
        }

        int maxDetectedTraffic = 0;
        NetProcess probableInitiator = null;

        // pick the process with the biggest trafficFlow
        for(Map.Entry<NetProcess, Integer> entry : processPayload.entrySet())
        {
            if(entry.getValue() > maxDetectedTraffic)
            {
                maxDetectedTraffic = entry.getValue();
                probableInitiator = entry.getKey();
            }
        }

        // probableInitiator`s traffic MUST be dominant in entire traffic flow
        return maxDetectedTraffic >= getDominancePayloadSize() ?
                probableInitiator : null;
    }


    private float getDominancePayloadSize()
    {
        // More than 40% of all traffic was generated by single element
        return totalPayload * 0.4f;
    }


    @Override
    public String toString() {

        StringBuilder meanIps = new StringBuilder();
        StringBuilder meanPorts = new StringBuilder();

        for(IPv4Address ip : ipPayload.keySet())
                meanIps.append(ip+"("+ ipPayload.get(ip)+"b), ");
        for(Port port : portPayload.keySet())
                meanPorts.append(port+"("+ portPayload.get(port)+"b)"+
                        DB_ProcessInfo.getInstance().getProcessOfPort(port)+", ");

        IPv4Address dominant = getDominantDstAddr();

        return "Total KBytes: "+(totalPayload/1024d)+" | "+
                "AvgPacketSize(b): "+((float)totalPayload/ totalPackets)+" | "+
                "Uptime(s): "+ getActivityTimeSec()+" | "+
                "\nDestinations: "+meanIps+
                "\nSources: "+meanPorts+
                "\nDominant destination: "+ (dominant == null ? "null" : dominant.getIpInfo())+
                "\nDominant source: "+ getDominantSrcPort();
    }

}
