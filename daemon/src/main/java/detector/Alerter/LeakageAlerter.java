package detector.Alerter;

import detector.ThreatPattern.PatternParser.ThreatMessage;
import detector.ThreatPattern.Threat;

/**
 * Alerts about constant leakage in foreign servers
 */
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
