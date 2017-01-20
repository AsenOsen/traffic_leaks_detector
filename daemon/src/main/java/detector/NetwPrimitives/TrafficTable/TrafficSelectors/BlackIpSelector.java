package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;

/*
* Select only those IP which ARE NOT in white list
* */
public class BlackIpSelector
        implements TrafficSelector {

    @Override
    public boolean select(TrafficFlow trafficFlow) {
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
            if(addr.getIpInfo().getOrg().toLowerCase().indexOf("yandex") != -1)
                return false;

        return true;
    }
}
