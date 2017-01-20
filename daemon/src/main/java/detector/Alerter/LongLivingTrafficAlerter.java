package detector.Alerter;

import detector.Alerter.Threat.Threat;

/**
 * Alerts about long-active traffic flows
 */
public class LongLivingTrafficAlerter extends Alerter
{
    @Override
    protected void alert(Threat threat)
    {
        System.out.println("------------------------------------ Long-living traffic ---");
        System.out.println(threat.createReport());
    }

}
