package detector.Alerter;

import detector.ThreatPattern.Threat;

/**
 * Alerts about long-active traffic flows
 */
public class SlowStableTrafficAlerter extends Alerter
{
    @Override
    protected void alert(Threat threat)
    {
        System.out.println("------------------------------------ Long-living stable leakage ---");
        System.out.println(threat.createReport());
    }

}
