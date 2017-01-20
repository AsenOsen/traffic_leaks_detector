package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.TrafficFlow.TrafficFlow;

/**
 * Selects the traffic which leaks somewhere continuously during N seconds
 * (typically when someone steals the data in real-time -- micro or camera)
 */
public class StableLeakSelector implements TrafficSelector
{
    private int rateSizeBytes;
    private int minActivitySec;
    private int maxDelaySec;


    public StableLeakSelector(int limitTrafficBytes, int minActivitySec, int maxDelaySec)
    {
        this.rateSizeBytes = limitTrafficBytes;
        this.minActivitySec = minActivitySec;
        this.maxDelaySec = maxDelaySec;
    }


    @Override
    public boolean select(TrafficFlow trafficFlow)
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
                trafficFlow.getBytes() >= rateSizeBytes;

        return  isSuspiciousTrafficSize;
    }


    private boolean isTrafficActual(TrafficFlow trafficFlow)
    {
        boolean lifeTimeAppropriate =
                trafficFlow.getActivityTimeSec() >= minActivitySec;
        boolean wasActiveNotLongAgo =
                trafficFlow.getInactivityTimeSec() < maxDelaySec;

        return lifeTimeAppropriate && wasActiveNotLongAgo;
    }

}
