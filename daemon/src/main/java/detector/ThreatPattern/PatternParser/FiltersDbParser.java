package detector.ThreatPattern.PatternParser;

import detector.LogHandler;
import detector.ThreatPattern.ThreatPattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.rmi.runtime.Log;

import java.io.InputStream;
import java.util.List;

/**
 * Created by SAMSUNG on 20.01.2017.
 */
public class FiltersDbParser extends ResourcePatternParser
{

    @Override
    protected InputStream getPatternDataInputStream()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resStream = classLoader.getResourceAsStream("ignores.adequate.json");
        return resStream;
    }


    @Override
    public void fillListWithData(List<ThreatPattern> listToFill)
    {
        try
        {
            JSONObject json = new JSONObject(getResourceData());
            if(json.has("ignores"))
            {
                JSONArray ignores = json.getJSONArray("ignores");
                for(int i=0; i<ignores.length(); i++)
                {
                    JSONObject ignorePattern = ignores.getJSONObject(i);
                    ThreatPattern threatPattern = createPattern(ignorePattern);
                    listToFill.add(threatPattern);
                }
            }
            else {
                LogHandler.Warn("'Ignores' root tag not found for ignores-data-file!");
            }
        }
        catch (JSONException e)
        {
            LogHandler.Err(e);
        }
    }

}
