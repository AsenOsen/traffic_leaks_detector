package detector.Analyzer;

import detector.Alerter.BigTrafficLeakAlerter;
import detector.Alerter.LeakageAlerter;
import detector.Alerter.SlowStableTrafficAlerter;
import detector.Analyzer.Algorithms.ActiveLeakAlgorithm;
import detector.Analyzer.Algorithms.Algorithm;
import detector.Analyzer.Algorithms.PassiveLeakAlgorithm;
import detector.Analyzer.Algorithms.SuddenLeakAlgorithm;
import detector.NetwPrimitives.Packet;
import detector.NetwPrimitives.TrafficTable.LatestTrafficTable;
import detector.NetwPrimitives.TrafficTable.TrafficOperations.TrafficSelector;
import detector.NetwPrimitives.TrafficTable.TrafficSelectors.UserFilterSelector;
import detector.NetwPrimitives.TrafficTable.TrafficTable;

/************************************************************************
 * This is a Singleton class.
 *
 * Class analyzes the data, collected within traffic tables.
 * Class uses a bunch of detection algorithm do determine potential leak.
 *
 * *********************************************************************/
public class Analyzer
{

    private static final Analyzer instance = new Analyzer();

    // common space of collectors which are depends on each other -
    // if something is detected in one of them, others removes these detections
    private TrafficTable activeCollector = new TrafficTable();
    private TrafficTable passiveCollector = new TrafficTable();
    private TrafficTable latestCollector = new LatestTrafficTable(Config.getInstance().OBSERVING_TRAFFIC_TIME_SEC);

    // algorithms of detection:
    private Algorithm activeCollectorAlgorithm = new ActiveLeakAlgorithm();
    private Algorithm passiveCollectorAlgorithm = new PassiveLeakAlgorithm();
    private Algorithm suddenLeakAlgorithm = new SuddenLeakAlgorithm();

    // common selector for each collector
    private TrafficSelector blackListSelector = new UserFilterSelector();


    private Analyzer()
    {

    }


    public static Analyzer getInstance()
    {
        return instance;
    }


    public void registerPacket(Packet netPacket)
    {
        int payloadSize = netPacket.getPayloadSize();

        // Accept packet if it has payload inside
        if (payloadSize > 0)
        {
            activeCollector.add(netPacket);
            latestCollector.add(netPacket);
            passiveCollector.add(netPacket);
        }
    }


    public void analyze()
    {
        analyzeActiveLeaks();
        analyzeSuddenLeaks();
        analyzePassiveLeaks();
    }


    private void removeDetectedTraffic(TrafficTable detected)
    {
        latestCollector.removeSubset(detected, true);
        activeCollector.removeSubset(detected, true);
        passiveCollector.removeSubset(detected, true);
    }


    private void analyzeSuddenLeaks()
    {
        latestCollector.clean(suddenLeakAlgorithm);

        TrafficTable leaks = latestCollector
                .select(suddenLeakAlgorithm)
                .select(blackListSelector);

        leaks.raiseComplaints(new BigTrafficLeakAlerter());
        removeDetectedTraffic(leaks);
    }


    private void analyzeActiveLeaks()
    {
        activeCollector.clean(activeCollectorAlgorithm);

        TrafficTable leaks =
                activeCollector
                .select(activeCollectorAlgorithm)
                .select(blackListSelector);

        leaks.raiseComplaints(new LeakageAlerter());
        removeDetectedTraffic(leaks);
    }


    private void analyzePassiveLeaks()
    {
        passiveCollector.clean(passiveCollectorAlgorithm);

        TrafficTable leaks =
                passiveCollector
                .select(passiveCollectorAlgorithm)
                .select(blackListSelector);

        leaks.raiseComplaints(new SlowStableTrafficAlerter());
        removeDetectedTraffic(leaks);
    }

}
