package detector.ThreatPattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import detector.AppConfig.AppLocale;
import detector.Data.KnownPatternsDB;
import detector.LogModule;
import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.IpInfo;
import detector.NetwPrimitives.Port;
import detector.OsProcessesPrimitives.NetProcess;
import detector.ThreatPattern.PatternParser.PatternField;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;


/********************************************************
 * Describes concrete known traffic pattern.
 * Patterns store in JSON format
 *******************************************************/
@JsonIgnoreProperties({PatternField.COMMENT})
public class ThreatPattern implements Comparable<ThreatPattern>
{
    @JsonProperty(PatternField.NAME)
    private final String codeName;
    @JsonProperty(PatternField.PRIORITY)
    private final int priority;

    @JsonProperty(PatternField.PID)
    private String pid;
    @JsonProperty(PatternField.DST_IP)
    private String dstIp;
    @JsonProperty(PatternField.SRC_PORT)
    private String srcPort;
    @JsonProperty(PatternField.PROCESS_NAME)
    private String processName;
    @JsonProperty(PatternField.ORG_NAME)
    private String orgName;
    @JsonProperty(PatternField.HOST_NAME)
    private String hostName;
    @JsonProperty(PatternField.RELATED_PATTERNS)
    private String relatedPatterns;
    @JsonProperty(PatternField.RELATION_MODE)
    private String relationMode;
    @JsonProperty(PatternField.MESSAGE)
    private String msg;
    @JsonProperty(PatternField.EXCITER)
    private String msgExciter;

    private Set<ThreatPattern> dependencies = new HashSet<ThreatPattern>();


    @JsonCreator
    public ThreatPattern(
            @JsonProperty(PatternField.NAME) String name,
            @JsonProperty(PatternField.PRIORITY) int priorityLevel)
    {
        this.codeName = name==null ? "<unnamed_filter>" : name;
        this.priority = priorityLevel;
    }


    /*
    * Validates pattern conventions
    * */
    public void validate()
    {
        // only pattern with special code name 'Undefined' can have no rules
        boolean isAllowedEmptyPattern = !codeName.equalsIgnoreCase("Pattern.Undefined");

        // "at least 1 rule" convention
        if(pid==null && dstIp==null && srcPort==null &&
                processName==null && orgName==null && hostName==null &&
                relatedPatterns==null && isAllowedEmptyPattern
                )
            LogModule.Err(new Exception("Pattern '"+codeName+"' SHOULD have at least 1 rule!"));
    }


    public String getName()
    {
        return codeName;
    }


    public boolean matches(Threat threat)
    {
        assert threat!=null;

        NetProcess process = threat.getInitiatorProcess();
        Port port = threat.getInitiatorPort();
        IPv4Address ip = threat.getForeignIp();
        IpInfo info = ip==null ? null : ip.getIpInfo();

        return matchRelations(threat) &&
                matchPid(process) &&
                matchDstIp(ip) &&
                matchSrcPort(port) &&
                matchProcessName(process) &&
                matchOrganization(info) &&
                matchHostname(info);
    }


    @NotNull
    public ThreatMessage createMessage(Threat threat)
    {
        ThreatMessage threatMessage = new ThreatMessage();
        threatMessage.setMessage(getMessageByTemplate(threat));
        threatMessage.setPatternName(codeName);
        //threatMessage.setCallbackFilter(getCallbackFilter());
        threatMessage.setExciter(getExciter());

        return threatMessage;
    }


    private String getExciter()
    {
        // if 'exciter' was not specified exactly, try to find it in current locale
        if(msgExciter == null)
        {
            String localizedName = String.format("%s.%s", codeName, PatternField.EXCITER);
            msgExciter = AppLocale.getInstance().getLocalizedString(localizedName);
        }

        return msgExciter;
    }


