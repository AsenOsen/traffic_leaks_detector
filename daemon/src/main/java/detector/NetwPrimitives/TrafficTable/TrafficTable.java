package detector.NetwPrimitives.TrafficTable;

import detector.Alerter.Alerter;
import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Packet;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow;
import detector.NetwPrimitives.TrafficTable.TrafficSelectors.TrafficSelector;
import detector.DB_OsProcessesInfo;
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


    private ConcurrentMap<IPv4Address, TrafficFlow> _ipTraffic   =
            new ConcurrentHashMap<IPv4Address, TrafficFlow>(32);
    private ConcurrentMap<Port, TrafficFlow> _portTraffic =
            new ConcurrentHashMap<Port, TrafficFlow>(32);
    private ConcurrentMap<Port, TrafficFlow> _undefinedProcessTraffic =
            new ConcurrentHashMap<Port, TrafficFlow>(32);
    private ConcurrentMap<NetProcess, TrafficFlow> _processTraffic =
            new ConcurrentHashMap<NetProcess, TrafficFlow>(32);


    public TrafficTable()
    {

    }


    /*
    * Inserts new network packet to statistics tables
    * */
    public /*synchronized*/ void add(Packet packet)
    {
        // Increases payload of destination IP
        IPv4Address ip = packet.getDestinationAddress();
        if(ip!=null && ip.isValid())
        {
            _ipTraffic.putIfAbsent(ip, createNewFlow());
            _ipTraffic.get(ip).addPayload(packet);
        }

        // Increases payload of concrete port
        Port port = packet.getSourcePort();
        if(port!=null && port.isValid())
        {
            _portTraffic.putIfAbsent(port, createNewFlow());
            _portTraffic.get(port).addPayload(packet);
        }

        // Increases payload of a process which owns this port
        NetProcess portOwnerProcess = DB_OsProcessesInfo.getInstance().getProcessOfPort(port);
        if(portOwnerProcess != null)
        {
            _processTraffic.putIfAbsent(portOwnerProcess, createNewFlow());
            _processTraffic.get(portOwnerProcess).addPayload(packet);
        }
        else
        {
            _undefinedProcessTraffic.putIfAbsent(port, createNewFlow());
            _undefinedProcessTraffic.get(port).addPayload(packet);
        }

        resolveUndefinedProcessTraffic();
    }


    /*
    * Resolve unknown port owners(processes)
    * */
    private void resolveUndefinedProcessTraffic()
    {
        Iterator<Map.Entry<Port, TrafficFlow>> undProcess = _undefinedProcessTraffic.entrySet().iterator();
        while(undProcess.hasNext())
        {
            Map.Entry<Port, TrafficFlow> entry = undProcess.next();
            Port port = entry.getKey();
            TrafficFlow portTraffic = entry.getValue();
            NetProcess portOwner = DB_OsProcessesInfo.getInstance().getProcessOfPort(port);
            if(portOwner != null) // port owner is found
            {
                _processTraffic.putIfAbsent(portOwner, createNewFlow());
                _processTraffic.get(portOwner).mergeWith(portTraffic);

                undProcess.remove();
            }
            else // port owner still unknown
            {
                // if owner is undefined during long time than throw it away
                if(portTraffic.getInactivityTimeSec() >= 10f)
                    undProcess.remove();
            }
        }
    }


    /*
    * Just produces the new traffic flow
    * */
    private TrafficFlow createNewFlow()
    {
        return new TrafficFlow();
    }


    /*
    * Removes each traffic record which exists in @sub
    * */
    public /*synchronized*/ void removeSubset(TrafficTable sub)
    {
        for(IPv4Address ip : sub._ipTraffic.keySet())
            _ipTraffic.remove(ip);

        for(Port port : sub._portTraffic.keySet())
            _portTraffic.remove(port);

        for(NetProcess process : sub._processTraffic.keySet())
            _processTraffic.remove(process);
    }


    /*
    * Removes each traffic record which exists so long(not active enough)
    * */
    public /*synchronized*/ void removeInactive(float downTime)
    {
        Iterator<Map.Entry<IPv4Address, TrafficFlow>> ipTraffic =
                _ipTraffic.entrySet().iterator();
        while(ipTraffic.hasNext())
        {
            if(ipTraffic.next().getValue().getInactivityTimeSec() >= downTime)
                ipTraffic.remove();
        }

        Iterator<Map.Entry<Port, TrafficFlow>> portTraffic =
                _portTraffic.entrySet().iterator();
        while(portTraffic.hasNext())
        {
            if(portTraffic.next().getValue().getInactivityTimeSec() >= downTime)
                portTraffic.remove();
        }

        Iterator<Map.Entry<NetProcess, TrafficFlow>> processTraffic =
                _processTraffic.entrySet().iterator();
        while(processTraffic.hasNext())
        {
            if(processTraffic.next().getValue().getInactivityTimeSec() >= downTime)
                processTraffic.remove();
        }
    }


    /*
    * This method returns a subset from traffic tables which desires the @selector`s condition
    * */
    public /*synchronized*/ TrafficTable selectSubset(TrafficSelector selector)
    {
        TrafficTable selectedTable = new TrafficTable();

        for(IPv4Address ip : _ipTraffic.keySet())
            if(selector.select(ip, _ipTraffic.get(ip)))
                selectedTable._ipTraffic.put(ip, _ipTraffic.get(ip));

        for(Port port : _portTraffic.keySet())
            if(selector.select(port, _portTraffic.get(port)))
                selectedTable._portTraffic.put(port, _portTraffic.get(port));

        for(NetProcess process : _processTraffic.keySet())
            if(selector.select(process, _processTraffic.get(process)))
                selectedTable._processTraffic.put(process, _processTraffic.get(process));

        return selectedTable;
    }


    /*
    * Determines if there is elements in different traffic tables(ip, port, process)
    * which are belong to the same activity. Remove them if detected.
    * */
    /*public void removeSimilarities()
    {
        // OS Processes has the highest collapsing priority
        if(_processTraffic.size() > 0)
        {
            for (NetProcess process : _processTraffic.keySet())
            {
                TrafficFlow processTraffic = _processTraffic.get(process);
                IPv4Address dominantIP = processTraffic.getDominantDstAddr();
                Port dominantPort = processTraffic.getDominantSrcPort();

                if(dominantIP != null) {
                    TrafficFlow remIpTraffic = _ipTraffic.remove(dominantIP);
                    if(remIpTraffic != null && remIpTraffic.getBytes() > processTraffic.getBytes())
                        _processTraffic.put(process, remIpTraffic);
                }
                if(dominantPort != null) {
                    TrafficFlow remIpTraffic = _portTraffic.remove(dominantPort);
                    if(remIpTraffic != null && remIpTraffic.getBytes() > processTraffic.getBytes())
                        _processTraffic.put(process, remIpTraffic);
                }
            }
        }

        // Destination IPs has the middle collapsing priority
        if(_ipTraffic.size() > 0)
        {
            for (IPv4Address ip : _ipTraffic.keySet())
            {
                TrafficFlow ipTraffic = _ipTraffic.get(ip);
                Port dominantPort = _ipTraffic.get(ip).getDominantSrcPort();
                if(dominantPort != null) {
                    TrafficFlow remIpTraffic = _portTraffic.remove(dominantPort);
                    if(remIpTraffic != null && remIpTraffic.getBytes() > ipTraffic.getBytes())
                        _ipTraffic.put(ip, remIpTraffic);
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
        Set<NetProcess> alertedProcess = new HashSet<NetProcess>(_processTraffic.size());
        Set<IPv4Address> alertedIp = new HashSet<IPv4Address>(_ipTraffic.size());
        Set<Port> alertedPort = new HashSet<Port>(_portTraffic.size());

        // OS Processes has the highest alerting priority
        for(Map.Entry<NetProcess, TrafficFlow> entry : _processTraffic.entrySet())
        {
            IPv4Address dominantIp = entry.getValue().getDominantDstAddr();
            Port dominantPort = entry.getValue().getDominantSrcPort();

            alertedIp.add(dominantIp);
            alertedPort.add(dominantPort);
            alertedProcess.add(entry.getKey());

            alerter.complainAboutProcess(entry.getKey(), entry.getValue());
        }

        // Destination IPs has the middle alerting priority
        for(Map.Entry<IPv4Address, TrafficFlow> entry : _ipTraffic.entrySet())
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
        for(Map.Entry<Port, TrafficFlow> entry : _portTraffic.entrySet())
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
