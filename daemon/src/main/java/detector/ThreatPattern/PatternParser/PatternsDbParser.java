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
    protected InputStream getContentInputStream()
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
            JSONObject json = new JSONObject(getResourceContent());
            for (String name : json.keySet())
            {
                JSONObject jsonPattern = json.getJSONObject(name);
                jsonPattern.put(PatternField.NAME, name);

                if(!jsonPattern.has(PatternField.PRIORITY))
                {
                    LogModule.Warn("Each traffic pattern SHOULD have a priority!");
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
