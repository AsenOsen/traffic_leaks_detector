package detector.NetwPrimitives;

import detector.LogHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpInfo {

    private String dump;

    private String ip;
    private String hostname;
    private String city;
    private String region;
    private String country;
    private String location;
    private String organization;


    public IpInfo(String jsonData)
    {
        dump = new String(jsonData);

        try
        {
            JSONObject json = new JSONObject(jsonData);

            if(json.has("ip"))
                ip = json.getString("ip");
            if(json.has("hostname"))
                hostname = json.getString("hostname");
            if(json.has("city"))
                city = json.getString("city");
            if(json.has("region"))
                region = json.getString("region");
            if(json.has("country"))
                country = json.getString("country");
            if(json.has("loc"))
                location = json.getString("loc");
            if(json.has("org"))
                organization = json.getString("org");
        }
        catch (JSONException e)
        {
            LogHandler.Warn("Error parsing json object: "+e.getMessage()+", JSON: "+dump);
        }

    }


    public String getOrg()
    {
        return organization;
    }

    public String getPrettyOrg()
    {
        Pattern pattern = Pattern.compile("^AS[0-9]{3,}\\s(.*)$");
        Matcher matcher = pattern.matcher(organization);
        return matcher.find() ? matcher.group(1) : organization;
    }

    public String getHostname()
    {
        return hostname==null ? null :
                (hostname.compareToIgnoreCase("No Hostname")==0 ? null : hostname);
    }


    @Override
    public String toString() {
        return dump;
    }
}
