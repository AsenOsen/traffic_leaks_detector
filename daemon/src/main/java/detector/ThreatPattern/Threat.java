package detector.ThreatPattern;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

import java.util.Date;
import java.util.Iterator;

/**
 * Describes a potential leakage threat
 */
public class Threat
{

    private NetProcess process;   // process which caused the alert
    private IPv4Address ip;       // ip which caused the alert
    private Port port;            // port which caused the alert
    private TrafficFlow traffic;  // malicious traffic                 // THIS NEEDED ONLY FOR DEBUGGING


    public Threat(TrafficFlow traffic)
    {
        this.process = traffic.getDominantProcess();
        this.ip = traffic.getDominantDstAddr();
        this.port = traffic.getDominantSrcPort();
        this.traffic = traffic;
    }


    public String createReport()
    {
        Iterator<ThreatPattern> patternsItr = DB_KnownPatterns.getInstance().getPatterns();
        while(patternsItr.hasNext())
        {
            ThreatPattern pattern = patternsItr.next();
            if(pattern.matches(this))
            {
                return pattern.createMessage(this) + "\n" + getDebugMessage();
            }
        }

        return getDebugMessage();
    }


    public NetProcess getInitiatorProcess()
    {
        return process;
    }

    public IPv4Address getForeignIp()
    {
        return ip;
    }

    public Port getInitiatorPort()
    {
        return port;
    }

    public float getLeakSize()
    {
        return traffic.getBytes()/1000f;
    }

    public float getActivityTime()
    {
        return traffic.getActivityTimeSec();
    }


    private String getDebugMessage()
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


        return msg.toString();
    }
}
