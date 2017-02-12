package detector.ThreatPattern;

import detector.AppConfig.AppLocale;
import detector.LogModule;
import detector.ThreatPattern.Pattern.ThreatPattern;
import detector.UserFiltersManager;
import org.json.JSONObject;

import java.util.Calendar;


/***************************************************
 * Describes a high-level user message about threat
 **************************************************/
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
    private String exciter;
    private String lowLvlMessage;
    private String callbackFilter;
    private long utcTimestamp;


    public ThreatMessage()
    {
        utcTimestamp = Calendar.getInstance().getTime().getTime() / 1000;
    }


    public void setType(ThreatType type)
    {
        this.type = type;
    }


    public void setMessage(String msg)
    {
        this.userMessage = msg;
    }


    public void setExciter(String msg)
    {
        this.exciter = msg;
    }


    public void setCallbackFilter(ThreatPattern filter)
    {
        this.callbackFilter = UserFiltersManager.getInstance().toStorable(filter);
    }


    public void setLowLevelMessage(String msg)
    {
        this.lowLvlMessage = msg;
    }


    public String produceGuiMessage()
    {
        JSONObject json = new JSONObject();
        json.put("message_full", userMessage);
        json.put("message_exciter", exciter);
        json.put("message_type", getThreatType());
        json.put("callback_filter", callbackFilter);
        json.put("utc_timestamp", utcTimestamp);

        LogModule.Log("Gui message: "+json.toString());
        return json.toString();
    }


    /*
    * Debug method
    * */
    public void Dump()
    {
        LogModule.Log(getDump());
    }


    private String getThreatType()
    {
        switch (type)
        {
            case BigTrafficMessage:
                return AppLocale.getInstance().getLocalizedString("ThreatMessage.BigLeak");
            case LeakageMessage:
                return AppLocale.getInstance().getLocalizedString("ThreatMessage.ActiveLeak");
            case SlowLeakageMessage:
                return AppLocale.getInstance().getLocalizedString("ThreatMessage.PassiveLeak");
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
