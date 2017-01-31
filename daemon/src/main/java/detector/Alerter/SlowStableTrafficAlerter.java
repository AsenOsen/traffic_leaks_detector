package detector.Alerter;

import detector.ThreatPattern.Threat;
import detector.ThreatPattern.ThreatMessage;

/*******************************************************
 * Alerts about long-active but slow(passive) traffic flows
 ******************************************************/
public class SlowStableTrafficAlerter extends Alerter
{
    @Override
    protected ThreatMessage getThreatMessage(Threat threat)
    {
        ThreatMessage threatMsg = threat.createReport();
        threatMsg.setType(ThreatMessage.ThreatType.SlowLeakageMessage);
        return threatMsg;
    }

}
