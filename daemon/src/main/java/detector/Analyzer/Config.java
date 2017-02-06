package detector.Analyzer;

import detector.LogModule;

/*************************************************************
 * Contains the traffic analysis configuration
 *************************************************************/
public class Config
{
    private static Config ourInstance = new Config();

    // 100 kB per 10 seconds - allowed, but no more per 10 second
    public final int OBSERVING_ALLOWED_LEAK_BYTES          ;
    public final int OBSERVING_TRAFFIC_TIME_SEC            = 10;

    public final int MINIMAL_ALLOWED_LEAK_BYTES            ;

    public final int ACTIVE_LEAKAGE_ALLOWED_IDLE_TIME_SEC  = 2;
    public final int ACTIVE_LEAKAGE_DETECTION_TIME_SEC     = 8;

    public final int PASSIVE_LEAKAGE_ALLOWED_IDLE_TIME_SEC = 15;
    public final int PASSIVE_LEAKAGE_DETECTION_TIME_SEC    = 60;


    public static Config getInstance()
    {
        return ourInstance;
    }


    private Config()
    {
        OBSERVING_ALLOWED_LEAK_BYTES = getMaxTraffic10Value();
        MINIMAL_ALLOWED_LEAK_BYTES = getMinTrafficLeakValue();

        LogModule.Log("SET: Allowed bytes per 10 seconds = " +OBSERVING_ALLOWED_LEAK_BYTES);
        LogModule.Log("SET: Minimal leak size bytes = " +MINIMAL_ALLOWED_LEAK_BYTES);
    }


    private int getMaxTraffic10Value()
    {
        int defaultValue = 100 * 1024;

        try
        {
            String maxTraffic10SecStr = System.getProperty("daemon.config.max-traffic-during-10-sec", defaultValue+"");
            return Integer.parseInt(maxTraffic10SecStr);
        }
        catch (NumberFormatException e)
        {
            LogModule.Warn("argument 'max-traffic-during-10-sec' MUST BE a digit!");
            return defaultValue;
        }
    }


    private int getMinTrafficLeakValue()
    {
        int defaultValue = 32 * 1024;

        try
        {
            String maxTraffic10SecStr = System.getProperty("daemon.config.min-leak-size", defaultValue+"");
            return Integer.parseInt(maxTraffic10SecStr);
        }
        catch (NumberFormatException e)
        {
            LogModule.Warn("argument 'min-leak-size' MUST BE a digit!");
            return defaultValue;
        }
    }

}
