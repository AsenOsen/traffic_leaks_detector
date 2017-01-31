package detector.Analyzer.Algorithms;

import detector.Analyzer.Config;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.NetwPrimitives.TrafficTable.TrafficOperations.TrafficSelector;
import detector.NetwPrimitives.TrafficTable.TrafficSelectors.IntervalActivitySelector;
import detector.NetwPrimitives.TrafficTable.TrafficSelectors.OverflowSelector;
import detector.NetwPrimitives.TrafficTable.TrafficSelectors.TargetedSelector;

/****************************************************
 * This algorithm searches for sudden and big leaks
 ****************************************************/
public class SuddenLeakAlgorithm extends Algorithm
{
    private final TrafficSelector overflowed;
    private final TrafficSelector underactive;
    private final TrafficSelector targeted;


    public SuddenLeakAlgorithm()
    {
        overflowed = new OverflowSelector(
                Config.getInstance().OBSERVING_ALLOWED_LEAK_BYTES
        );
        targeted =
                new TargetedSelector();
        underactive = new IntervalActivitySelector(
                0,
                Config.getInstance().OBSERVING_TRAFFIC_TIME_SEC
        );
    }


    @Override
    public boolean select(TrafficFlow trafficFlow)
    {
        return  overflowed.select(trafficFlow) &&
                underactive.select(trafficFlow) &&
                targeted.select(trafficFlow);
    }


    @Override
    public boolean isGarbage(TrafficFlow trafficFlow)
    {
        return trafficFlow.getInactivityTimeSec() > Config.getInstance().OBSERVING_TRAFFIC_TIME_SEC;
    }

}
