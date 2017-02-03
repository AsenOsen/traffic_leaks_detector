package detector.Alerter;

import detector.GUIModule;
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
        GUIModule.getInstance().offerMessageForGui(message);
        GUIModule.getInstance().runGui();
        message.Dump();
    }


    /*
    * Reports the user about some potential threat
    * */
    protected abstract ThreatMessage getThreatMessage(Threat threat);
}
