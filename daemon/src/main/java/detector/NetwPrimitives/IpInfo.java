package detector.NetwPrimitives;

import detector.LogModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/************************************************
* Contains info about concrete IP address
* ***********************************************/
public class IpInfo {

    private String jsonDump;

    private String ip;
    private String hostname;
    private String city;
    private String region;
    private String country;
    private String location;
    private String orgName;


    public IpInfo(String jsonData)
    {
        jsonDump = jsonData;

        try
        {
            JSONObject json = new JSONObject(jsonData);

            if(json.has("ip"))
                ip = json.getString("ip");
            if(json.has("hostname"))
            {
                hostname = json.getString("hostname");
                if(hostname.compareToIgnoreCase("No Hostname")==0)
                    hostname = null;
            }
            if(json.has("city"))
                city = json.getString("city");
            if(json.has("region"))
                region = json.getString("region");
            if(json.has("country"))
                country = json.getString("country");
            if(json.has("loc"))
                location = json.getString("loc");
            if(json.has("org"))
            {
                orgName = json.getString("org");
                Pattern pattern = Pattern.compile("^AS[0-9]{3,}\\s(.*)$");
                Matcher matcher = pattern.matcher(orgName);
                if(matcher.find())
                    orgName = matcher.group(1);
            }
        }
        catch (JSONException e)
        {
            LogModule.Warn("Error parsing json object: "+e.getMessage()+", JSON: "+ jsonDump);
        }

    }


    @Nullable
    public String getOrgName()
    {
        return orgName;
    }


    @Nullable
    public String getHostName()
    {
        return hostname;
    }


    @NotNull
    public String getGeoInfo()
    {
        return String.format("%s, %s, %s", country, region, city);
    }


    @Override
    public String toString()
    {
        return jsonDump;
    }
}
