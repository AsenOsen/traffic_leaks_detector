package detector.Alerter;

import detector.ThreatPattern.ThreatMessage;
import detector.ThreatPattern.Threat;

/**
 * Alerts about long-active but slow traffic flows
 */
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
