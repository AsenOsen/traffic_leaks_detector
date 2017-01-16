package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

/**
 * This class describes the next selection rule:
 * "more than 80 kB and in less than 10 seconds"
 */
public class OverflowSelector implements TrafficSelector{

    private int allowedTrafficBytes;

    public OverflowSelector(int maxTrafficBytes)
    {
        this.allowedTrafficBytes = maxTrafficBytes;
    }


    @Override
    public boolean select(IPv4Address dstIp, TrafficFlow trafficFlow)
    {
        return isOverflowed(trafficFlow);
    }


    @Override
    public boolean select(Port srcPort, TrafficFlow trafficFlow)
    {
        return isOverflowed(trafficFlow);
    }


    @Override
    public boolean select(NetProcess process, TrafficFlow trafficFlow)
    {
        boolean isDominantDetected =
                trafficFlow.getDominantDstAddr() != null ||
                        trafficFlow.getDominantSrcPort() != null;

        return isOverflowed(trafficFlow) &&
                isDominantDetected;
    }


    private boolean isOverflowed(TrafficFlow trafficFlow)
    {
        boolean isSuspiciousTrafficSize =
                trafficFlow.getBytes() >= allowedTrafficBytes;

        return  isSuspiciousTrafficSize;
    }

}
