package detector.Alerter;

import detector.ThreatPattern.Threat;

/**
 * Alerts about constant leakage in foreign servers
 */
public class LeakageAlerter extends Alerter
{
    @Override
    protected void alert(Threat threat)
    {
        System.out.println("------------------------------------ Leak traffic ---");
        System.out.println(threat.createReport());
    }

}
