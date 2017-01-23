package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.TrafficFlow.TrafficFlow;

/**
 * Selects traffic which leaked size is too big and activity time small
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
        return !isTimeRateExceeded(trafficFlow) &&
                isTrafficSizeExceeded(trafficFlow) &&
                (trafficFlow.getDominantDstAddr() != null || trafficFlow.getDominantSrcPort() != null);
    }


    private boolean isTrafficSizeExceeded(TrafficFlow trafficFlow)
    {
        boolean isExceeded =
                trafficFlow.getBytes() >= limitSizeBytes;

        return isExceeded;
    }


    private boolean isTimeRateExceeded(TrafficFlow trafficFlow)
    {
        boolean isExceeded =
                trafficFlow.getActivityTimeSec() > limitTimeSec;

        return isExceeded;
    }

}
