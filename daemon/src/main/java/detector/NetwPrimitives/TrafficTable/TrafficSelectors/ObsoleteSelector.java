package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

/**
 * Selects the traffic which was firstly detected a lot time ago
 */
public class ObsoleteSelector implements TrafficSelector
{
    private int obsoleteTimeSec;

    public ObsoleteSelector(int obsoleteTimeSec)
    {
        this.obsoleteTimeSec = obsoleteTimeSec;
    }

    @Override
    public boolean select(IPv4Address dstIp, TrafficFlow trafficFlow)
    {
        return isObsolete(trafficFlow);
    }

    @Override
    public boolean select(Port srcPort, TrafficFlow trafficFlow)
    {
        return isObsolete(trafficFlow);
    }

    @Override
    public boolean select(NetProcess process, TrafficFlow trafficFlow)
    {
        return isObsolete(trafficFlow);
    }

    private boolean isObsolete(TrafficFlow trafficFlow)
    {
        float existenceTimeSec =
                trafficFlow.getActivityTimeSec() + trafficFlow.getInactivityTimeSec();

        return existenceTimeSec > obsoleteTimeSec;
    }

}
