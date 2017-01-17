package detector.Alerter;

import detector.Alerter.Threat.ThreatReport;

/**
 * Alerts about long-active traffic flows
 */
public class LongLivingTrafficAlerter extends Alerter
{
    @Override
    protected void alert(ThreatReport threatReport)
    {
        System.out.println("------------------------------------ Long-living traffic ---");
        System.out.println(threatReport.getHumanReadable());
    }

}
