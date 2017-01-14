package detector.NetwPrimitives.TrafficTable.TrafficSelectors;


import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

public interface TrafficSelector {

    boolean select(IPv4Address dstIp, TrafficFlow trafficFlow);
    boolean select(Port srcPort, TrafficFlow trafficFlow);
    boolean select(NetProcess process, TrafficFlow trafficFlow);

}
