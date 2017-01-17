package detector.Alerter.Threat;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.IpInfo;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

import java.util.Date;

/**
 *
 */
public class ThreatReport
{
    private NetProcess process;   // process which caused the alert
    private IPv4Address ip;       // ip which caused the alert
    private Port port;            // port which caused the alert
    private TrafficFlow traffic;  // malicious traffic


    public ThreatReport(NetProcess process, TrafficFlow traffic)
    {
        this.process = process;
        this.ip = traffic.getDominantDstAddr();
        this.port = traffic.getDominantSrcPort();
        this.traffic = traffic;
    }


    public ThreatReport(IPv4Address ip, TrafficFlow traffic)
    {
        this.process = traffic.getDominantProcess();
        this.ip = ip;
        this.port = traffic.getDominantSrcPort();
        this.traffic = traffic;
    }


    public ThreatReport(Port port, TrafficFlow traffic)
    {
        this.process = traffic.getDominantProcess();
        this.ip = traffic.getDominantDstAddr();
        this.port = port;
        this.traffic = traffic;
    }


    public String getHumanReadable()
    {
        StringBuilder msg = new StringBuilder();
        msg.append("Date: "+(new Date().toString())+"\n");

        if(process != null)
            msg.append("[process]Процесс отправляет подозрительно много данных\n"+process+" => "+ traffic+"\n");
        else
        if(ip != null)
            msg.append("[ip]На IP уходит подозрительно много данных\n"+ip + " => " + traffic+"\n");
        else
        if(port != null)
            msg.append("[port]Порт отправляет подозрительно много данных\n:"+port + " => " + traffic+"\n");


        IpInfo info = ip.getIpInfo();
        if(info != null)
        {
            String owner = info.getOwner();
            if(owner != null && owner.toLowerCase().indexOf("yandex llc") > -1)
                msg.append("Яндукс метрика следит за тобой, сын мой...\n");
        }


        return  msg.toString();
    }
}
