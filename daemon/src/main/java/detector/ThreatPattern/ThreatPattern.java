package detector.ThreatPattern;

import com.fasterxml.jackson.annotation.*;

import detector.LogHandler;
import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.IpInfo;
import detector.NetwPrimitives.Port;
import detector.OsProcessesPrimitives.NetProcess;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThreatPattern implements Comparable<ThreatPattern>
{
    @JsonProperty("name")
    private final String codeName;
    @JsonProperty("priority")
    private final int priority;

    @JsonProperty("pid")
    private String pid;
    @JsonProperty("dstip")
    private String dstip;
    @JsonProperty("srcport")
    private String srcport;
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
    @JsonProperty("comment")
    private String comment;

    private Set<ThreatPattern> dependencies = new HashSet<ThreatPattern>();


    @JsonCreator
    public ThreatPattern(@JsonProperty("name") String name, @JsonProperty("priority") int priorityLevel)
    {
        this.codeName = name;
        this.priority = priorityLevel;

        // pattern name naming convention checking
        if(codeName.indexOf("Pattern.") == -1)
            LogHandler.Err(new Exception("Pattern`s name SHOULD starts with 'Pattern.'!"));
    }


    public String getName()
    {
        return codeName;
    }


    public boolean matches(Threat threat)
    {
        NetProcess process = threat.getInitiatorProcess();
        Port port = threat.getInitiatorPort();
        IPv4Address ip = threat.getForeignIp();
        IpInfo info = ip==null ? null : ip.getIpInfo();

        return testDependencies(threat) &&
                testPid(process) &&
                testDstIp(ip) &&
                testSrcPort(port) &&
                testProcessName(process) &&
                testOrganization(info) &&
                testHostname(info);
    }


    public String createMessage(Threat threat)
    {
        if(msg == null)
        {
            LogHandler.Warn("ThreatPattern message is null!");
            return null;
        }

        NetProcess process = threat.getInitiatorProcess();
        Port port = threat.getInitiatorPort();
        IPv4Address ip = threat.getForeignIp();
        IpInfo info = ip==null ? null : ip.getIpInfo();

        String stub = "?";
        String ipAddr = ip==null ? stub : (ip.toString()==null ? stub : ip.toString());
        String portNo = port==null ? stub : (port.toString()==null ? stub : port.toString());
        String psPid  = process==null ? stub : process.getPid()+"";
        String psName = process==null ? stub : (process.getName()==null ? stub : process.getName());
        String orgName= info==null ? stub : (info.getOrg()==null ? stub : info.getOrg());
        String hstName= info==null ? stub : (info.getHostname()==null ? stub : info.getHostname());
        String leakSize = (int)threat.getLeakSize()+"";
        String actTime  = (int)threat.getActivityTime()+"";

        return msg
                .replaceAll("\\{ip\\}", ipAddr)
                .replaceAll("\\{port\\}", portNo)
                .replaceAll("\\{pid\\}", psPid)
                .replaceAll("\\{processname\\}", psName)
                .replaceAll("\\{organization\\}", orgName)
                .replaceAll("\\{hostname\\}", hstName)
                .replaceAll("\\{kbytes\\}", leakSize)
                .replaceAll("\\{timesec\\}", actTime);
    }


    private void loadDependencies()
    {
        if(dependantPatterns != null)
        {
            Iterator<ThreatPattern> it = DB_KnownPatterns.getInstance().getPatterns();
            while (it.hasNext())
            {
                ThreatPattern pattern = it.next();
                if(isStringMatches(dependantPatterns, pattern.codeName))
                    dependencies.add(pattern);
            }

            if(dependencies.size() == 0)
                LogHandler.Warn("No 'pattern' matches found for "+codeName+" pattern!");
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


    private boolean testDstIp(IPv4Address ip)
    {
        if(this.dstip != null)
        {
            String ipAddress = String.valueOf(ip == null ? null : ip.toString());
            return isStringMatches(this.dstip, ipAddress);
        }
        return true;
    }


    private boolean testSrcPort(Port port)
    {
        if(this.srcport != null)
        {
            String portNum = String.valueOf(port == null ? null : port.toString());
            return isStringMatches(this.srcport, portNum);
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
        String dependencies = "";
        for(ThreatPattern th : this.dependencies)
            dependencies += th.codeName;

        return priority+" - "+codeName+": "+
                pid+" | "+ dstip +" | "+ srcport +" | "+processName+" | "+hostname+" | "+organization+
                " | "+msg+" | Dependencies: "+dependencies+ " | Comment: "+comment;
    }

}
