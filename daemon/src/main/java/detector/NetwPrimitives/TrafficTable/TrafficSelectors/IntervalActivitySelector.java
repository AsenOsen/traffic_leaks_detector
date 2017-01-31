package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.NetwPrimitives.TrafficTable.TrafficOperations.TrafficSelector;

/*********************************************
 * Selects the traffic if its activity time
 * within some interval
 *********************************************/
public class IntervalActivitySelector implements TrafficSelector
{
    private int minActivityTimeSec;
    private int maxActivityTimeSec;

    public IntervalActivitySelector(int minActivityTimeSec, int maxActivityTimeSec)
    {
        assert minActivityTimeSec <= maxActivityTimeSec;

        this.minActivityTimeSec = minActivityTimeSec;
        this.maxActivityTimeSec = maxActivityTimeSec;
    }

    @Override
    public boolean select(TrafficFlow trafficFlow)
    {
        float activityTime = trafficFlow.getActivityTimeSec();
        return  minActivityTimeSec <= activityTime && activityTime <= maxActivityTimeSec;
    }
}
