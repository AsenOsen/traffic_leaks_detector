package detector.Analyzer;

import detector.Alerter.BigTrafficLeakAlerter;
import detector.Alerter.LeakageAlerter;
import detector.Alerter.SlowStableTrafficAlerter;
import detector.NetwPrimitives.*;
import detector.NetwPrimitives.TrafficTable.TimedTrafficTable;
import detector.NetwPrimitives.TrafficTable.TrafficSelectors.*;
import detector.NetwPrimitives.TrafficTable.TrafficSelectors.CleaningSelectors.SlowLongLivingSelector;
import detector.NetwPrimitives.TrafficTable.TrafficTable;

/************************************************************************************
 * This is a Singleton class.
 * Class analyzes the data, collected with NetInterceptor`s singleton.
 * Class uses concrete analyzer algorithms from Analyzers package.
 *
 * *********************************************************************************/
public class Analyzer
{

    private static final Analyzer instance = new Analyzer();

    private TrafficTable active2secTrafficCollector = new TrafficTable();
    private TrafficTable last10secCollector = new TimedTrafficTable(10);
    private TrafficTable stable60secCollector = new TrafficTable();

    private BlackListSelector blackListSelector =
            new BlackListSelector();
    // 100kB for last 10 second
    private SuddenLeakSelector suddenLeakSelector =
            new SuddenLeakSelector(100 * 1024, 10);
    // 32 kB during at least last 8 sec with inactivity intervals less than w sec
    private StableLeakSelector stableLeakSelector =
            new StableLeakSelector(32 * 1024, 8, 2);
    // 32 kB during at least last 60 sec with inactivity intervals less than 15 sec (at least 4 actions at last minute)
    private SlowStableLeakSelector slowStableSelector =
            new SlowStableLeakSelector(32 * 1024 , 60, 15);
    // 32 kB still not accumulated during 70 sec of activity
    private SlowLongLivingSelector slowLongLivingSelector =
            new SlowLongLivingSelector(32 * 1024, 60+5);

    // time of the last traffic tables cleaning
    private long lastCleaningTime = System.currentTimeMillis();


    private Analyzer()
    {

    }


    public static Analyzer getInstance()
    {
        return instance;
    }


    public void analyze()
    {
        analyzeAlgorithmStableLeaks();
        analyzeAlgorithmSuddenLeaks();
        analyzeAlgorithmStableSlowLeaks();
    }


    public void registerPacket(Packet netPacket)
    {
        int payloadSize = netPacket.getPayloadSize();

        // Accept packet if it has payload inside
        if (payloadSize > 0)
        {
            active2secTrafficCollector.add(netPacket);
            last10secCollector.add(netPacket);
            stable60secCollector.add(netPacket);
        }
    }


    private void removeDetectedTraffic(TrafficTable detected)
    {
        last10secCollector.removeIrrelevantSubset(detected, true);
        active2secTrafficCollector.removeIrrelevantSubset(detected, true);
        stable60secCollector.removeIrrelevantSubset(detected, true);
    }


    /*
    * This algorithm:
    * 1) tracks all traffic for last N seconds
    * 2) detects if during last N second the bytes limit was overflowed
    * */
    private void analyzeAlgorithmSuddenLeaks()
    {
        last10secCollector.removeInactive(10f);

        TrafficTable leaks = last10secCollector.select(suddenLeakSelector);
        leaks = leaks.select(blackListSelector);
        leaks.raiseComplaints(new BigTrafficLeakAlerter());
        removeDetectedTraffic(leaks);
    }


    /*
    * This algorithm:
    * 1) tracks active traffic
    * 2) detects if limit was overflowed
    * */
    private void analyzeAlgorithmStableLeaks()
    {
        active2secTrafficCollector.removeInactive(2f);

        TrafficTable leaks = active2secTrafficCollector.select(stableLeakSelector);
        leaks = leaks.select(blackListSelector);
        leaks.raiseComplaints(new LeakageAlerter());
        removeDetectedTraffic(leaks);
    }


    /*
    * This algorithm:
    * 1) tracks passive traffic
    * 2) which overflowed the limit
    * */
    private void analyzeAlgorithmStableSlowLeaks()
    {
        stable60secCollector.removeInactive(15f);
        // garbage cleaner runs each minute
        boolean isTimeToClean = (System.currentTimeMillis() - lastCleaningTime) > 60000L;
        if(isTimeToClean)
        {
            // remove garbage from @stable60secCollector
            TrafficTable leaks = stable60secCollector.select(slowLongLivingSelector);
            stable60secCollector.removeIrrelevantSubset(leaks, false);
            lastCleaningTime = System.currentTimeMillis();
        }

        TrafficTable leaks = stable60secCollector.select(slowStableSelector);
        leaks = leaks.select(blackListSelector);
        leaks.raiseComplaints(new SlowStableTrafficAlerter());
        removeDetectedTraffic(leaks);
    }

}
