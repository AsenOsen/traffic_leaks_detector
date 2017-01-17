package detector.Alerter;

import detector.Alerter.Threat.ThreatReport;

/**
 * Alerts about constant leakage in foreign servers
 */
public class LeakageAlerter extends Alerter
{
    @Override
    protected void alert(ThreatReport threatReport)
    {
        System.out.println("------------------------------------ Leak traffic ---");
        System.out.println(threatReport.getHumanReadable());
    }

}
