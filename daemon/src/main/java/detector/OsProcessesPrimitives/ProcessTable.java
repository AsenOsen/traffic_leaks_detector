package detector.OsProcessesPrimitives;


import detector.NetwPrimitives.Port;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/************************************************************************
* This class represents a key-value table.
* This is needed for fast search of which process owns concrete port
* **********************************************************************/
public class ProcessTable {


    private ConcurrentMap<Port, NetProcess> processTable =
            new ConcurrentHashMap<Port, NetProcess>(100);


    /*
    * Registers a bond between @port and @process
    * */
    public synchronized void set(Port port, NetProcess process)
    {
        if(port!=null && port.isValid())
            processTable.put(port, process);
    }


    /*
    * Get port`s process owner
    * */
    public NetProcess get(Port port)
    {
        return processTable.get(port);
    }


    public /*synchronized*/ void addInfo(NetProcess process, String processName)
    {
        for(NetProcess p : processTable.values())
            if(p.equals(process))
                p.setName(processName);
    }


    /*
    * Enriches @processTable with @another
    * */
    public /*synchronized*/ void mergeWith(ProcessTable another)
    {
        for (Map.Entry<Port, NetProcess> entry : another.processTable.entrySet())
        {
            processTable.put(entry.getKey(), entry.getValue());
        }
    }


    public /*synchronized*/ void clean()
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
