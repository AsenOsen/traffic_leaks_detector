package detector;

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
        cleanTraffic();
        analyzeStableLeaks();
        analyzeSuddenLeaks();
        analyzeStableSlowLeaks();
    }


    public void register(Packet netPacket)
    {
        int payloadSize = netPacket.getPayloadSize();

        // Accept packet if has payload
        if (payloadSize > 0)
        {
            active2secTrafficCollector.add(netPacket);
            last10secCollector.add(netPacket);
            stable60secCollector.add(netPacket);
        }
    }


    private void cleanTraffic()
    {
        last10secCollector.removeInactive(10f);
        active2secTrafficCollector.removeInactive(2f);
        stable60secCollector.removeInactive(15f);

        // garbage cleaner runs each minute
        boolean isTimeToClean = (System.currentTimeMillis() - lastCleaningTime) > 60000L;
        if(isTimeToClean)
        {
            runGarbageCleaners();
            lastCleaningTime = System.currentTimeMillis();
        }
    }


    private void runGarbageCleaners()
    {
        // remove garbage from @last10secCollector
        TrafficTable leaks = last10secCollector.selectSubset(slowLongLivingSelector);
        last10secCollector.removeIrrelevantSubset(leaks, false);
        // remove garbage from @stable60secCollector
        leaks = stable60secCollector.selectSubset(slowLongLivingSelector);
        stable60secCollector.removeIrrelevantSubset(leaks, false);
        // @active2secTrafficCollector contains no garbage for default
        // ...
    }


    private void removeDetectedTraffic(TrafficTable detected)
    {
        last10secCollector.removeIrrelevantSubset(detected, true);
        active2secTrafficCollector.removeIrrelevantSubset(detected, true);
        stable60secCollector.removeIrrelevantSubset(detected, true);
    }


    private void analyzeSuddenLeaks()
    {
        TrafficTable leaks = last10secCollector.selectSubset(suddenLeakSelector);
        leaks = leaks.selectSubset(blackListSelector);
        leaks.raiseComplaints(new BigTrafficLeakAlerter());
        removeDetectedTraffic(leaks);
    }



    private void analyzeStableLeaks()
    {
        TrafficTable leaks = active2secTrafficCollector.selectSubset(stableLeakSelector);
        leaks = leaks.selectSubset(blackListSelector);
        leaks.raiseComplaints(new LeakageAlerter());
        removeDetectedTraffic(leaks);
    }


    private void analyzeStableSlowLeaks()
    {
        TrafficTable leaks = stable60secCollector.selectSubset(slowStableSelector);
        leaks = leaks.selectSubset(blackListSelector);
        leaks.raiseComplaints(new SlowStableTrafficAlerter());
        removeDetectedTraffic(leaks);
    }

}
