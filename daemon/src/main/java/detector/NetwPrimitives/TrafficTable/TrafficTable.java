package detector.NetwPrimitives.TrafficTable;

import detector.Alerter.Alerter;
import detector.Data.ProcessInfoDB;
import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Packet;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.NetwPrimitives.TrafficTable.TrafficOperations.TrafficCleaner;
import detector.NetwPrimitives.TrafficTable.TrafficOperations.TrafficSelector;
import detector.OsProcessesPrimitives.NetProcess;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**************************************************************
* This class represents the tables of different traffic flows:
 * 1) TrafficFlow to concrete remote IP
 * 2) TrafficFlow from concrete source PORT
 * 3) TrafficFlow from concrete PROCESS
 *
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
        assert packet!=null;

        // Increases payload of destination IP
        IPv4Address ip = packet.getDestinationAddress();
        if(ip!=null && ip.isValid())
        {
            ipTraffic.putIfAbsent(ip, createTrafficFlow());
            ipTraffic.get(ip).addPayload(packet);
        }

        // Increases payload of concrete port
        Port port = packet.getSourcePort();
        if(port!=null && port.isValid())
        {
            portTraffic.putIfAbsent(port, createTrafficFlow());
            portTraffic.get(port).addPayload(packet);
        }

        // Increases payload of a process which owns this port
        NetProcess portOwnerProcess = ProcessInfoDB.getInstance().getProcessOfPort(port);
        if(portOwnerProcess != null)
        {
            processTraffic.putIfAbsent(portOwnerProcess, createTrafficFlow());
            processTraffic.get(portOwnerProcess).addPayload(packet);
        }
        /*else
        {
            _undefinedProcessTraffic.putIfAbsent(port, createTrafficFlow());
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
            NetProcess portOwner = ProcessInfoDB.getInstance().getProcessOfPort(port);
            if(portOwner != null) // port owner is found
            {
                processTraffic.putIfAbsent(portOwner, createTrafficFlow());
                processTraffic.get(portOwner).mergeWith(portTraffic);

                undProcess.remove();
            }
            else // port owner still unknown
            {
                // if owner is undefined during long time than throw it away
                if(portTraffic.getIdleTimeSec() >= 10f)
                    undProcess.remove();
            }
        }
    }*/


    /*
    * Just produces the new traffic flow
    * */
    @NotNull
    protected TrafficFlow createTrafficFlow()
    {
        return new TrafficFlow();
    }


    /*
    * Removes each traffic record which exists in @sub
    * And remove all related to it if @removeRelated==true
    * */
    public void removeSubset(TrafficTable sub, boolean removeRelated)
    {
        assert sub!=null;

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
        assert trafficFlow!=null;

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
    * Cleans traffic via some cleaning rule
    * */
    public void clean(TrafficCleaner cleaner)
    {
        assert cleaner!=null;

        Iterator<Map.Entry<IPv4Address, TrafficFlow>> ipItr = ipTraffic.entrySet().iterator();
        while(ipItr.hasNext())
            if(cleaner.isGarbage(ipItr.next().getValue()))
                ipItr.remove();

        Iterator<Map.Entry<Port, TrafficFlow>> portItr = portTraffic.entrySet().iterator();
        while(portItr.hasNext())
            if(cleaner.isGarbage(portItr.next().getValue()))
                portItr.remove();

        Iterator<Map.Entry<NetProcess, TrafficFlow>> processItr = processTraffic.entrySet().iterator();
        while(processItr.hasNext())
            if(cleaner.isGarbage(processItr.next().getValue()))
                processItr.remove();
    }


    /*
    * Select traffic via some selection rule
    * */
    public TrafficTable select(TrafficSelector selector)
    {
        assert selector!=null;

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
        assert alerter!=null;

        Set <NetProcess>  alertedProcess = new HashSet<NetProcess>(processTraffic.size());
        Set <IPv4Address> alertedIp      = new HashSet<IPv4Address>(ipTraffic.size());
        Set <Port>        alertedPort    = new HashSet<Port>(portTraffic.size());

        // OS Processes first - they has the highest alerting priority
        for(Map.Entry<NetProcess, TrafficFlow> entry : processTraffic.entrySet())
        {
            IPv4Address dominantIp = entry.getValue().getDominantDstAddr();
            Port dominantPort = entry.getValue().getDominantSrcPort();

            alertedIp.add(dominantIp);
            alertedPort.add(dominantPort);
            alertedProcess.add(entry.getKey());

            alerter.complainAboutFlow(entry.getValue());
        }

        // Destination IPs next - they has the middle alerting priority
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

        // Ports after everything - they has the lowest alerting priority
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
