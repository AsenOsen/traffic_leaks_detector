package detector.OsProcessesPrimitives.platforms.routine;


import detector.LogHandler;
import detector.NetwPrimitives.Port;
import detector.OsProcessesPrimitives.NetProcess;
import detector.OsProcessesPrimitives.ProcessTable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WindowsProcessTableExtractor
        extends ProcessTableExtractor {


    private ProcessTable winProcessesTable = new ProcessTable();

    private final Pattern netstat_PortPattern =
            Pattern.compile(":([0-9]{1,5})\\s+");
    private final Pattern netstat_PidPattern =
            Pattern.compile("\\s+([0-9]{1,5})($|\\s|\\n)");
    private final Pattern tasklist_Pattern =
            Pattern.compile("^(.*\\.exe)\\s{2,}([0-9]{1,5})\\s+", Pattern.CASE_INSENSITIVE);


    @Override
    public ProcessTable getActualTable() {

        winProcessesTable.clean();

        fillTableWithNetstatOutput(runNativeCmd("netstat -a -o -n"));
        fillTableWithTasklistOutput(runNativeCmd("tasklist /nh /fo table"));

        //winProcessesTable.raiseAlerts();

        return winProcessesTable;
    }


    private void fillTableWithTasklistOutput(StringBuilder tasklist)
    {
        String[] lines = tasklist.toString().toLowerCase().split("\\n");
        Matcher matcher;

        for(String line : lines)
        {
            matcher = tasklist_Pattern.matcher(line);
            if(matcher.find())
            {
                try
                {
                    int pid = Integer.parseInt(matcher.group(2));
                    String processName = matcher.group(1);
                    winProcessesTable.addInfo(new NetProcess(pid), processName);
                }
                catch (NumberFormatException e)
                {
                    LogHandler.Warn("Tasklist format error");
                    continue;
                }
            }
        }

        // System processes (not important)
        winProcessesTable.addInfo(new NetProcess(0), "System-Idle-Process");
        winProcessesTable.addInfo(new NetProcess(4), "System");
    }


    private void fillTableWithNetstatOutput(StringBuilder netstat)
    {
        String[] lines = netstat.toString().toLowerCase().split("\\n");
        Matcher portMatcher, pidMatcher;

        for(String line : lines)
        {
            if(line.indexOf("tcp")!=-1 || line.indexOf("udp")!=-1)
            {
                portMatcher = netstat_PortPattern.matcher(line);
                pidMatcher = netstat_PidPattern.matcher(line);
                if(portMatcher.find() && pidMatcher.find())
                {
                    try
                    {
                        int portNumber = Integer.parseInt(portMatcher.group(1));
                        int pid = Integer.parseInt(pidMatcher.group(1));
                        Port port = new Port(portNumber);
                        NetProcess process = new NetProcess(pid);
                        winProcessesTable.set(port, process);
                    }
                    catch (NumberFormatException e)
                    {
                        LogHandler.Warn("Netstat format error");
                        continue;
                    }
                }
            }
        }
    }




}
