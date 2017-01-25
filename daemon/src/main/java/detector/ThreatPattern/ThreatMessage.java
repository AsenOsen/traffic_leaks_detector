package detector.ThreatPattern;

import org.json.JSONObject;

/**
 * Describes a high-level userMessage about threat
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
    private String userMessage;
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


    public void setUserMessage(String msg)
    {
        this.userMessage = msg;
    }


    public void setLowLevelMessage(String msg)
    {
        this.lowLvlMessage = msg;
    }


    public String produceGuiMessage()
    {
        JSONObject json = new JSONObject();
        json.put("user_message", userMessage);
        json.put("label", getThreatType());

        System.out.println(json.toString());
        return json.toString();
    }


    /*
    * Debug method
    * */
    public void Dump()
    {
        System.out.println(getDump());
    }


    private String getThreatType()
    {
        switch (type)
        {
            case BigTrafficMessage:
                return "Отправка большого объема данных";
            case LeakageMessage:
                return "Утечка данных";
            case SlowLeakageMessage:
                return "Медленная утечка данных";
            default:
                assert true : "There is cannot be any other threat types!";
                return null;
        }
    }


    private String getDump()
    {
        StringBuilder dump = new StringBuilder();

        dump.append("-------------------------------------- " + getThreatType() + " --- ");
        dump.append("\n"+ userMessage);
        dump.append("\n"+lowLvlMessage);

        return dump.toString();
    }
}
