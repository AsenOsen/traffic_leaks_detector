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

    private NetProcess process;   // process which caused the getThreatMessage
    private IPv4Address ip;       // ip which caused the getThreatMessage
    private Port port;            // port which caused the getThreatMessage
    private TrafficFlow traffic;  // malicious traffic


    public Threat(TrafficFlow traffic)
    {
        this.process = traffic.getDominantProcess();
        this.ip = traffic.getDominantDstAddr();
        this.port = traffic.getDominantSrcPort();
        this.traffic = traffic;
    }


    public ThreatMessage createReport()
    {
        ThreatPattern pattern = DB_KnownPatterns.getInstance().findMatchingPattern(this);
        if(pattern != null)
        {
            ThreatMessage msg = pattern.createMessage(this);
            msg.setLowLevelMessage(this.toString());
            return msg;
        }

        // if no pattern match found
        ThreatMessage report = new ThreatMessage();
        report.setMessage(this.toString());
        return report;
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


    @Override
    public String toString()
    {
        StringBuilder msg = new StringBuilder();
        msg.append("Time: "+(new Date().toString())+"\n");

        if(process != null)
            msg.append("[process]\n"+process+" => "+ traffic+"\n");
        else
        if(ip != null)
            msg.append("[ip]\n"+ip + " => " + traffic+"\n");
        else
        if(port != null)
            msg.append("[port]\n:"+port + " => " + traffic+"\n");

        return msg.toString();
    }
}
