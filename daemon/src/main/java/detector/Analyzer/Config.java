package detector.Analyzer;

/*************************************************************
 * Contains the traffic analysis configuration
 *************************************************************/
public class Config
{
    private static Config ourInstance = new Config();

    // 100 kB per 10 seconds - allowed, but no more per 10 second
    public final int OBSERVING_ALLOWED_LEAK_BYTES          = 100 * 1024;
    public final int OBSERVING_TRAFFIC_TIME_SEC            = 10;

    public final int MINIMAL_ALLOWED_LEAK_BYTES            = 32 * 1024;

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

    }

}
