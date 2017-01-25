package detector.ThreatPattern;

/**
 * Describes a high-level message about threat
 */
public class ThreatMessage
{
    public enum ThreatType
    {
        BigTrafficMessage,
        LeakageMessage,
        SlowLeakageMessage
    }

    private ThreatType type;
    private String message;
    private String lowLvlMessage;
    private String pattern;


    public ThreatMessage()
    {

    }


    public void setType(ThreatType type)
    {
        this.type = type;
    }


    public void setPatternName(String patternCodeName)
    {
        this.pattern = patternCodeName;
    }


    public void setMessage(String msg)
    {
        this.message = msg;
    }


    public void setLowLevelMessage(String msg)
    {
        this.lowLvlMessage = msg;
    }


    public String produceClientMessage()
    {
        return message;
    }

    /*
    * Debug method
    * */
    public void Dump()
    {
        System.out.println(getDump());
    }


    private String getDump()
    {
        StringBuilder dump = new StringBuilder();
        switch (type)
        {
            case BigTrafficMessage:
                dump.append("---------------------------- Big traffic ---");
                break;
            case LeakageMessage:
                dump.append("---------------------------- Default Traffic Leakage ---");
                break;
            case SlowLeakageMessage:
                dump.append("---------------------------- Long-living leakage ---");
                break;
        }

        dump.append("\n"+message);
        dump.append("\n"+lowLvlMessage);

        return dump.toString();
    }
}
