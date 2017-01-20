package detector.Alerter.Threat;

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
public class ThreatPattern implements Comparable<ThreatPattern>
{
    private final String codeName;
    private final int priority;

    private Pattern
        pid,
        ip,
        port,
        processName,
        organization,
        hostname;

    private String msg;

    private Pattern dependantPatterns;
    private ArrayList<ThreatPattern> dependencies = new ArrayList<ThreatPattern>();


    public ThreatPattern(String name, int priorityLevel)
    {
        this.codeName = name;
        this.priority = priorityLevel;
    }


    public boolean test(Threat threat)
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

        float leakSize = threat.getLeakSize();

        return msg.
                replaceAll("\\{ip\\}", (ip==null ? "?" : ip.toString())).
                replaceAll("\\{port\\}", (port==null ? "?" : port.toString())).
                replaceAll("\\{pid\\}", (process==null ? "?" : process.getPid())+"").
                replaceAll("\\{processname\\}", (process==null ? "?" : process.getName())).
                replaceAll("\\{organization\\}", (info==null ? "?" : info.getOrg())).
                replaceAll("\\{hostname\\}", (info==null ? "?" : info.getHostname())).
                replaceAll("\\{kbytes\\}", (int)leakSize+"");
    }


    public void setPid(String pattern)
    {
        pid = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    public void setIp(String pattern)
    {
        ip = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    public void setPort(String pattern)
    {
        port = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    public void setProcessName(String pattern)
    {
        processName = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    public void setOrganization(String pattern)
    {
        organization = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    public void setHostName(String pattern)
    {
        hostname = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    public void setDependentPatterns(String pattern)
    {
        dependantPatterns = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    public void setMessage(String msg)
    {
        this.msg = msg;
    }


    public void loadDependentPatterns()
    {
        if(dependantPatterns != null)
        {
            Iterator<ThreatPattern> patternsItr = DB_KnownPatterns.getInstance().getPatterns();
            while(patternsItr.hasNext())
            {
                ThreatPattern pattern = patternsItr.next();
                if(isMatches(dependantPatterns, pattern.codeName))
                    dependencies.add(pattern);
            }
        }
    }


    private boolean testDependencies(Threat threat)
    {
        for(ThreatPattern dependency : dependencies)
        {
            if(!dependency.test(threat))
                return false;
        }
        return true;
    }


    private boolean testProcessName(NetProcess process)
    {
        if(this.processName != null)
        {
            String processName = process == null ? null : process.getName();
            return isMatches(this.processName, processName);
        }
        return true;
    }


    private boolean testPid(NetProcess process)
    {
        if(this.pid != null)
        {
            String pid = String.valueOf(process == null ? -1 : process.getPid());
            return isMatches(this.pid, pid);
        }
        return true;
    }


    private boolean testIp(IPv4Address ip)
    {
        if(this.ip != null)
        {
            String ipAddress = String.valueOf(ip == null ? null : ip.toString());
            return isMatches(this.ip, ipAddress);
        }
        return true;
    }


    private boolean testPort(Port port)
    {
        if(this.port != null)
        {
            String portNum = String.valueOf(port == null ? null : port.toString());
            return isMatches(this.port, portNum);
        }
        return true;
    }


    private boolean testOrganization(IpInfo info)
    {
        if(this.organization != null)
        {
            String orgName = String.valueOf(info == null ? null : info.getOrg());
            return isMatches(this.organization, orgName);
        }
        return true;
    }


    private boolean testHostname(IpInfo info)
    {
        if(this.hostname != null)
        {
            String host = String.valueOf(info == null ? null : info.getHostname());
            return isMatches(this.hostname, host);
        }
        return true;
    }


    private boolean isMatches(Pattern pattern, String str)
    {
        if(str == null)
            return false;

        Matcher matcher = pattern.matcher(str);
        return matcher.find();
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
