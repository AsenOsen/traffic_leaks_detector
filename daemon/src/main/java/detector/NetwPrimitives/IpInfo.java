package detector.NetwPrimitives;

import detector.LogHandler;
import org.json.JSONException;
import org.json.JSONObject;

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
            ip = json.getString("ip");
            hostname = json.getString("hostname");
            city = json.getString("city");
            region = json.getString("region");
            country = json.getString("country");
            location = json.getString("loc");
            organization = json.getString("org");
        }
        catch (JSONException e)
        {
            LogHandler.Warn("Error parsing json object: "+e.getMessage()+", JSON: "+dump);
        }
    }


    public String getOwner()
    {
        return organization;
    }


    @Override
    public String toString() {
        return dump;
    }
}