    private String getMessageByTemplate(Threat threat)
    {
        // if 'message' was not specified exactly, try to find it in current locale
        if(msg == null)
        {
            String localizedName = String.format("%s.%s", codeName, PatternField.MESSAGE);
            msg = AppLocale.getInstance().getLocalizedString(localizedName);
        }

        // ...if it still not specified, then it is not exists
        if(msg == null)
        {
            LogModule.Warn("Message SHOULD BE specified for "+codeName+" pattern.");
            return null;
        }

        NetProcess process = threat.getInitiatorProcess();
        Port port = threat.getInitiatorPort();
        IPv4Address ip = threat.getForeignIp();
        IpInfo info = ip==null ? null : ip.getIpInfo();

        String stub = "[-]";
        String ipAddr   = ip==null ?      stub : (ip.toString()==null      ? stub : ip.toString());
        String portNo   = port==null ?    stub : (port.toString()==null    ? stub : port.toString());
        String psPid    = process==null ? stub : (process.getPid()==null   ? stub : process.getPid()+"");
        String psName   = process==null ? stub : (process.getName()==null  ? stub : process.getName());
        String orgName  = info==null ?    stub : (info.getOrgName()==null  ? stub : info.getOrgName());
        String hstName  = info==null ?    stub : (info.getHostName()==null ? stub : info.getHostName());
        String geoPos   = info==null ?    stub : (info.getGeoInfo());
        String leakSize = getPrettyDataSize(threat.getLeakSizeBytes());
        String actTime  = String.format("%.1f", threat.getActivityTime());

        return msg
                .replaceAll("\\{"+PatternField.DST_IP+"\\}",       ipAddr)
                .replaceAll("\\{"+PatternField.SRC_PORT+"\\}",     portNo)
                .replaceAll("\\{"+PatternField.PID+"\\}",          psPid)
                .replaceAll("\\{"+PatternField.PROCESS_NAME+"\\}", psName)
                .replaceAll("\\{"+PatternField.ORG_NAME+"\\}",     orgName)
                .replaceAll("\\{"+PatternField.HOST_NAME+"\\}",    hstName)
                .replaceAll("\\{geo\\}",                           geoPos)
                .replaceAll("\\{leaksize\\}",                      leakSize)
                .replaceAll("\\{timesec\\}",                       actTime);
    }


    private String getPrettyDataSize(int bytes)
    {
        if(bytes < 1024)
            return String.format("%d B", bytes);
        if(1024 <= bytes && bytes < 1024*1024)
            return String.format("%.1f KB", bytes/1024f);

        return String.format("%.1f MB", bytes/(1024f*1024f));
    }


    private void loadDependencies()
    {
        if(relatedPatterns != null)
        {
            for(String patternName : KnownPatternsDB.getInstance().getNames())
            {
                if (isStringMatches(relatedPatterns, patternName)) {
                    ThreatPattern dependency = KnownPatternsDB.getInstance().getPatternByName(patternName);
                    dependencies.add(dependency);
                }
            }

            //for(ThreatPattern dep : dependencies)
            //    System.out.println(dep);

            if(dependencies.size() == 0)
                LogModule.Warn("No related patterns found for "+codeName+" pattern!");
        }
    }


    private boolean matchRelations(Threat threat)
    {
        // Lazy dependencies loading
        if(dependencies.size()==0 && relatedPatterns!=null)
            loadDependencies();

        // At least one of related pattern matches -- OR
        if(relationMode!=null && relationMode.equalsIgnoreCase("any"))
        {
            for(ThreatPattern dependency : dependencies)
            {
                if(dependency.matches(threat))
                    return true;
            }

            return false;
        }
        // All patterns match without exceptions -- AND
        else
        {
            for(ThreatPattern dependency : dependencies)
            {
                if(!dependency.matches(threat))
                    return false;
            }
            return true;
        }
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
    public int compareTo(@NotNull ThreatPattern o)
    {
        if(o == null)
            return -1;

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
                pid+" | "+ dstIp +" | "+ srcPort +" | "+processName+" | "+ hostName +" | "+ orgName +
                " | "+msg+" | Dependencies: "+dependencies;
    }

}
