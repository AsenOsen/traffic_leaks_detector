package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.ThreatPattern.DB_KnownPatterns;
import detector.ThreatPattern.Threat;
import detector.ThreatPattern.ThreatPattern;

/**
 * Selects traffic which:
 * 1) continuously(~ during 1 minute) leaks, but relatively slowly (~ 4 actions per minute)
 * 2) AND overflowed bytes limit
 * 3) AND belongs to non-standard applications
 * 4) AND is targeted
 *
 * (more probably through intermediate application`s buffer-collector)
 */
public class SlowStableLeakSelector implements TrafficSelector
{
    private int rateSizeBytes;
    private int minActivitySec;
    private int maxDelaySec;


    public SlowStableLeakSelector(int limitTrafficBytes, int minActivitySec, int maxDelaySec)
    {
        this.rateSizeBytes = limitTrafficBytes;
        this.minActivitySec = minActivitySec;
        this.maxDelaySec = maxDelaySec;
    }


    @Override
    public boolean select(TrafficFlow trafficFlow)
    {
        boolean isTargeted  // specific remote IP detected or single port
                = (trafficFlow.getDominantDstAddr() != null || trafficFlow.getDominantSrcPort() != null);

        return
                isNonFading(trafficFlow) &&
                isOverflowed(trafficFlow) &&
                !isStdApp(trafficFlow) &&
                isTargeted;
    }


    private boolean isOverflowed(TrafficFlow trafficFlow)
    {
        boolean isOverflowed =
                trafficFlow.getBytes() >= rateSizeBytes;

        return  isOverflowed;
    }


    private boolean isNonFading(TrafficFlow trafficFlow)
    {
        float activityTime = trafficFlow.getActivityTimeSec();

        boolean isLongLiver =
                minActivitySec <= activityTime && activityTime <= minActivitySec + 10;
        boolean isContinuouslyActive =
                trafficFlow.getInactivityTimeSec() < maxDelaySec;

        return isLongLiver && isContinuouslyActive;
    }
    
    /*
    * Checks if application is harmless
    * */
    private boolean isStdApp(TrafficFlow trafficFlow)
    {
        Threat threat = new Threat(trafficFlow);
        ThreatPattern knownApps = DB_KnownPatterns.getInstance().getPatternByName("Pattern.StdApps");

        return
                (knownApps!=null) && (knownApps.matches(threat));
    }
}
