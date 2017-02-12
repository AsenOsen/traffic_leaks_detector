package detector.ThreatPattern.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.IpInfo;
import detector.NetwPrimitives.Port;
import detector.OsProcessesPrimitives.NetProcess;
import detector.ThreatPattern.PatternStorage.PatternField;
import detector.ThreatPattern.Threat;

import java.util.regex.Pattern;

/**
 * Created by SAMSUNG on 11.02.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public class TrafficPattern
{
    @JsonProperty(PatternField.TrafficField.PID)
    private String pid;
    @JsonProperty(PatternField.TrafficField.DST_IP)
    private String dstIp;
    @JsonProperty(PatternField.TrafficField.SRC_PORT)
    private String srcPort;
    @JsonProperty(PatternField.TrafficField.PROCESS_NAME)
    private String processName;
    @JsonProperty(PatternField.TrafficField.ORG_NAME)
    private String orgName;
    @JsonProperty(PatternField.TrafficField.HOST_NAME)
    private String hostName;
    @JsonProperty(PatternField.TrafficField.DIFFER_BY)
    private String differByFields;


    public boolean matches(Threat threat)
    {
        assert threat!=null;

        NetProcess process = threat.getInitiatorProcess();
        Port port = threat.getInitiatorPort();
        IPv4Address ip = threat.getForeignIp();
        IpInfo info = ip==null ? null : ip.getIpInfo();

        return  matchPid(process) &&
                matchDstIp(ip) &&
                matchSrcPort(port) &&
                matchProcessName(process) &&
                matchOrganization(info) &&
                matchHostname(info);
    }


    public boolean isEmpty()
    {
        return  pid==null &&
                dstIp==null &&
                srcPort==null &&
                processName==null &&
                orgName==null &&
                hostName==null;
    }


    /*
    * Returns a unique pattern.
    * Uniqueness based on the fields pattern should differentiate by
    * */
    public TrafficPattern getUniquePatternByThreat(Threat threat)
    {
        TrafficPattern uniquePattern = new TrafficPattern();

        if(differByFields != null)
        {
            NetProcess process = threat.getInitiatorProcess();
            IPv4Address ip = threat.getForeignIp();
            IpInfo info = ip==null ? null : ip.getIpInfo();

            String ipAddr   = ip.toString();
            String psName   = process==null ? null : (process.getName()==null  ? null : process.getName());
            String orgName  = info==null    ? null : (info.getOrgName()==null  ? null : info.getOrgName());
            String hstName  = info==null    ? null : (info.getHostName()==null ? null : info.getHostName());

            // uniqueness by destination IP
            if (differByFields.indexOf(PatternField.TrafficField.DST_IP) != -1 && ipAddr != null)
                uniquePattern.dstIp = "^" + Pattern.quote(ipAddr) + "$";
            // uniqueness by porcess name
            if (differByFields.indexOf(PatternField.TrafficField.PROCESS_NAME) != -1 && psName != null)
                uniquePattern.processName = "^" + Pattern.quote(psName) + "$";
            // uniqueness by org name
            if (differByFields.indexOf(PatternField.TrafficField.ORG_NAME) != -1 && orgName != null)
                uniquePattern.orgName = "^" + Pattern.quote(orgName) + "$";
            // uniqueness by host name
            if (differByFields.indexOf(PatternField.TrafficField.HOST_NAME) != -1 && hostName != null)
                uniquePattern.hostName = "^" + Pattern.quote(hstName) + "$";
        }

        return uniquePattern;
    }


    private boolean matchProcessName(NetProcess process)
    {
        if(this.processName != null)
        {
            String processName = process == null ? null : process.getName();
            return isStringMatches(this.processName, processName);
        }
        return true;
    }


    private boolean matchPid(NetProcess process)
    {
        if(this.pid != null)
        {
            String pid = process==null ? null : (process.getPid()==null ? null : process.getPid().toString());
            return isStringMatches(this.pid, pid);
        }
        return true;
    }


    private boolean matchDstIp(IPv4Address ip)
    {
        if(this.dstIp != null)
        {
            String ipAddress = ip==null ? null : ip.toString();
            return isStringMatches(this.dstIp, ipAddress);
        }
        return true;
    }


    private boolean matchSrcPort(Port port)
    {
        if(this.srcPort != null)
        {
            String portNum = port==null ? null : port.toString();
            return isStringMatches(this.srcPort, portNum);
        }
        return true;
    }


    private boolean matchOrganization(IpInfo info)
    {
        if(this.orgName != null)
        {
            String orgName = info==null ? null : info.getOrgName();
            return isStringMatches(this.orgName, orgName);
        }
        return true;
    }


    private boolean matchHostname(IpInfo info)
    {
        if(this.hostName != null)
        {
            String host = info==null ? null : info.getHostName();
            return isStringMatches(this.hostName, host);
        }
        return true;
    }


    private boolean isStringMatches(String pattern, String str)
    {
        assert pattern!=null : "Pattern can`t be NULL!";

        if(str == null)
            return false;

        return str.matches("(?i)"+pattern);
        /*Matcher matcher = pattern.matcher(str);
        return matcher.find();*/
    }


    @Override
    public String toString()
    {
        return pid+" | "+ dstIp +" | "+ srcPort +" | "+processName+" | "+ hostName +" | "+ orgName;
    }

}
