package detector;

import detector.Alerter.BigTrafficLeakAlerter;
import detector.Alerter.LeakageAlerter;
import detector.Alerter.LongLivingTrafficAlerter;
import detector.NetwPrimitives.*;
import detector.NetwPrimitives.TrafficTable.TrafficSelectors.*;
import detector.NetwPrimitives.TrafficTable.TrafficTable;

/************************************************************************************
 * This is a Singleton class.
 * Class analyzes the data, collected with NetInterceptor`s singleton.
 * Class uses concrete analyzer algorithms from Analyzers package.
 *
 * *********************************************************************************/
public class Analyzer{


    private static final Analyzer instance = new Analyzer();
    // private TrafficTable time05sec;  // network traffic during last 5 seconds
    // private TrafficTable time30sec;  // network traffic during last 30 seconds
    // private TrafficTable time60sec;  // network traffic during last minute
    private TrafficTable passiveTrafficCollector = new TrafficTable();
    private TrafficTable activeTrafficCollector = new TrafficTable();

    private BigAndFastSelector bigLeakSelector = // 100kB per 10sec
            new BigAndFastSelector(100 * 1024, 10);
    private StableLeakageSelector stableLeakSelector = // 32 kB during at least 8 sec with inactivity intervals less than 2 sec
            new StableLeakageSelector(32 * 1024, 8, 2);
    //private LongLiversSelector longLiversSelector = // 50 kB of non-fading during 60 sec traffic
    //        new LongLiversSelector(50 * 1024 , 60);
    private ObsoleteSelector obsoleteSelector = // exists longer than 10 sec
            new ObsoleteSelector(10);


    private Analyzer(){  }


    public static Analyzer getInstance()
    {
        return instance;
    }


    public void analyze()
    {
        cleanTraffic();

        analyzeStableLeaks();
        analyzeBigTrafficLeaks();
        //analyzeLongLivers();
    }


    public void register(Packet netPacket)
    {
        int payloadSize = netPacket.getPayloadSize();

        // Accept packet if has payload
        if (payloadSize > 0)
        {
            passiveTrafficCollector.add(netPacket);
            activeTrafficCollector.add(netPacket);
        }
    }


    private void cleanTraffic()
    {
        passiveTrafficCollector.removeInactive(10f);
        activeTrafficCollector.removeInactive(2f);

        TrafficTable obsolete = passiveTrafficCollector.selectSubset(obsoleteSelector);
        cleanDetectedTraffic(obsolete);
    }


    private void cleanDetectedTraffic(TrafficTable detected)
    {
        passiveTrafficCollector.removeSubset(detected);
        activeTrafficCollector.removeSubset(detected);
    }


    private void analyzeBigTrafficLeaks()
    {
        TrafficTable leaks = passiveTrafficCollector.selectSubset(bigLeakSelector);
        leaks.raiseComplaints(new BigTrafficLeakAlerter());
        cleanDetectedTraffic(leaks);
    }



    private void analyzeStableLeaks()
    {
        TrafficTable leaks = activeTrafficCollector.selectSubset(stableLeakSelector);
        leaks.raiseComplaints(new LeakageAlerter());
        cleanDetectedTraffic(leaks);
    }


    /*private void analyzeLongLivers()
    {
        TrafficTable leaks = passiveTrafficCollector.selectSubset(longLiversSelector);
        leaks.raiseComplaints(new LongLivingTrafficAlerter());
        cleanDetectedTraffic(leaks);
    }*/

}
