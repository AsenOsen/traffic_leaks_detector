package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

/**
 * This class describes the next selection rule:
 * "older than 1 minute"
 */
public class LongLiversSelector implements TrafficSelector
{
    private int allowedTrafficBytes;
    private int minActivityTimeSec;

    public LongLiversSelector(int minTrafficBytes, int minActivityTimeSec)
    {
        this.allowedTrafficBytes = minTrafficBytes;
        this.minActivityTimeSec = minActivityTimeSec;
    }


    @Override
    public boolean select(IPv4Address dstIp, TrafficFlow trafficFlow)
    {
        return isTrafficSuspicious(trafficFlow);
    }


    @Override
    public boolean select(Port srcPort, TrafficFlow trafficFlow)
    {
        return isTrafficSuspicious(trafficFlow);
    }


    @Override
    public boolean select(NetProcess process, TrafficFlow trafficFlow)
    {
        return isTrafficSuspicious(trafficFlow);
    }


    private boolean isTrafficSuspicious(TrafficFlow trafficFlow)
    {
        boolean isUpTimeTooLong =
                trafficFlow.getActivityTimeSec() >= minActivityTimeSec;
        boolean isTrafficSuspicious =
                trafficFlow.getBytes() >= allowedTrafficBytes;

        return isUpTimeTooLong && isTrafficSuspicious;
    }
}
