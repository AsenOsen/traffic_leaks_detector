package detector.Alerter;

import detector.GUIWrapper;
import detector.ThreatPattern.PatternParser.ThreatMessage;
import detector.ThreatPattern.Threat;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;

/**
 * Alerts potential traffic leak.
 * Abstract class which describes common logic for all specific alert types.
 */
public abstract class Alerter
{

    public void complainAboutFlow(TrafficFlow traffic)
    {
        Threat threat = new Threat(traffic);
        ThreatMessage threatMsg = getThreatMessage(threat);
        alert(threatMsg);
    }


    private void alert(ThreatMessage message)
    {
        GUIWrapper.getInstance().runGui();
        message.Dump();
    }


    /*
    * Reports the user about some potential threat
    * */
    protected abstract ThreatMessage getThreatMessage(Threat threat);
}
