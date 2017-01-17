package detector.NetwPrimitives.TrafficTable;

import detector.Alerter.Alerter;
import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Packet;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.NetwPrimitives.TrafficTable.TrafficSelectors.TrafficSelector;
import detector.DB_ProcessInfo;
import detector.OsProcessesPrimitives.NetProcess;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**************************************************************
* This class represents the tables of different traffic flows:
 * 1) TrafficFlow on concrete remote IP
 * 2) TrafficFlow from concrete source PORT
 * 3) TrafficFlow from concrete PROCESS
 *
 * This is a thread safe class.
* ************************************************************/
public class TrafficTable {


    private ConcurrentMap<IPv4Address, TrafficFlow> ipTraffic =
            new ConcurrentHashMap<IPv4Address, TrafficFlow>(32);
    private ConcurrentMap<Port, TrafficFlow> portTraffic =
            new ConcurrentHashMap<Port, TrafficFlow>(32);
    //private ConcurrentMap<Port, TrafficFlow> _undefinedProcessTraffic =
    //        new ConcurrentHashMap<Port, TrafficFlow>(32);
    private ConcurrentMap<NetProcess, TrafficFlow> processTraffic =
            new ConcurrentHashMap<NetProcess, TrafficFlow>(32);


    public TrafficTable()
    {

    }


    /*
    * Inserts new network packet to statistics tables
    * */
    public void add(Packet packet)
    {
        // Increases payload of destination IP
        IPv4Address ip = packet.getDestinationAddress();
        if(ip!=null && ip.isValid())
        {
            ipTraffic.putIfAbsent(ip, createNewFlow());
            ipTraffic.get(ip).addPayload(packet);
        }

        // Increases payload of concrete port
        Port port = packet.getSourcePort();
        if(port!=null && port.isValid())
        {
            portTraffic.putIfAbsent(port, createNewFlow());
            portTraffic.get(port).addPayload(packet);
        }

        // Increases payload of a process which owns this port
        NetProcess portOwnerProcess = DB_ProcessInfo.getInstance().getProcessOfPort(port);
        if(portOwnerProcess != null)
        {
            processTraffic.putIfAbsent(portOwnerProcess, createNewFlow());
            processTraffic.get(portOwnerProcess).addPayload(packet);
        }
        else
        {
            //_undefinedProcessTraffic.putIfAbsent(port, createNewFlow());
            //_undefinedProcessTraffic.get(port).addPayload(packet);
        }

        //resolveUndefinedProcessTraffic();
    }


    /*
    * Resolve unknown port owners(processes)
    * */
    /*private void resolveUndefinedProcessTraffic()
    {
        Iterator<Map.Entry<Port, TrafficFlow>> undProcess = _undefinedProcessTraffic.entrySet().iterator();
        while(undProcess.hasNext())
        {
            Map.Entry<Port, TrafficFlow> entry = undProcess.next();
            Port port = entry.getKey();
            TrafficFlow portTraffic = entry.getValue();
            NetProcess portOwner = DB_ProcessInfo.getInstance().getProcessOfPort(port);
            if(portOwner != null) // port owner is found
            {
                processTraffic.putIfAbsent(portOwner, createNewFlow());
                processTraffic.get(portOwner).mergeWith(portTraffic);

                undProcess.remove();
            }
            else // port owner still unknown
            {
                // if owner is undefined during long time than throw it away
                if(portTraffic.getInactivityTimeSec() >= 10f)
                    undProcess.remove();
            }
        }
    }*/


    /*
    * Just produces the new traffic flow
    * */
    protected TrafficFlow createNewFlow()
    {
        return new TrafficFlow();
    }


    /*
    * Removes each traffic record which exists in @sub
    * */
    public void removeIrrelevantSubset(TrafficTable sub)
    {
        for(Map.Entry<NetProcess, TrafficFlow> entry : sub.processTraffic.entrySet())
        {
            processTraffic.remove(entry.getKey());
            removeRelatedElements(entry.getValue());
        }

        for(Map.Entry<IPv4Address, TrafficFlow> entry : sub.ipTraffic.entrySet())
        {
            ipTraffic.remove(entry.getKey());
            removeRelatedElements(entry.getValue());
        }

        for(Map.Entry<Port, TrafficFlow> entry : sub.portTraffic.entrySet())
        {
            portTraffic.remove(entry.getKey());
            removeRelatedElements(entry.getValue());
        }
    }


    /*
    * Cleans all traffic tables where noticed traffic flows
    * related(by ip, port or process) to @trafficFlow
    * */
    private void removeRelatedElements(TrafficFlow trafficFlow)
    {
        NetProcess dominantProcess = trafficFlow.getDominantProcess();
        IPv4Address dominantIp = trafficFlow.getDominantDstAddr();
        Port dominantPort = trafficFlow.getDominantSrcPort();

        if(dominantProcess != null)
            processTraffic.remove(dominantProcess);
        if(dominantIp != null)
            ipTraffic.remove(dominantIp);
        if(dominantPort != null)
            portTraffic.remove(dominantPort);
    }


