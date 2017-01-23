package detector;


import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.IpInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;

public class DB_IpInfo {

    private static final DB_IpInfo instance = new DB_IpInfo();
    private static final String IP_INFO_SOURCE = "http://ipinfo.io/%s/json";

    private static ConcurrentHashMap<IPv4Address, IpInfo> ipTable =
            new ConcurrentHashMap<IPv4Address, IpInfo>();


    public static DB_IpInfo getInstance()
    {
        return instance;
    }


    private void findInfo(IPv4Address ip)
    {
        if(ip == null || !ip.isValid())
            return;

        StringBuilder info = new StringBuilder();

        try
        {
            URL url = new URL(String.format(IP_INFO_SOURCE, ip));
            URLConnection connection = url.openConnection();
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while((line = reader.readLine()) != null)
                info.append(line);
        }
        catch (MalformedURLException e)
        {
            LogHandler.Err(e);
        }
        catch (IOException e)
        {
            LogHandler.Warn("Cant get info about "+ip+" address.");
        }

        if(info.toString().trim().length() > 0)
        {
            IpInfo infoObject = new IpInfo(info.toString());
            ipTable.putIfAbsent(ip, infoObject);
        }
    }


    /*
    * This is synchronous method.
    * */
    public IpInfo getIpInfo(IPv4Address ip)
    {
        // check existence first and if missing, find info for this IP.
        if(!ipTable.containsKey(ip))
            findInfo(ip);

        // return the info value from table after while
        return ipTable.get(ip);
    }


}
