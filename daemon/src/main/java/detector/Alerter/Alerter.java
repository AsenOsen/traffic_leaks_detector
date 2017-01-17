package detector.Alerter;

import detector.Alerter.Threat.ThreatReport;
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

    public void complainAboutProcess(NetProcess process, TrafficFlow traffic)
    {
        alert(new ThreatReport(process, traffic));
    }


    public void complainAboutIp(IPv4Address ip, TrafficFlow traffic)
    {
        alert(new ThreatReport(ip, traffic));
    }


    public void complainAboutPort(Port port, TrafficFlow traffic)
    {
        alert(new ThreatReport(port, traffic));
    }


    /*
    * Reports the user about some potential threatReport
    * */
    protected abstract void alert(ThreatReport threatReport);
}
