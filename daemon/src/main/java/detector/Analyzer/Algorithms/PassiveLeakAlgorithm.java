package detector.Analyzer.Algorithms;

import detector.Analyzer.Config;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.NetwPrimitives.TrafficTable.TrafficOperations.TrafficSelector;
import detector.NetwPrimitives.TrafficTable.TrafficSelectors.AliveSelector;
import detector.NetwPrimitives.TrafficTable.TrafficSelectors.IntervalActivitySelector;
import detector.NetwPrimitives.TrafficTable.TrafficSelectors.OverflowSelector;
import detector.NetwPrimitives.TrafficTable.TrafficSelectors.TargetedSelector;

/****************************************************
 * This algorithm searches for passive continuous leaks
 ****************************************************/
public class PassiveLeakAlgorithm extends Algorithm
{
    private final TrafficSelector overflowed;
    private final TrafficSelector targeted;
    private final TrafficSelector overactive;
    private final TrafficSelector alive;
    private final int garbageTimeSec;


    public PassiveLeakAlgorithm()
    {
        garbageTimeSec =
                Config.getInstance().PASSIVE_LEAKAGE_DETECTION_TIME_SEC + 10;
        overflowed = new OverflowSelector(
                Config.getInstance().MINIMAL_ALLOWED_LEAK_BYTES
        );
        overactive = new IntervalActivitySelector(
                Config.getInstance().PASSIVE_LEAKAGE_DETECTION_TIME_SEC,
                Integer.MAX_VALUE
        );
        alive = new AliveSelector(
                Config.getInstance().PASSIVE_LEAKAGE_ALLOWED_IDLE_TIME_SEC
        );
        targeted =
                new TargetedSelector();
    }


    @Override
    public boolean select(TrafficFlow trafficFlow)
    {
        return  overflowed.select(trafficFlow) &&
                overactive.select(trafficFlow) &&
                alive.select(trafficFlow) &&
                targeted.select(trafficFlow);
    }


    @Override
    public boolean isGarbage(TrafficFlow trafficFlow)
    {
        return  trafficFlow.getInactivityTimeSec() > Config.getInstance().PASSIVE_LEAKAGE_ALLOWED_IDLE_TIME_SEC ||
                trafficFlow.getActivityTimeSec() > garbageTimeSec;
    }

}
