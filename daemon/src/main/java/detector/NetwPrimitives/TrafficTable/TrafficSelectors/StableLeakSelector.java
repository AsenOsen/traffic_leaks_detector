package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.TrafficFlow.TrafficFlow;

/**
 * Selects the traffic which:
 * 1) leaks continuously to somewhere during N seconds
 * 2) AND overflowed bytes limit
 * 3) AND is targeted
 *
 * (typically when someone steals the data in real-time mode - micro or camera)
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
        boolean isTargeted  // specific remote IP detected or single port
                = (trafficFlow.getDominantDstAddr()!=null || trafficFlow.getDominantSrcPort()!=null);

        return
                isNonFading(trafficFlow) &&
                isOverflowed(trafficFlow) &&
                isTargeted;
    }


    private boolean isOverflowed(TrafficFlow trafficFlow)
    {
        boolean isSuspiciousTrafficSize =
                trafficFlow.getBytes() >= rateSizeBytes;

        return  isSuspiciousTrafficSize;
    }


    private boolean isNonFading(TrafficFlow trafficFlow)
    {
        boolean lifeTimeAppropriate =
                trafficFlow.getActivityTimeSec() >= minActivitySec;
        boolean wasActiveNotLongAgo =
                trafficFlow.getInactivityTimeSec() < maxDelaySec;

        return lifeTimeAppropriate && wasActiveNotLongAgo;
    }

}
