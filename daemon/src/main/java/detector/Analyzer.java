package detector;

import detector.Alerter.BigTrafficLeakAlerter;
import detector.Alerter.LeakageAlerter;
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

    private SuddenLeakSelector suddenLeakSelector = // 100kB for last 10 second
            new SuddenLeakSelector(100 * 1024, 10);
    private StableLeakSelector stableLeakSelector = // 32 kB during at least last 8 sec with inactivity intervals less than 1 sec
            new StableLeakSelector(32 * 1024, 8, 1);
    //private LongLiversSelector longLiversSelector = // 50 kB of non-fading during 60 sec traffic
    //        new LongLiversSelector(50 * 1024 , 60);


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
        //analyzeLongLivers();
    }


    public void register(Packet netPacket)
    {
        int payloadSize = netPacket.getPayloadSize();

        // Accept packet if has payload
        if (payloadSize > 0)
        {
            activeTrafficCollector.add(netPacket);
            last10secCollector.add(netPacket);
        }
    }


    private void cleanTraffic()
    {
        last10secCollector.removeInactive(10f);
        activeTrafficCollector.removeInactive(1f);
    }


    private void removeDetectedTraffic(TrafficTable detected)
    {
        last10secCollector.removeIrrelevantSubset(detected);
        activeTrafficCollector.removeIrrelevantSubset(detected);
    }


    private void analyzeSuddenLeaks()
    {
        TrafficTable leaks = last10secCollector.selectSubset(suddenLeakSelector);
        leaks.raiseComplaints(new BigTrafficLeakAlerter());
        removeDetectedTraffic(leaks);
    }



    private void analyzeStableLeaks()
    {
        TrafficTable leaks = activeTrafficCollector.selectSubset(stableLeakSelector);
        leaks.raiseComplaints(new LeakageAlerter());
        removeDetectedTraffic(leaks);
    }


    /*private void analyzeLongLivers()
    {
        TrafficTable leaks = passiveTrafficCollector.selectSubset(longLiversSelector);
        leaks.raiseComplaints(new LongLivingTrafficAlerter());
        removeDetectedTraffic(leaks);
    }*/

}
