package detector.ThreatPattern;

import detector.LogHandler;
import org.json.JSONObject;

import java.util.Calendar;


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
    private String title;
    private String lowLvlMessage;
    private String pattern;
    private long utcTimestamp;


    public ThreatMessage()
    {
        utcTimestamp = Calendar.getInstance().getTime().getTime() / 1000;
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


    public void setShortMessage(String msg)
    {
        this.title = msg;
    }


    public void setLowLevelMessage(String msg)
    {
        this.lowLvlMessage = msg;
    }


    public String produceGuiMessage()
    {
        JSONObject json = new JSONObject();
        json.put("user_message", userMessage);
        json.put("label", title);
        json.put("utc_timestamp", utcTimestamp);

        LogHandler.Log("Gui message: "+json.toString());
        return json.toString();
    }


    /*
    * Debug method
    * */
    public void Dump()
    {
        LogHandler.Log(getDump());
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
