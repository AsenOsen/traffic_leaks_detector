package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.TrafficFlow.TrafficFlow;

/**
 * Selects traffic which:
 * 1) overflowed bytes limit(usually big ~ 100kB)
 * 2) AND during short time
 * 3) AND is targeted
 *
 * (typically when someone steals files from local computer)
 */
public class SuddenLeakSelector implements TrafficSelector
{
    private int limitSizeBytes;
    private int limitTimeSec;


    public SuddenLeakSelector(int limitTrafficBytes, int allowedTimeSec)
    {
        this.limitSizeBytes = limitTrafficBytes;
        this.limitTimeSec = allowedTimeSec;
    }


    @Override
    public boolean select(TrafficFlow trafficFlow)
    {
        boolean isTargeted  // specific remote IP detected or single port
                = (trafficFlow.getDominantDstAddr() != null || trafficFlow.getDominantSrcPort() != null);

        return
                isTrafficBig(trafficFlow) &&
                isShortTime(trafficFlow) &&
                isTargeted;
    }


    private boolean isTrafficBig(TrafficFlow trafficFlow)
    {
        boolean isExceeded =
                trafficFlow.getBytes() >= limitSizeBytes;

        return isExceeded;
    }


    private boolean isShortTime(TrafficFlow trafficFlow)
    {
        boolean isExceeded =
                trafficFlow.getActivityTimeSec() <= limitTimeSec;

        return isExceeded;
    }

}
