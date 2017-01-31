package detector.Alerter;

import detector.ThreatPattern.Threat;
import detector.ThreatPattern.ThreatMessage;

/***************************************************
 * Alerts about big outgoing traffic leaks
 **************************************************/
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
