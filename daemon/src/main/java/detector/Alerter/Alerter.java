package detector.Alerter;

import detector.GUIWrapper;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.ThreatPattern.Threat;
import detector.ThreatPattern.ThreatMessage;

/**********************************************************
 * Alerts potential traffic leak.
 *
 * Abstract class which describes common logic
 * for all algorithm-specific alert types.
 *********************************************************/
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
        GUIWrapper.getInstance().offerMessageForGui(message);
        GUIWrapper.getInstance().runGui();
        message.Dump();
    }


    /*
    * Reports the user about some potential threat
    * */
    protected abstract ThreatMessage getThreatMessage(Threat threat);
}
