package detector.NetwPrimitives.TrafficTable;

import detector.NetwPrimitives.TrafficFlow.LatestTrafficFlow;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import org.jetbrains.annotations.NotNull;

/*******************************************************
 * Traffic table which stores the traffic during
 * last N seconds ONLY
 *******************************************************/
public class LatestTrafficTable extends TrafficTable
{
    private int watchTimeSec;

    public LatestTrafficTable(int watchTimeSec)
    {
        this.watchTimeSec = watchTimeSec;
    }

    @Override
    @NotNull
    protected TrafficFlow createTrafficFlow()
    {
        return new LatestTrafficFlow(watchTimeSec);
    }
}
