package detector.ThreatPattern.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import detector.AppConfig.AppLocale;
import detector.AppData.KnownPatternsDB;
import detector.LogModule;
import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.IpInfo;
import detector.NetwPrimitives.Port;
import detector.OsProcessesPrimitives.NetProcess;
import detector.ThreatPattern.PatternStorage.PatternField;
import detector.ThreatPattern.Threat;
import detector.ThreatPattern.ThreatMessage;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/********************************************************
 * Describes concrete known traffic pattern.
 *******************************************************/
@JsonIgnoreProperties({PatternField.COMMENT})
public class ThreatPattern implements Comparable<ThreatPattern>
{
    @JsonProperty(PatternField.TRAFFIC_PATTERN)
    protected TrafficPattern traffic;

    @JsonProperty(PatternField.NAME)
    protected String codeName;
    @JsonProperty(PatternField.PRIORITY)
    protected Integer priority;
    @JsonProperty(PatternField.RELATED_PATTERNS)
    protected String relatedPatterns;
    @JsonProperty(PatternField.RELATION_MODE)
    protected String relationMode;
    @JsonProperty(PatternField.MESSAGE)
    protected String msg;
    @JsonProperty(PatternField.EXCITER)
    protected String msgExciter;

    @JsonIgnore
    private Set<ThreatPattern> dependencies = new HashSet<ThreatPattern>();


    public String getName()
    {
        return codeName;
    }


    /*
    * Validates pattern conventions
    * */
    public void validate()
    {
        // only pattern with special code name 'Undefined' can have no rules
        boolean isDummyPattern = codeName.equalsIgnoreCase("Pattern.Undefined");

        // "has code name" convention
        if(codeName==null || codeName.trim().length()==0)
            LogModule.Warn("Pattern`s name SHOULD BE specified!");

        // "prioritized" convention
        if((priority==null || priority==0) && !isDummyPattern)
            LogModule.Warn("Pattern '"+codeName+"' SHOULD have a priority!");

        // "not-empty threat" convention
        if((traffic==null || traffic.isEmpty()) && relatedPatterns==null && !isDummyPattern)
            LogModule.Warn("Pattern '"+codeName+"' SHOULD have at least 1 rule!");

        // "message required" convention
        if(msg==null)
        {
            String localizedName = String.format("%s.%s", codeName, PatternField.MESSAGE);
            if(AppLocale.getInstance().getLocalizedString(localizedName) == null)
                LogModule.Warn("Pattern '"+codeName+"' SHOULD have message!");
        }

        // "message required" convention
        if(msgExciter==null)
        {
            String localizedName = String.format("%s.%s", codeName, PatternField.EXCITER);
            if(AppLocale.getInstance().getLocalizedString(localizedName) == null)
                LogModule.Warn("Pattern '"+codeName+"' SHOULD have exciter message!");
        }
    }


    public boolean matches(Threat threat)
    {
        assert threat!=null;

        return  matchRelations(threat) &&
                (traffic==null ? true : traffic.matches(threat));
    }


    @NotNull
    public ThreatMessage createMessage(Threat threat)
    {
        ThreatMessage threatMessage = new ThreatMessage();
        threatMessage.setMessage(getMessageByTemplate(threat));
        threatMessage.setCallbackFilter(getUniquePatternByThreat(threat));
        threatMessage.setExciter(getExciter());

        return threatMessage;
    }


    /*
    * Generates the unique threat-pattern by concrete threat.
    * */
    private ThreatPattern getUniquePatternByThreat(Threat threat)
    {
        ThreatPattern filter = new ThreatPattern();
        filter.codeName = String.format("Ignore.Custom.%s", codeName);
        filter.relatedPatterns = String.format("^%s$", Pattern.quote(codeName));
        filter.relationMode = "all";

        if(this.traffic != null)
            filter.traffic = traffic.getUniquePatternByThreat(threat);

        return filter;
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
                .replaceAll("\\{"+PatternField.TrafficField.DST_IP+"\\}",       ipAddr)
                .replaceAll("\\{"+PatternField.TrafficField.SRC_PORT+"\\}",     portNo)
                .replaceAll("\\{"+PatternField.TrafficField.PID+"\\}",          psPid)
                .replaceAll("\\{"+PatternField.TrafficField.PROCESS_NAME+"\\}", psName)
                .replaceAll("\\{"+PatternField.TrafficField.ORG_NAME+"\\}",     orgName)
                .replaceAll("\\{"+PatternField.TrafficField.HOST_NAME+"\\}",    hstName)
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


    private boolean isStringMatches(String pattern, String str)
    {
        assert pattern!=null : "Pattern can`t be NULL!";

        if(str == null)
            return false;

        try {
            return str.matches("(?i)" + pattern);
        }catch (PatternSyntaxException e){
            LogModule.Warn("Error in regexp syntax: "+e.getMessage());
            return false;
        }
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
                traffic.toString() + " | " + " | "+msg+" | Dependencies: "+dependencies;
    }

}