    /*
    * Removes each traffic record which exists so long(not active enough)
    * */
    public void removeInactive(float downTime)
    {
        Iterator<Map.Entry<IPv4Address, TrafficFlow>> ipItr = ipTraffic.entrySet().iterator();
        while(ipItr.hasNext())
            if(ipItr.next().getValue().getInactivityTimeSec() >= downTime)
                ipItr.remove();

        Iterator<Map.Entry<Port, TrafficFlow>> portItr = portTraffic.entrySet().iterator();
        while(portItr.hasNext())
            if(portItr.next().getValue().getInactivityTimeSec() >= downTime)
                portItr.remove();

        Iterator<Map.Entry<NetProcess, TrafficFlow>> processItr = processTraffic.entrySet().iterator();
        while(processItr.hasNext())
            if(processItr.next().getValue().getInactivityTimeSec() >= downTime)
                processItr.remove();
    }


    /*
    * This method returns a subset from traffic tables which desires the @selector`s condition
    * */
    public TrafficTable selectSubset(TrafficSelector selector)
    {
        TrafficTable selectedTable = new TrafficTable();

        for(IPv4Address ip : ipTraffic.keySet())
            if(selector.select(ip, ipTraffic.get(ip)))
                selectedTable.ipTraffic.put(ip, ipTraffic.get(ip));

        for(Port port : portTraffic.keySet())
            if(selector.select(port, portTraffic.get(port)))
                selectedTable.portTraffic.put(port, portTraffic.get(port));

        for(NetProcess process : processTraffic.keySet())
            if(selector.select(process, processTraffic.get(process)))
                selectedTable.processTraffic.put(process, processTraffic.get(process));

        return selectedTable;
    }


    /*
    * Determines if there is elements in different traffic tables(ip, port, process)
    * which are belong to the same activity. Remove them if detected.
    * */
    /*public void removeSimilarities()
    {
        // OS Processes has the highest collapsing priority
        if(processTraffic.size() > 0)
        {
            for (NetProcess process : processTraffic.keySet())
            {
                TrafficFlow processTraffic = processTraffic.get(process);
                IPv4Address dominantIP = processTraffic.getDominantDstAddr();
                Port dominantPort = processTraffic.getDominantSrcPort();

                if(dominantIP != null) {
                    TrafficFlow remIpTraffic = ipTraffic.remove(dominantIP);
                    if(remIpTraffic != null && remIpTraffic.getBytes() > processTraffic.getBytes())
                        processTraffic.put(process, remIpTraffic);
                }
                if(dominantPort != null) {
                    TrafficFlow remIpTraffic = portTraffic.remove(dominantPort);
                    if(remIpTraffic != null && remIpTraffic.getBytes() > processTraffic.getBytes())
                        processTraffic.put(process, remIpTraffic);
                }
            }
        }

        // Destination IPs has the middle collapsing priority
        if(ipTraffic.size() > 0)
        {
            for (IPv4Address ip : ipTraffic.keySet())
            {
                TrafficFlow ipTraffic = ipTraffic.get(ip);
                Port dominantPort = ipTraffic.get(ip).getDominantSrcPort();
                if(dominantPort != null) {
                    TrafficFlow remIpTraffic = portTraffic.remove(dominantPort);
                    if(remIpTraffic != null && remIpTraffic.getBytes() > ipTraffic.getBytes())
                        ipTraffic.put(ip, remIpTraffic);
                }
            }
        }

        // The lowest priority belongs to PORTs
        // ...
    }*/


    /*
    * Call complaining methods on traffic which contains
    * inside current traffic tables
    * */
    public void raiseComplaints(Alerter alerter)
    {
        Set<NetProcess> alertedProcess = new HashSet<NetProcess>(processTraffic.size());
        Set<IPv4Address> alertedIp = new HashSet<IPv4Address>(ipTraffic.size());
        Set<Port> alertedPort = new HashSet<Port>(portTraffic.size());

        // OS Processes has the highest alerting priority
        for(Map.Entry<NetProcess, TrafficFlow> entry : processTraffic.entrySet())
        {
            IPv4Address dominantIp = entry.getValue().getDominantDstAddr();
            Port dominantPort = entry.getValue().getDominantSrcPort();

            alertedIp.add(dominantIp);
            alertedPort.add(dominantPort);
            alertedProcess.add(entry.getKey());

            alerter.complainAboutProcess(entry.getKey(), entry.getValue());
        }

        // Destination IPs has the middle alerting priority
        for(Map.Entry<IPv4Address, TrafficFlow> entry : ipTraffic.entrySet())
        {
            NetProcess dominantProcess = entry.getValue().getDominantProcess();
            Port dominantPort = entry.getValue().getDominantSrcPort();

            if(!alertedIp.contains(entry.getKey()) && !alertedProcess.contains(dominantProcess))
            {
                alertedIp.add(entry.getKey());
                alertedPort.add(dominantPort);
                alertedProcess.add(dominantProcess);

                alerter.complainAboutIp(entry.getKey(), entry.getValue());
            }
        }

        // The lowest alerting priority belongs to PORTs
        for(Map.Entry<Port, TrafficFlow> entry : portTraffic.entrySet())
        {
            IPv4Address dominantIp = entry.getValue().getDominantDstAddr();
            NetProcess dominantProcess = entry.getValue().getDominantProcess();

            if(!alertedIp.contains(dominantIp) && !alertedProcess.contains(dominantProcess) && !alertedPort.contains(entry.getKey()))
            {
                alerter.complainAboutPort(entry.getKey(), entry.getValue());
            }
        }

    }
}
