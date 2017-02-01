package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.NetwPrimitives.TrafficTable.TrafficOperations.TrafficSelector;

/************************************************
 * Selects the traffic if its last activity
 * was detected less than limit ago.
 ***********************************************/
public class AliveSelector implements TrafficSelector
{
    private int limitSleepTimeSec;

    public AliveSelector(int limitSleepTimeSec)
    {
        this.limitSleepTimeSec = limitSleepTimeSec;
    }

    @Override
    public boolean select(TrafficFlow trafficFlow)
    {
        return trafficFlow.getIdleTimeSec() < limitSleepTimeSec;
    }
}
