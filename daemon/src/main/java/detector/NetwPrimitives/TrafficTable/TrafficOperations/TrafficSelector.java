package detector.NetwPrimitives.TrafficTable.TrafficOperations;


import detector.NetwPrimitives.TrafficFlow.TrafficFlow;

/***************************************
* Describes traffic selection rule
* **************************************/
public interface TrafficSelector
{

    boolean select(TrafficFlow trafficFlow);

}
