package detector.Alerter;

import detector.Alerter.Threat.ThreatReport;

/**
 * Alerts about big outgoing traffic leaks
 */
public class BigTrafficLeakAlerter extends Alerter
{
    @Override
    protected void alert(ThreatReport threatReport)
    {
        System.out.println("------------------------------------ Big Traffic ---");
        System.out.println(threatReport.getHumanReadable());
    }

}
