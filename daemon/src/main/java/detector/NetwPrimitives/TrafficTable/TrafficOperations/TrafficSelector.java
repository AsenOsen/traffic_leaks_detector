package detector.NetwPrimitives.TrafficTable.TrafficSelectors;


import detector.NetwPrimitives.TrafficFlow.TrafficFlow;

public interface TrafficSelector {

    boolean select(TrafficFlow trafficFlow);

}
