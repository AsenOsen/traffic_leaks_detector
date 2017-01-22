package detector;

import detector.Alerter.BigTrafficLeakAlerter;
import detector.Alerter.LeakageAlerter;
import detector.Alerter.SlowStableTrafficAlerter;
import detector.NetwPrimitives.*;
import detector.NetwPrimitives.TrafficTable.TimedTrafficTable;
import detector.NetwPrimitives.TrafficTable.TrafficSelectors.*;
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
    // private TrafficTable time05sec;  // network traffic during last 5 seconds
    // private TrafficTable time30sec;  // network traffic during last 30 seconds
    // private TrafficTable time60sec;  // network traffic during last minute
   // private TrafficTable passiveTrafficCollector = new TrafficTable();
    private TrafficTable activeTrafficCollector = new TrafficTable();
    private TrafficTable last10secCollector = new TimedTrafficTable(10);
    private TrafficTable stable60secCollector = new TrafficTable();

    private BlackListSelector blackListSelector =
            new BlackListSelector();
    private SuddenLeakSelector suddenLeakSelector = // 100kB for last 10 second
            new SuddenLeakSelector(100 * 1024, 10);
    private StableLeakSelector stableLeakSelector = // 32 kB during at least last 8 sec with inactivity intervals less than w sec
            new StableLeakSelector(32 * 1024, 8, 2);
    private SlowStableLeakSelector slowStableSelector = // 32 kB during at least last 60 sec with inactivity intervals less than 15 sec
            new SlowStableLeakSelector(32 * 1024 , 60, 15);


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
            activeTrafficCollector.add(netPacket);
            last10secCollector.add(netPacket);
            stable60secCollector.add(netPacket);
        }
    }


    private void cleanTraffic()
    {
        last10secCollector.removeInactive(10f);
        activeTrafficCollector.removeInactive(2f);
        stable60secCollector.removeInactive(15f);
    }


    private void removeDetectedTraffic(TrafficTable detected)
    {
        last10secCollector.removeIrrelevantSubset(detected);
        activeTrafficCollector.removeIrrelevantSubset(detected);
        stable60secCollector.removeIrrelevantSubset(detected);
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
        TrafficTable leaks = activeTrafficCollector.selectSubset(stableLeakSelector);
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
