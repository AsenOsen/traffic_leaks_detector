package detector.OsProcessesPrimitives.platforms.routine;


import detector.LogHandler;
import detector.NetwPrimitives.Port;
import detector.OsProcessesPrimitives.NetProcess;
import detector.OsProcessesPrimitives.ProcessTable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinuxProcessTableExtractor
        extends ProcessTableExtractor {


    private ProcessTable linProcessesTable = new ProcessTable();

    private final Pattern netstat_Pattern_A =
            Pattern.compile(":([0-9]{1,5})\\s+.*:([0-9]{1,5}).*\\s+([0-9]{1,5})\\/(.*)$");
    private final Pattern netstat_Pattern_B =
            Pattern.compile(":([0-9]{1,5})\\s+.*:(\\*).*\\s+([0-9]{1,5})\\/(.*)$");


    @Override
    public ProcessTable getActualTable() {

        linProcessesTable.clean();

        fillTableWithNetstatOutput( runNativeCmd("netstat -tunapl") );

        return linProcessesTable;
    }


    private void fillTableWithNetstatOutput(StringBuilder netstat)
    {
        String[] lines = netstat.toString().toLowerCase().split("\\n");
        Matcher matcher;

        for(String line : lines)
        {
            if(line.indexOf("tcp")!=-1 || line.indexOf("udp")!=-1)
            {
                matcher = netstat_Pattern_A.matcher(line);
                boolean matchA = matcher.find();
                boolean matchB = false;
                if(!matchA) {
                    matcher = netstat_Pattern_B.matcher(line);
                    matchB = matcher.find();
                }

                if(matchA || matchB)
                {
                    try
                    {
                        int portNumber = Integer.parseInt(matcher.group(1));
                        int pid = Integer.parseInt(matcher.group(3));
                        String pidName = matcher.group(4).trim();
                        Port port = new Port(portNumber);
                        NetProcess process = new NetProcess(pid).setName(pidName);
                        linProcessesTable.set(port, process);
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
