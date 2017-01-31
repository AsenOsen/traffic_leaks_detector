package detector.NetwPrimitives.TrafficTable.TrafficOperations;

import detector.NetwPrimitives.TrafficFlow.TrafficFlow;

/**
 * Created by SAMSUNG on 31.01.2017.
 */
public interface TrafficExcluder
{

    boolean exclude(TrafficFlow trafficFlow);

}
