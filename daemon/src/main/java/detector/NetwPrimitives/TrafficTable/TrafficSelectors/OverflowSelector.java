package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.NetwPrimitives.TrafficTable.TrafficOperations.TrafficSelector;

/************************************************************
 * Selects traffic if its bandwidth overflowed some limit
 ***********************************************************/
public class OverflowSelector implements TrafficSelector
{
    private int overflowSizeBytes;


    public OverflowSelector(int overflowSizeBytes)
    {
        this.overflowSizeBytes = overflowSizeBytes;
    }


    @Override
    public boolean select(TrafficFlow trafficFlow)
    {
        return trafficFlow.getBytes() > overflowSizeBytes;
    }

}
