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


    private Analyzer(){  }


    public static Analyzer getInstance()
    {
        return instance;
    }


    public void analyze()
    {
        // clear collectors each 10 and 2 seconds! otherwise it is risky to lose potential leak detection

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


    private void analyzeBigTrafficLeaks()
    {
        passiveTrafficCollector.removeInactive(10f);

        TrafficTable leaks = passiveTrafficCollector.selectSubset(new BigAndFastSelector());
        passiveTrafficCollector.removeSubset(leaks);
       // activeTrafficCollector.removeSubset(leaks);

        leaks.removeSimilarities();
        leaks.raiseComplaints(new BigTrafficLeakAlerter());
    }


    private void analyzeStableLeaks()
    {
        activeTrafficCollector.removeInactive(2f);

        TrafficTable leaks = activeTrafficCollector.selectSubset(new StableLeakageSelector());
        activeTrafficCollector.removeSubset(leaks);
        //passiveTrafficCollector.removeSubset(leaks);

        leaks.removeSimilarities();
        leaks.raiseComplaints(new LeakageAlerter());
    }


    private void analyzeLongLivers()
    {
        TrafficTable longTimeTraffic = passiveTrafficCollector.selectSubset(new LongLiversSelector());
        //activeTrafficCollector.removeSubset(longTimeTraffic);
        passiveTrafficCollector.removeSubset(longTimeTraffic);

        longTimeTraffic.removeSimilarities();
        longTimeTraffic.raiseComplaints(new LongLivingTrafficAlerter());
    }

}
