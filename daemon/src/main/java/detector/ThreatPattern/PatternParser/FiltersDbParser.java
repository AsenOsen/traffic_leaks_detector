package detector.ThreatPattern.PatternParser;

import detector.LogHandler;
import detector.ThreatPattern.ThreatPattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

/**
 * Created by SAMSUNG on 20.01.2017.
 */
public class FiltersDbParser extends ResourcePatternParser
{
    private enum Mode{
        MODE_PARANOID,  // minimum filters
        MODE_ADEQUATE,  // normal filters
        MODE_CHILL      // all known filters
    }


    private static Mode filterMode = null;

    static{
        String userPickedMode = System.getProperty("mode", "adequate").toLowerCase().trim();
        boolean isParanoic = userPickedMode.equals("paranoid");
        boolean isAdequate = userPickedMode.equals("adequate");
        boolean isChill = userPickedMode.equals("chill");

        if(isParanoic)
            filterMode = Mode.MODE_PARANOID;
        if(isAdequate)
            filterMode = Mode.MODE_ADEQUATE;
        if(isChill)
            filterMode = Mode.MODE_CHILL;

        if(filterMode == null)
            LogHandler.Err(new Exception("Specified filter mode '"+userPickedMode+"' is incorrect!"));
        LogHandler.Log("Chosen mode: "+filterMode);
    }


    @Override
    protected InputStream getPatternDataInputStream()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resStream = classLoader.getResourceAsStream(getResourceNameByMode());

        if(resStream==null)
            LogHandler.Warn("No filters db resource found.");

        return resStream;
    }


    private String getResourceNameByMode()
    {
        String name;
        switch (filterMode)
        {
            case MODE_PARANOID:
                name = "paranoid";
                break;
            case MODE_ADEQUATE:
                name = "adequate";
                break;
            case MODE_CHILL:
                name = "chill";
                break;
            default:
                LogHandler.Warn("Filter mode is undefined!");
                return null;
        }

        return String.format("ignores.%s.json", name);
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
