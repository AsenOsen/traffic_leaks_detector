package detector.NetwPrimitives.TrafficTable;

import detector.NetwPrimitives.TrafficFlow.TimedTrafficFlow;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;

/**
 * Traffic table which stores the traffic during last N seconds ONLY
 */
public class TimedTrafficTable extends TrafficTable
{
    private int watchTimeSec;

    public TimedTrafficTable(int watchTimeSec)
    {
        this.watchTimeSec = watchTimeSec;
    }

    @Override
    protected TrafficFlow createNewFlow()
    {
        return new TimedTrafficFlow(watchTimeSec);
    }
}
