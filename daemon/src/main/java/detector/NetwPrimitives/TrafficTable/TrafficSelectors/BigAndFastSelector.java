package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

/**
 * This class describes the next selection rule:
 * "more than 80 kB and in less than 10 seconds"
 */
public class BigAndFastSelector implements TrafficSelector{

    private int allowedTrafficBytes;
    private int allowedTimeRateSec;

    public BigAndFastSelector(int maxTrafficBytes, int timeRateSec)
    {
        this.allowedTrafficBytes = maxTrafficBytes;
        this.allowedTimeRateSec = timeRateSec;
    }


    @Override
    public boolean select(IPv4Address dstIp, TrafficFlow trafficFlow)
    {
        return isTrafficActual(trafficFlow) && isOverflowed(trafficFlow);
    }


    @Override
    public boolean select(Port srcPort, TrafficFlow trafficFlow)
    {
        return isTrafficActual(trafficFlow) && isOverflowed(trafficFlow);
    }


    @Override
    public boolean select(NetProcess process, TrafficFlow trafficFlow)
    {
        boolean isDominantDetected =
                trafficFlow.getDominantDstAddr() != null ||
                        trafficFlow.getDominantSrcPort() != null;

        return isTrafficActual(trafficFlow) &&
                isOverflowed(trafficFlow) &&
                isDominantDetected;
    }


    private boolean isOverflowed(TrafficFlow trafficFlow)
    {
        boolean isSuspiciousTrafficSize =
                trafficFlow.getBytes() >= allowedTrafficBytes;

        return  isSuspiciousTrafficSize;
    }


    private boolean isTrafficActual(TrafficFlow trafficFlow)
    {
        boolean lifeTimeAppropriate =
                trafficFlow.getActivityTimeSec() <= allowedTimeRateSec;
        boolean wasActiveNotLongAgo =
                trafficFlow.getInactivityTimeSec() < allowedTimeRateSec;

        return lifeTimeAppropriate && wasActiveNotLongAgo;
    }

}
