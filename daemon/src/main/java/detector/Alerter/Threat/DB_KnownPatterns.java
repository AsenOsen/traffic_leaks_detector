package detector.Alerter.Threat;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import detector.LogHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

/**
 * This is a singleton.
 * Stores all known traffic patterns in ordered(by its priority) way.
 * Patterns with higher priority goes first.
 */
public class DB_KnownPatterns
{

    private static final DB_KnownPatterns ourInstance =
            new DB_KnownPatterns();
    private List<ThreatPattern> orderedPatternList =  // Patters stores in ordered way.
            new ArrayList<ThreatPattern>();


    public static DB_KnownPatterns getInstance()
    {
        return ourInstance;
    }


    /*
    * Returns an ordered patterns iterator
    * */
    public Iterator<ThreatPattern> getPatterns()
    {
        return orderedPatternList.iterator();
    }


    private DB_KnownPatterns()
    {

    }


    /*
    * Loads the patterns from some outer-resource
    * */
    public void loadDB()
    {
        InputStream stream = getPatternDataInputStream();
        BufferedReader resReader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder resData = new StringBuilder();

        try
        {
            String line;
            while((line = resReader.readLine()) != null)
                resData.append(line);
        }
        catch (IOException e)
        {
            LogHandler.Warn("Error read patterns resource: "+e.getMessage());
            return;
        }

        new PatternParser().fillPatternListWithData(resData.toString());
        Collections.sort(orderedPatternList);

        LogHandler.Log("Threats patterns database loaded successfully...");
    }


    /*
    * Provides an outer-resource`s stream
    * */
    private InputStream getPatternDataInputStream()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resStream = classLoader.getResourceAsStream("patterns.json");
        return resStream;
    }



    /*
    * Class encapsulates all the logic about resource format.
    * If fills the ArrayList of parent class with java objects.
    * */
    private class PatternParser
    {

        public void fillPatternListWithData(String data)
        {
            try
            {
                JSONObject json = new JSONObject(data);
                for (String name : json.keySet())
                {
                    JSONObject jsonPattern = json.getJSONObject(name);
                    ThreatPattern threatPattern = createPattern(jsonPattern, name);
                    orderedPatternList.add(threatPattern);
                }
            }
            catch (JSONException e)
            {
                LogHandler.Err(e);
            }
        }


        /*
        * Creates new java object from json object
        * */
        private ThreatPattern createPattern(JSONObject jPattern, String name)
        {
            if(!jPattern.has("priority"))
            {
                LogHandler.Err(new Exception("Each pattern SHOULD have a 'priority' field!"));
                return null;
            }

            try
            {
                ObjectMapper mapper = new ObjectMapper();
                InjectableValues inject = new InjectableValues.Std().addValue(String.class, name);
                mapper.setInjectableValues(inject);
                ThreatPattern newThreatPattern = mapper.readValue(jPattern.toString(), ThreatPattern.class);
                return newThreatPattern;
            }
            catch (Exception e)
            {
                LogHandler.Warn("Error while parsing object: "+e.getMessage());
                return null;
            }
        }
    }

}
