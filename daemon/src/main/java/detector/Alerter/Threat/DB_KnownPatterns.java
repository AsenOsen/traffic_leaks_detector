package detector.Alerter.Threat;

import detector.LogHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

/**
 *
 */
public class DB_KnownPatterns
{

    private static final DB_KnownPatterns ourInstance = new DB_KnownPatterns();
    //private HashMap<String, ThreatPattern> threatsDB =
    //        new HashMap<String, ThreatPattern>(50);
    private List<ThreatPattern> orderedPatternList =
            new ArrayList<ThreatPattern>();


    public static DB_KnownPatterns getInstance()
    {
        return ourInstance;
    }


    public Iterator<ThreatPattern> getPatterns()
    {
        return orderedPatternList.iterator();
    }


    private DB_KnownPatterns()
    {

    }


    public void loadDB()
    {
        InputStream resStream = getPatternsDataInputStream();
        BufferedReader resReader = new BufferedReader(new InputStreamReader(resStream));

        StringBuilder resData = new StringBuilder();
        String line;
        try
        {
            while((line = resReader.readLine()) != null)
                resData.append(line);
        }
        catch (IOException e)
        {
            LogHandler.Warn("Error read patterns file: "+e.getMessage());
            return;
        }

        PatternParser parser = new PatternParser(resData.toString());
        orderedPatternList = parser.getPatterns();
        Collections.sort(orderedPatternList);

        LogHandler.Log("Threats patterns DB loaded successfully");
    }


    private InputStream getPatternsDataInputStream()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resStream = classLoader.getResourceAsStream("patterns.json");
        return resStream;
    }


    /*
    * Class incapsulates all the logic about resource format
    * Constructor takes string data
    * getPatterns() return a list of prepared objects
    * */
    private static class PatternParser
    {

        private String data;
        private List<ThreatPattern> patterns = new ArrayList<ThreatPattern>();


        public PatternParser(String resourceData)
        {
            data = resourceData;
        }


        public List<ThreatPattern> getPatterns()
        {
            try
            {
                JSONObject json = new JSONObject(data);
                if(json.has("patterns"))
                {
                    for (String name : json.getJSONObject("patterns").keySet())
                    {
                        JSONObject jsonPattern = json.getJSONObject("patterns").getJSONObject(name);
                        ThreatPattern threatPattern = createPattern(jsonPattern, name);
                        patterns.add(threatPattern);
                    }
                }
                else{
                    LogHandler.Warn("'Patterns' block not found!");
                    return null;
                }
            }
            catch (JSONException e)
            {
                LogHandler.Err(e);
                return null;
            }

            resolvePatternDependencies();
            return patterns;
        }


        /*
        * Creates new java object from json object
        * */
        private ThreatPattern createPattern(JSONObject pattern, String name)
        {
            if(!pattern.has("priority"))
            {
                LogHandler.Err(new Exception("Each pattern SHOULD have a 'priority' field!"));
                return null;
            }

            int priority = pattern.getInt("priority");
            ThreatPattern threatPattern = new ThreatPattern(name, priority);

            if(pattern.has("msg"))
                threatPattern.setMessage(pattern.getString("msg"));

            if(pattern.has("pattern"))
                threatPattern.setDependentPatterns(pattern.getString("pattern"));
            if(pattern.has("ip"))
                threatPattern.setIp(pattern.getString("ip"));
            if(pattern.has("pid"))
                threatPattern.setPid(pattern.getString("pid"));
            if(pattern.has("port"))
                threatPattern.setPort(pattern.getString("port"));
            if(pattern.has("processname"))
                threatPattern.setProcessName(pattern.getString("processname"));
            if(pattern.has("organization"))
                threatPattern.setOrganization(pattern.getString("organization"));
            if(pattern.has("hostname"))
                threatPattern.setHostName(pattern.getString("hostname"));

            return threatPattern;
        }

        /*
        * Sets dependent patterns after loading all patterns.
        * It have to happen AFTER getting ALL @patterns, because otherwise it
        * can be impossible to find dependent pattern by its name
        * */
        private void resolvePatternDependencies()
        {
            for(ThreatPattern pattern : patterns)
                pattern.loadDependentPatterns();
        }

    }

}
