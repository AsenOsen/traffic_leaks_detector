package detector.Alerter;

import detector.ThreatPattern.PatternParser.ThreatMessage;
import detector.ThreatPattern.Threat;

/**
 * Alerts about big outgoing traffic leaks
 */
public class BigTrafficLeakAlerter extends Alerter
{
    @Override
    protected ThreatMessage getThreatMessage(Threat threat)
    {
        ThreatMessage threatMsg = threat.createReport();
        threatMsg.setType(ThreatMessage.ThreatType.BigTrafficMessage);
        return threatMsg;
    }

}
