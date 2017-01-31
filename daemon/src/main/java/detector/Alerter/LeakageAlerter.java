package detector.Alerter;

import detector.ThreatPattern.Threat;
import detector.ThreatPattern.ThreatMessage;

/*******************************************************
 * Alerts about constant leakage on foreign servers
 ******************************************************/
public class LeakageAlerter extends Alerter
{
    @Override
    protected ThreatMessage getThreatMessage(Threat threat)
    {
        ThreatMessage threatMsg = threat.createReport();
        threatMsg.setType(ThreatMessage.ThreatType.LeakageMessage);
        return threatMsg;
    }

}
