package detector.OsProcessesPrimitives;


import detector.NetwPrimitives.Port;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/************************************************************************
* This class represents a key-value table.
* This is needed for fast search of which process owns concrete port
* **********************************************************************/
public class ProcessTable
{

    private ConcurrentMap<Port, NetProcess> processTable =
            new ConcurrentHashMap<Port, NetProcess>(100);
    private ConcurrentMap<Port, Long> timeTable =
            new ConcurrentHashMap<Port, Long>(100);


    /*
    * Registers a bond between @port and @process
    * */
    public void set(Port port, NetProcess process)
    {
        if(port!=null && port.isValid())
        {
            processTable.put(port, process);
            timeTable.put(port, System.currentTimeMillis());
        }
    }


    /*
    * Get port`s process owner
    * */
    public NetProcess get(Port port)
    {
        return processTable.get(port);
    }


    public void addInfo(NetProcess process, String processName)
    {
        for(NetProcess p : processTable.values())
            if(p.equals(process))
                p.setName(processName);
    }


    /*
    * Enriches @processTable with @another
    * */
    public void mergeWith(ProcessTable another)
    {
        // merge 2 tables into single
        for (Map.Entry<Port, NetProcess> entry : another.processTable.entrySet())
            set(entry.getKey(), entry.getValue());
    }


    /*
    * Removes killed processes and closed ports from data-table
    * */
    public void clearObsoleteData(float obsoleteTimeSec)
    {
        // remove all obsolete processes` data from table
        Iterator<Map.Entry<Port, Long>> timeTableItr = timeTable.entrySet().iterator();
        Long now = System.currentTimeMillis();
        while(timeTableItr.hasNext())
        {
            Map.Entry<Port, Long> entry = timeTableItr.next();
            Port port = entry.getKey();
            float inactivityTimeSec = (now - entry.getValue()) / 1000f;

            // if info about the port hasn`t poped up during last 10 seconds
            if(inactivityTimeSec > obsoleteTimeSec)
            {
                processTable.remove(port);
                timeTable.remove(port);
            }
        }
    }


    public void clean()
    {
        processTable.clear();
    }


    /*
    * Debugging method
    * */
    public void Print()
    {
        for (Map.Entry<Port, NetProcess> entry : processTable.entrySet())
        {
            NetProcess proc = entry.getValue();
            if(proc == null)
                System.out.println(entry.getKey().toString()+":UNDEFINED" );
            else
                //if(proc.getName()!=null && proc.getName().toLowerCase().indexOf("filezilla") != -1)
                    System.out.println(entry.getKey().toString()+" = "+proc);
        }
    }

}
