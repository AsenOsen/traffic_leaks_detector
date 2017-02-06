package detector.ThreatPattern.PatternParser;

import detector.LogModule;
import detector.ThreatPattern.ThreatPattern;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

/*************************************************************
 * Parses all known traffic patterns
 * from some outer resource
 ************************************************************/
public class PatternsDbParser extends ResourcePatternParser
{

    @Override
    protected InputStream getPatternDataInputStream()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resStream = classLoader.getResourceAsStream("patterns.json");

        if(resStream==null)
            LogModule.Warn("No 'patterns.json' resource found.");

        return resStream;
    }


    @Override
    public void fillListWithData(List<ThreatPattern> listToFill)
    {
        try
        {
            JSONObject json = new JSONObject(getResourceData());
            for (String name : json.keySet())
            {
                JSONObject jsonPattern = json.getJSONObject(name);
                jsonPattern.put("name", name);
                if(!jsonPattern.has("priority"))
                {
                    LogModule.Err(new Exception("Each pattern in patterns db SHOULD have a 'priority' field!"));
                    continue;
                }

                ThreatPattern threatPattern = createPattern(jsonPattern);
                if(threatPattern != null)
                    listToFill.add(threatPattern);
            }
        }
        catch (JSONException e)
        {
            LogModule.Warn("Patterns file JSON format error: "+e.getMessage());
        }
    }
}
