package detector;

import detector.NetwPrimitives.*;
import detector.NetwPrimitives.TrafficTable.TrafficSelectors.BlackIpSelector;
import detector.NetwPrimitives.TrafficTable.TrafficSelectors.BlackProcessSelector;
import detector.NetwPrimitives.TrafficTable.TrafficSelectors.OverflowsSelector;
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
    private TrafficTable traffic = new TrafficTable(); // common traffic heap
    private TrafficTable alertsTable; // suspicious traffic


    private Analyzer(){  }


    public static Analyzer getInstance()
    {
        return instance;
    }


    public void analyze()
    {
        // all traffic flows which has been inactive during last second - remove
        traffic.removeInactive(1f);

        TrafficTable overflows = traffic.selectSubset(new OverflowsSelector());
        //overflows = overflows.selectSubset(new BlackIpSelector());
        //overflows = overflows.selectSubset(new BlackProcessSelector());

        // if something was detected, its traffic flow should be removed
        traffic.removeSubset(overflows);


        alertsTable = overflows;
        alert();
    }


    public void register(Packet netPacket)
    {
        int payloadSize = netPacket.getPayloadSize();

        // Accept packet if has payload
        if(payloadSize > 0)
        {
            traffic.add(netPacket);
        }
    }


    private void alert()
    {
        alertsTable.smartCollapse();
        alertsTable.raiseAlerts();
    }

}
