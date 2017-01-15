package detector.Alerter;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

/**
 * Abstract class which describes common logic for all specific alert types.
 * Alerts potential traffic leaks.
 */
public abstract class Alerter {

    /*
    * Detected PROCESS has THE TOP priority - so informative
    * */
    public void complainAboutProcess(NetProcess process, TrafficFlow traffic)
    {
        raiseProcessAlert(process, traffic);
    }


    /*
    * Detected IP has THE MIDDLE priority - can be false-positive
    * */
    public void complainAboutIp(IPv4Address ip, TrafficFlow traffic)
    {
        NetProcess procDominant = traffic.getDominantProcess();

        if(procDominant != null)
            complainAboutProcess(procDominant, traffic);
        else
            raiseIpAlert(ip, traffic);
    }


    /*
    * Detected PORT has THE LOWEST priority - uninformative, can be false-positive
    * */
    public void complainAboutPort(Port port, TrafficFlow traffic)
    {
        NetProcess processDominant = traffic.getDominantProcess();
        IPv4Address ipDominant = traffic.getDominantDstAddr();

        if(processDominant != null)
            complainAboutProcess(processDominant, traffic);
        else
        if(ipDominant != null)
            complainAboutIp(ipDominant, traffic);
        else
            raisePortAlert(port, traffic);

    }


    /*
    * Alerts suspicious process threat
    * */
    protected abstract void raiseProcessAlert(NetProcess process, TrafficFlow traffic);

    /*
    * Alerts suspicious port threat
    * */
    protected abstract void raisePortAlert(Port port, TrafficFlow traffic);

    /*
    * Alerts suspicious ip threat
    * */
    protected abstract void raiseIpAlert(IPv4Address ip, TrafficFlow traffic);
}
