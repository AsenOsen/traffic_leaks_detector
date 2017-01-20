package detector.Alerter;

import detector.Alerter.Threat.Threat;

/**
 * Alerts about big outgoing traffic leaks
 */
public class BigTrafficLeakAlerter extends Alerter
{
    @Override
    protected void alert(Threat threat)
    {
        System.out.println("------------------------------------ Big Traffic ---");
        System.out.println(threat.createReport());
    }

}
