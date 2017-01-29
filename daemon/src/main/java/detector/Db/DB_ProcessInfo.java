package detector.Db;

import detector.NetwPrimitives.Port;
import detector.OsProcessesPrimitives.NetProcess;
import detector.OsProcessesPrimitives.ProcessTable;
import detector.OsProcessesPrimitives.platforms.routine.ProcessTableExtractor;
import detector.OsProcessesPrimitives.platforms.routine.LinuxProcessTableExtractor;
import detector.OsProcessesPrimitives.platforms.routine.WindowsProcessTableExtractor;

/*****************************************************************************
* This is a singleton.
* Its instance suppose to accomplish in different thread.
* The aim pf class: keep actual info about each process`s open ports
* ****************************************************************************/
public class DB_ProcessInfo
{

    private static final DB_ProcessInfo instance = new DB_ProcessInfo();
    private ProcessTable processTable = new ProcessTable();
    private ProcessTableExtractor platformProcessTable = null;


    public static DB_ProcessInfo getInstance()
    {
        return instance;
    }


    public void update()
    {
        if(platformProcessTable != null)
        {
            ProcessTable actualProcessTable = platformProcessTable.getActualTable();
            processTable.mergeWith(actualProcessTable);
            processTable.clearObsoleteData(10);
        }
    }


    public NetProcess getProcessOfPort(Port port)
    {
        return processTable.get(port);
    }


    private DB_ProcessInfo()
    {
        String osName = System.getProperty("os.name");
        if(osName != null)
        {
            boolean isWindows =
                    osName.toLowerCase().indexOf("windows") != -1;
            boolean isLinux =
                    osName.toLowerCase().indexOf("linux") != -1;

            if(isLinux)
                platformProcessTable = new LinuxProcessTableExtractor();
            else
            if(isWindows)
                platformProcessTable = new WindowsProcessTableExtractor();
        }
    }

}
