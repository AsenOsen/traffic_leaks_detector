package detector.Alerter.Threat;

import com.fasterxml.jackson.annotation.*;

import detector.LogHandler;
import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.IpInfo;
import detector.NetwPrimitives.Port;
import detector.OsProcessesPrimitives.NetProcess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThreatPattern implements Comparable<ThreatPattern>
{
    @JacksonInject
    private final String codeName;
    @JsonProperty("priority")
    private final int priority;

    @JsonProperty("pid")
    private String pid;
    @JsonProperty("ip")
    private String ip;
    @JsonProperty("port")
    private String port;
    @JsonProperty("processname")
    private String processName;
    @JsonProperty("organization")
    private String organization;
    @JsonProperty("hostname")
    private String hostname;
    @JsonProperty("dependant-patterns")
    private String dependantPatterns;
    @JsonProperty("msg")
    private String msg;

    private ArrayList<ThreatPattern> dependencies = new ArrayList<ThreatPattern>();


    @JsonCreator
    public ThreatPattern(@JsonProperty("codeName") String name, @JsonProperty("priority") int priorityLevel)
    {
        this.codeName = name;
        this.priority = priorityLevel;
    }


    public boolean matches(Threat threat)
    {
        NetProcess process = threat.getInitiatorProcess();
        Port port = threat.getInitiatorPort();
        IPv4Address ip = threat.getForeignIp();
        IpInfo info = ip==null ? null : ip.getIpInfo();

        return testDependencies(threat) &&
                testPid(process) &&
                testIp(ip) &&
                testPort(port) &&
                testProcessName(process) &&
                testOrganization(info) &&
                testHostname(info);
    }


    public String createMessage(Threat threat)
    {
        if(msg == null)
        {
            LogHandler.Warn("Threat message is null!");
            return null;
        }

        NetProcess process = threat.getInitiatorProcess();
        Port port = threat.getInitiatorPort();
        IPv4Address ip = threat.getForeignIp();
        IpInfo info = ip==null ? null : ip.getIpInfo();
        String leakSize = (int)threat.getLeakSize()+"";

        replaceMsgVar("\\{ip\\}", (ip==null ? null : ip.toString()));
        replaceMsgVar("\\{port\\}", (port==null ? null : port.toString()));
        replaceMsgVar("\\{pid\\}", (process==null ? null : process.getPid()+""));
        replaceMsgVar("\\{processname\\}", (process==null ? null : process.getName()));
        replaceMsgVar("\\{organization\\}", (info==null ? null : info.getOrg()));
        replaceMsgVar("\\{hostname\\}", (info==null ? null : info.getHostname()));
        replaceMsgVar("\\{kbytes\\}", leakSize);

        return msg;
    }


    private void replaceMsgVar(String regex, String replacement)
    {
        if(replacement == null)
            replacement = "(?)";
        msg = msg.replaceAll(regex, replacement);
    }


    private void loadDependencies()
    {
        if(dependantPatterns != null)
        {
            for (Iterator<ThreatPattern> it = DB_KnownPatterns.getInstance().getPatterns(); it.hasNext(); )
            {
                ThreatPattern pattern = it.next();
                if(isStringMatches(dependantPatterns, pattern.codeName))
                    dependencies.add(pattern);
            }
        }
    }


    private boolean testDependencies(Threat threat)
    {
        // First time dependencies list will be empty
        if(dependencies.size()==0 && dependantPatterns!=null)
            loadDependencies();

        for(ThreatPattern dependency : dependencies)
        {
            if(!dependency.matches(threat))
                return false;
        }

        return true;
    }


    private boolean testProcessName(NetProcess process)
    {
        if(this.processName != null)
        {
            String processName = process == null ? null : process.getName();
            return isStringMatches(this.processName, processName);
        }
        return true;
    }


    private boolean testPid(NetProcess process)
    {
        if(this.pid != null)
        {
            String pid = String.valueOf(process == null ? -1 : process.getPid());
            return isStringMatches(this.pid, pid);
        }
        return true;
    }


    private boolean testIp(IPv4Address ip)
    {
        if(this.ip != null)
        {
            String ipAddress = String.valueOf(ip == null ? null : ip.toString());
            return isStringMatches(this.ip, ipAddress);
        }
        return true;
    }


    private boolean testPort(Port port)
    {
        if(this.port != null)
        {
            String portNum = String.valueOf(port == null ? null : port.toString());
            return isStringMatches(this.port, portNum);
        }
        return true;
    }


    private boolean testOrganization(IpInfo info)
    {
        if(this.organization != null)
        {
            String orgName = String.valueOf(info == null ? null : info.getOrg());
            return isStringMatches(this.organization, orgName);
        }
        return true;
    }


    private boolean testHostname(IpInfo info)
    {
        if(this.hostname != null)
        {
            String host = String.valueOf(info == null ? null : info.getHostname());
            return isStringMatches(this.hostname, host);
        }
        return true;
    }


    private boolean isStringMatches(String pattern, String str)
    {
        if(str == null)
            return false;

        return str.matches("(?i)"+pattern);
        /*Matcher matcher = pattern.matcher(str);
        return matcher.find();*/
    }


    @Override
    public int hashCode()
    {
        return codeName.hashCode();
    }


    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof ThreatPattern))
            return false;

        return obj.hashCode() == this.hashCode();
    }


    @Override
    public int compareTo(ThreatPattern o)
    {
        if(o.priority > this.priority)
            return +1;
        if(o.priority < this.priority)
            return -1;

        return 0;
    }


    @Override
    public String toString()
    {
        return priority+" - "+codeName+": "+
                pid+" | "+ip+" | "+port+" | "+processName+" | "+hostname+" | "+organization+" | "+msg;
    }

}
