package detector.Alerter;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

import java.util.Date;

/**
 * Alerts about long-active traffic flows
 */
public class LongLivingTrafficAlerter
        extends Alerter
{
    @Override
    protected void raiseProcessAlert(NetProcess process, TrafficFlow traffic)
    {
        System.out.println(getSpecificTitle());
        System.out.println("Date: "+(new Date().toString()));
        System.out.println("[process]Процесс отправляет подозрительно много данных\n"+process+" => "+ traffic);
    }

    @Override
    protected void raisePortAlert(Port port, TrafficFlow traffic)
    {
        System.out.println(getSpecificTitle());
        System.out.println("Date: "+(new Date().toString()));
        System.out.println("[port]Порт отправляет подозрительно много данных\n:"+port + " => " + traffic);
    }

    @Override
    protected void raiseIpAlert(IPv4Address ip, TrafficFlow traffic)
    {
        System.out.println(getSpecificTitle());
        System.out.println("Date: "+(new Date().toString()));
        System.out.println("[ip]На IP уходит подозрительно много данных\n"+ip + " => " + traffic);
    }


    private String getSpecificTitle()
    {
        return "------------------------------------ Long-living traffic ---";
    }
}
