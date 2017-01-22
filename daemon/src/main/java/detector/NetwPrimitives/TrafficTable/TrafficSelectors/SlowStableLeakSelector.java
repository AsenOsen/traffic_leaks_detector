package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;
import detector.ThreatPattern.DB_KnownPatterns;
import detector.ThreatPattern.Threat;
import detector.ThreatPattern.ThreatPattern;

/**
 * Selects traffic of nonstandard applications which leaks relatively slowly,
 * probably through some intermediate application`s buffer during a long time.
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
        return isNonFadingForSoLong(trafficFlow) &&
                isOverflowed(trafficFlow) &&
                (trafficFlow.getDominantDstAddr() != null || trafficFlow.getDominantSrcPort() != null) &&
                !isTypicalApplication(trafficFlow);
    }


    private boolean isOverflowed(TrafficFlow trafficFlow)
    {
        boolean isSuspiciousTrafficSize =
                trafficFlow.getBytes() >= rateSizeBytes;

        return  isSuspiciousTrafficSize;
    }


    private boolean isNonFadingForSoLong(TrafficFlow trafficFlow)
    {
        boolean lifeTimeAppropriate =
                trafficFlow.getActivityTimeSec() >= minActivitySec;
        boolean wasActiveNotLongAgo =
                trafficFlow.getInactivityTimeSec() < maxDelaySec;

        return lifeTimeAppropriate && wasActiveNotLongAgo;
    }
    
    /*
    * Checks if application is not standard
    * */
    private boolean isTypicalApplication(TrafficFlow trafficFlow)
    {
        Threat threat = new Threat(trafficFlow);
        ThreatPattern knownApps = DB_KnownPatterns.getInstance().getPatternByName("Pattern.StandardApps");

        return
                (knownApps!=null) && (knownApps.matches(threat));
    }
}
