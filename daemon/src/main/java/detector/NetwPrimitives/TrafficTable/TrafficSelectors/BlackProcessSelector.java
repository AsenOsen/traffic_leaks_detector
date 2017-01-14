package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

/**
 * Selects processnames which are not in white-list
 */
public class BlackProcessSelector implements TrafficSelector
{

    @Override
    public boolean select(IPv4Address dstIp, TrafficFlow trafficFlow) {

        // just an experimental prototyping below...
        NetProcess dominant = trafficFlow.getDominantProcess();
        if(dominant != null)
            if(dominant.getName().toLowerCase().indexOf("chrome") != -1)
                return false;

        return true;

    }

    @Override
    public boolean select(Port srcPort, TrafficFlow trafficFlow) {

        // just an experimental prototyping below...
        NetProcess dominant = trafficFlow.getDominantProcess();
        if(dominant != null)
            if(dominant.getName().toLowerCase().indexOf("chrome") != -1)
                return false;

        return true;

    }

    @Override
    public boolean select(NetProcess process, TrafficFlow trafficFlow) {

        // just an experimental prototyping below...
        if(process.getName().toLowerCase().indexOf("chrome") != -1)
            return false;

        return true;

    }
}
