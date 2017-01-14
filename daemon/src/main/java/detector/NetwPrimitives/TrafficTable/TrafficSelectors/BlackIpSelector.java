package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

/*
* Select only those IP which ARE NOT in white list
* */
public class BlackIpSelector
        implements TrafficSelector {

    @Override
    public boolean select(IPv4Address dstIp, TrafficFlow trafficFlow) {

        /*
        *
        * if (dstIP.inWhiteList())
        *   return false;
        * else
        *   return true;
        *
        * */

        // just an experimental prototyping below...
        if(dstIp.getIpInfo().getOwner().toLowerCase().indexOf("yandex") != -1)
            return false;

        return true;

    }

    @Override
    public boolean select(Port srcPort, TrafficFlow trafficFlow) {
        /*
        *
        * if (trafficFlow.getDominantDstIp().inWhiteList())
        *   return false;
        * else
        *   return true;
        *
        * */

        // just an experimental prototyping below...
        IPv4Address addr = trafficFlow.getDominantDstAddr();
        if(addr != null)
            if(addr.getIpInfo().getOwner().toLowerCase().indexOf("yandex") != -1)
                return false;

        return true;
    }

    @Override
    public boolean select(NetProcess process, TrafficFlow trafficFlow) {
        /*
        *
        * if (trafficFlow.getDominantDstIp().inWhiteList())
        *   return false;
        * else
        *   return true;
        *
        * */

        // just an experimental prototyping below...
        IPv4Address addr = trafficFlow.getDominantDstAddr();
        if(addr != null)
            if(addr.getIpInfo().getOwner().toLowerCase().indexOf("yandex") != -1)
                return false;

        return true;
    }
}
