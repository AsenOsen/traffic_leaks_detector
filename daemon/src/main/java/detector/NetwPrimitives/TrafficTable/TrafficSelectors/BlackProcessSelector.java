package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

/**
 * Selects processnames which are not in white-list
 */
public class BlackProcessSelector implements TrafficSelector
{

    @Override
    public boolean select(TrafficFlow trafficFlow) {

        // just an experimental prototyping below...
        if(trafficFlow.getDominantProcess().getName().toLowerCase().indexOf("chrome") != -1)
            return false;

        return true;

    }
}
