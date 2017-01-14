package detector;

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
public class DB_OsProcessesInfo {

    private static final DB_OsProcessesInfo instance = new DB_OsProcessesInfo();
    private ProcessTable processTable = new ProcessTable();
    private ProcessTableExtractor platformProcessTable = null;


    private DB_OsProcessesInfo()
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


    public static DB_OsProcessesInfo getInstance()
    {
        return instance;
    }


    public void update()
    {
        if(platformProcessTable != null)
        {
            ProcessTable actualProcessTable = platformProcessTable.getActualTable();
            processTable.mergeWith(actualProcessTable);
        }
    }


    public NetProcess getProcessOfPort(Port port)
    {
        return processTable.get(port);
    }

}
