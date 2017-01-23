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
public class TrafficTable
{

    // Each of map below lets make more bondings "entity->trafficFlow"
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
        /*else
        {
            _undefinedProcessTraffic.putIfAbsent(port, createNewFlow());
            _undefinedProcessTraffic.get(port).addPayload(packet);
        }

        resolveUndefinedProcessTraffic();
        */
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
    * And remove all related to it if @removeRelated==true
    * */
    public void removeIrrelevantSubset(TrafficTable sub, boolean removeRelated)
    {
        for(Map.Entry<NetProcess, TrafficFlow> entry : sub.processTraffic.entrySet())
        {
            processTraffic.remove(entry.getKey());
            if(removeRelated)
                removeRelatedElements(entry.getValue());
        }

        for(Map.Entry<IPv4Address, TrafficFlow> entry : sub.ipTraffic.entrySet())
        {
            ipTraffic.remove(entry.getKey());
            if(removeRelated)
                removeRelatedElements(entry.getValue());
        }

        for(Map.Entry<Port, TrafficFlow> entry : sub.portTraffic.entrySet())
        {
            portTraffic.remove(entry.getKey());
            if(removeRelated)
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
            if(selector.select(ipTraffic.get(ip)))
                selectedTable.ipTraffic.put(ip, ipTraffic.get(ip));

        for(Port port : portTraffic.keySet())
            if(selector.select(portTraffic.get(port)))
                selectedTable.portTraffic.put(port, portTraffic.get(port));

        for(NetProcess process : processTraffic.keySet())
            if(selector.select(processTraffic.get(process)))
                selectedTable.processTraffic.put(process, processTraffic.get(process));

        return selectedTable;
    }


    /*
    * Call complaining methods on traffic which contains
    * inside current traffic table
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

            alerter.complainAboutFlow(entry.getValue());
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

                alerter.complainAboutFlow(entry.getValue());
            }
        }

        // The lowest alerting priority belongs to PORTs
        for(Map.Entry<Port, TrafficFlow> entry : portTraffic.entrySet())
        {
            IPv4Address dominantIp = entry.getValue().getDominantDstAddr();
            NetProcess dominantProcess = entry.getValue().getDominantProcess();

            if(!alertedIp.contains(dominantIp) && !alertedProcess.contains(dominantProcess) && !alertedPort.contains(entry.getKey()))
            {
                alerter.complainAboutFlow(entry.getValue());
            }
        }

    }
}
