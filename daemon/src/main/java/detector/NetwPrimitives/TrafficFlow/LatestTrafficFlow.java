package detector.NetwPrimitives.TrafficFlow;


import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Packet;
import detector.NetwPrimitives.Port;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/***************************************************
 * This class contains a single traffic flow
 * which was detected during last N seconds ONLY
 *
 * This is a decorator over TrafficFlow class
 **************************************************/
public class LatestTrafficFlow extends TrafficFlow
{

    private int observingTimeSecCount = 0;
    private Deque<MomentaryTrafficFlow> momentTraffic = null;
    private Map<MomentaryTrafficFlow, MomentaryTrafficFlow> momentSearchTable = null;


    public LatestTrafficFlow(int observingTimeSec)
    {
        super();

        this.observingTimeSecCount = observingTimeSec;
        momentTraffic = new LinkedBlockingDeque<MomentaryTrafficFlow>(observingTimeSec);
        momentSearchTable = new ConcurrentHashMap<MomentaryTrafficFlow, MomentaryTrafficFlow>(observingTimeSec);
    }


    @Override
    public synchronized void addPayload(Packet additional)
    {
        cutObsoleteTraffic();
        super.addPayload(additional);
    }


    @Override
    protected void increaseTraffic(Packet packet)
    {
        super.increaseTraffic(packet);

        IPv4Address dstPacketIp = packet.getDestinationAddress();
        Port srcPacketPort = packet.getSourcePort();
        int payload = packet.getPayloadSize();

        if(observingTimeSecCount > 0)
        {
            MomentaryTrafficFlow moment;
            if (momentTraffic.size() == 0 || !momentTraffic.getLast().isCurrentMoment()) {
                moment = new MomentaryTrafficFlow();
                momentTraffic.offer(moment);
                momentSearchTable.put(moment, moment);
            } else {
                moment = momentTraffic.getLast();
            }
            moment.addPacket(payload);
            moment.addTraffic(dstPacketIp, payload);
            moment.addTraffic(srcPacketPort, payload);
        }
    }


    @Override
    public synchronized void mergeWith(TrafficFlow traffic)
    {
        super.mergeWith(traffic);

        if(traffic instanceof LatestTrafficFlow)
        {
            LatestTrafficFlow timedTraffic = (LatestTrafficFlow)traffic;
            if (observingTimeSecCount > 0)
                for (MomentaryTrafficFlow moment : momentTraffic)
                {
                    if (!moment.isMomentOutdated(observingTimeSecCount))
                        momentSearchTable.get(moment).merge(timedTraffic.momentSearchTable.get(moment));
                }
        }

        cutObsoleteTraffic();
    }


    @Override
    public float getLifeTimeSec()
    {
        return Math.min(super.getLifeTimeSec(), observingTimeSecCount);
    }


    private void cutObsoleteTraffic()
    {
        if(observingTimeSecCount == 0)
            return;

        while(
                momentTraffic.size() > 0 &&
                momentTraffic.peekFirst().isMomentOutdated(observingTimeSecCount)
                )
        {
            MomentaryTrafficFlow obsolete = momentTraffic.pollFirst();
            momentSearchTable.remove(obsolete);

            totalPayload -= obsolete.getTotalPayload();
            totalPackets -= obsolete.getTotalPackets();

            for(IPv4Address ip : ipPayload.keySet())
                ipPayload.put(ip, ipPayload.get(ip) - obsolete.getTrafficForIp(ip));

            for(Port port : portPayload.keySet())
                portPayload.put(port, portPayload.get(port) - obsolete.getTrafficForPort(port));
        }
    }
}
