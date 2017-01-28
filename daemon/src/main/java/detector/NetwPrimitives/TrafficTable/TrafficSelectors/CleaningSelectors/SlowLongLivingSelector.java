package detector.NetwPrimitives.TrafficTable.TrafficSelectors.CleaningSelectors;

import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.NetwPrimitives.TrafficTable.TrafficSelectors.TrafficSelector;

/**
 * Selects the traffic which:
 * 1) exists too long
 * 2) AND not active enough
 */
public class SlowLongLivingSelector implements TrafficSelector {

    private int limitLifeTimeSec;
    private int limitTrafficSizeBytes;


    public SlowLongLivingSelector(int allowedTransferBytes, int limitLifeTimeSec)
    {
        this.limitLifeTimeSec = limitLifeTimeSec;
        this.limitTrafficSizeBytes = allowedTransferBytes;
    }


    @Override
    public boolean select(TrafficFlow trafficFlow)
    {
        boolean isLongLiver =
                trafficFlow.getActivityTimeSec() >= limitLifeTimeSec;
        boolean notActiveEnough =
                trafficFlow.getBytes() < limitTrafficSizeBytes;

        return isLongLiver && notActiveEnough;
    }
}
