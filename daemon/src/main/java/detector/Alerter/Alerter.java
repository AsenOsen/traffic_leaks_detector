package detector.Alerter;

import detector.Alerter.Threat.Threat;
import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

/**
 * Abstract class which describes common logic for all specific alert types.
 * Alerts potential traffic leaks.
 */
public abstract class Alerter
{

    public void complainAboutFlow(TrafficFlow traffic)
    {
        alert(new Threat(traffic));
    }


    /*
    * Reports the user about some potential threat
    * */
    protected abstract void alert(Threat threat);
}
