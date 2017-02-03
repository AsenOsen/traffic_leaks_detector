package detector.ThreatPattern;

import detector.Data.KnownPatternsDB;
import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

import java.util.Date;

/*********************************************
 * Describes a potential leakage threat
 *********************************************/
public class Threat
{

    private NetProcess thrProcess;   // process which caused the getThreatMessage
    private IPv4Address thrIp;       // ip which caused the getThreatMessage
    private Port thrPort;            // port which caused the getThreatMessage
    private TrafficFlow thrTraffic;  // malicious traffic


    public Threat(TrafficFlow traffic)
    {
        this.thrProcess = traffic.getDominantProcess();
        this.thrIp = traffic.getDominantDstAddr();
        this.thrPort = traffic.getDominantSrcPort();
        this.thrTraffic = traffic;
    }


    public ThreatMessage createReport()
    {
        ThreatPattern pattern = KnownPatternsDB.getInstance().findMatchingPattern(this);
        if(pattern != null)
        {
            ThreatMessage msg = pattern.createMessage(this);
            msg.setLowLevelMessage(this.toString());
            return msg;
        }

        // if no pattern match found
        assert false : "Impossible situation detected!";
        ThreatMessage report = new ThreatMessage();
        report.setUserMessage(this.toString());
        return report;
    }


    public NetProcess getInitiatorProcess()
    {
        return thrProcess;
    }

    public IPv4Address getForeignIp()
    {
        return thrIp;
    }

    public Port getInitiatorPort()
    {
        return thrPort;
    }

    public int getLeakSizeBytes()
    {
        return thrTraffic.getBytes();
    }

    public float getActivityTime()
    {
        return thrTraffic.getLifeTimeSec();
    }


    @Override
    public String toString()
    {
        StringBuilder msg = new StringBuilder();
        msg.append("Time: "+(new Date().toString())+"\n");

        if(thrProcess != null)
            msg.append("[process]\n"+ thrProcess +" => "+ thrTraffic +"\n");
        else
        if(thrIp != null)
            msg.append("[ip]\n"+ thrIp + " <= " + thrTraffic +"\n");
        else
        if(thrPort != null)
            msg.append("[port]\n:"+ thrPort + " => " + thrTraffic +"\n");

        return msg.toString();
    }
}
