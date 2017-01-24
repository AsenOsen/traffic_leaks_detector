package detector.ThreatPattern.PatternParser;

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


    /*
    * Debug method
    * */
    public void Dump()
    {
        switch (type)
        {
            case BigTrafficMessage:
                System.out.println("---------------------------- Big traffic ---");
                break;
            case LeakageMessage:
                System.out.println("---------------------------- Default Traffic Leakage ---");
                break;
            case SlowLeakageMessage:
                System.out.println("---------------------------- Long-living leakage ---");
                break;
        }

        System.out.println(message);
        System.out.println(lowLvlMessage);
    }

}
