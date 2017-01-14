package detector;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

/**
 * Alerts potential traffic leaks
 */
public class Alerter {


    /*
    * Detected PROCESS has THE TOP priority - so informative
    * */
    public static void complainAboutProcess(NetProcess process, TrafficFlow traffic)
    {
        raiseProcessAlert(process, traffic);
    }


    /*
    * Detected IP has THE MIDDLE priority - can be false-positive
    * */
    public static void complainAboutIp(IPv4Address ip, TrafficFlow traffic)
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
    public static void complainAboutPort(Port port, TrafficFlow traffic)
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
    private static void raiseProcessAlert(NetProcess process, TrafficFlow traffic)
    {
        System.out.println("------------------------------------");
        System.out.println("[process]Процесс отправляет подозрительно много данных\n"+process+" => "+ traffic);
    }


    /*
    * Alerts suspicious port threat
    * */
    private static void raisePortAlert(Port port, TrafficFlow traffic)
    {
        System.out.println("------------------------------------");
        System.out.println("[port]Порт отправляет подозрительно много данных\n:"+port + " => " + traffic);
    }


    /*
    * Alerts suspicious ip threat
    * */
    private static void raiseIpAlert(IPv4Address ip, TrafficFlow traffic)
    {
        System.out.println("------------------------------------");
        System.out.println("[ip]На IP уходит подозрительно много данных\n"+ip + " => " + traffic);
    }
}
