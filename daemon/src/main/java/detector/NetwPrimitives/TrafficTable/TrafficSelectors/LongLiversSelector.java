package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

/**
 * This class describes the next selection rule:
 * "older than 1 minute"
 */
public class LongLiversSelector implements TrafficSelector {

    @Override
    public boolean select(IPv4Address dstIp, TrafficFlow trafficFlow)
    {
        return isUpTimeToLong(trafficFlow);
    }


    @Override
    public boolean select(Port srcPort, TrafficFlow trafficFlow)
    {
        return isUpTimeToLong(trafficFlow);
    }


    @Override
    public boolean select(NetProcess process, TrafficFlow trafficFlow)
    {
        return isUpTimeToLong(trafficFlow);
    }


    private boolean isUpTimeToLong(TrafficFlow trafficFlow)
    {
        // if uptime is greater than 1 minute
        return trafficFlow.getUpTimeSec() >= 60f;
    }
}
