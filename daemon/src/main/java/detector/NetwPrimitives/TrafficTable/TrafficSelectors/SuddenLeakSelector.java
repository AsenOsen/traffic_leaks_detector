package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.TrafficFlow.TrafficFlow;

/**
 * Selects traffic which leaked size is too big and activity time small
 * (typically when someone steals files from local computer)
 */
public class SuddenLeakSelector implements TrafficSelector
{
    private int rateSizeBytes;
    private int rateTimeSec;


    public SuddenLeakSelector(int limitTrafficBytes, int allowedTimeSec)
    {
        this.rateSizeBytes = limitTrafficBytes;
        this.rateTimeSec = allowedTimeSec;
    }


    @Override
    public boolean select(TrafficFlow trafficFlow)
    {
        boolean isDominantDetected =
                trafficFlow.getDominantDstAddr() != null ||
                        trafficFlow.getDominantSrcPort() != null;

        return !isTimeRateExceeded(trafficFlow) &&
                isTrafficSizeExceeded(trafficFlow) &&
                isDominantDetected;
    }


    private boolean isTrafficSizeExceeded(TrafficFlow trafficFlow)
    {
        boolean isExceeded =
                trafficFlow.getBytes() >= rateSizeBytes;

        return isExceeded;
    }


    private boolean isTimeRateExceeded(TrafficFlow trafficFlow)
    {
        boolean isExceeded =
                trafficFlow.getActivityTimeSec() > rateTimeSec;

        return isExceeded;
    }

}
