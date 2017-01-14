package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

public class OverflowsSelector implements TrafficSelector {

    /*
    * Suspiciously active sendings to some IP
    * */
    @Override
    public boolean select(IPv4Address dstIp, TrafficFlow trafficFlow) {

        boolean isSuspiciousTrafficSize =
                trafficFlow.getBytes() >= 1024 * 32;
        boolean isDuringShortTime =
                trafficFlow.getUpTimeSec() <= 60 *1f;

        return isSuspiciousTrafficSize && isDuringShortTime;
    }

    /*
    * Suspiciously active port number (port ~ stable session)
    * */
    @Override
    public boolean select(Port srcPort, TrafficFlow trafficFlow) {

        boolean isSuspiciousTrafficSize =
                trafficFlow.getBytes() >= 1024 * 32;
        boolean isDuringShortTime =
                trafficFlow.getUpTimeSec() <= 60 *1f;

        return isSuspiciousTrafficSize && isDuringShortTime;
    }

    /*
    * Suspiciously active OS process
    * */
    @Override
    public boolean select(NetProcess process, TrafficFlow trafficFlow) {

        boolean isSuspiciousTrafficSize =
                trafficFlow.getBytes() >= 1024 * 32;
        boolean isDuringShortTime =
                trafficFlow.getUpTimeSec() <= 60 *1f;
        boolean isDominantDetected =
                trafficFlow.getDominantDstAddr() != null || trafficFlow.getDominantSrcPort() != null;

        //if(isSuspiciousTrafficSize && isDuringShortTime && !isDominantDetected)
        //    System.out.println(process + " --- "+trafficFlow.isDominantDetected());

        return
                (isSuspiciousTrafficSize && isDuringShortTime) && ( isDominantDetected );
    }
}
