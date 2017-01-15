package detector.Alerter;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

import java.util.Date;

/**
 * Created by SAMSUNG on 15.01.2017.
 */
public class LeakageAlerter
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
        return "------------------------------------ Leak traffic ---";
    }


}
