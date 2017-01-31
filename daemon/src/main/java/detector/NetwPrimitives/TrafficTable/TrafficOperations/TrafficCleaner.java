package detector.NetwPrimitives.TrafficTable.TrafficOperations;

import detector.NetwPrimitives.TrafficFlow.TrafficFlow;

/********************************************
 * Describes traffic obsolescence rule
 *******************************************/
public interface TrafficCleaner
{

    boolean isGarbage(TrafficFlow trafficFlow);

}
