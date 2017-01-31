package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.NetwPrimitives.TrafficTable.TrafficOperations.TrafficSelector;

/******************************************************
 * Selects the traffic if it is targeted on something
 *****************************************************/
public class TargetedSelector implements TrafficSelector
{

    @Override
    public boolean select(TrafficFlow trafficFlow)
    {
        boolean isIpTargeted = trafficFlow.getDominantDstAddr() != null;
        boolean isPortTargeted = trafficFlow.getDominantSrcPort() != null;

        return isIpTargeted || isPortTargeted;
    }

}
