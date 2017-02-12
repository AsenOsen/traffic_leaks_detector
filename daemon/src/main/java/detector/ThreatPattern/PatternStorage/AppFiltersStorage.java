package detector.ThreatPattern.PatternStorage;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import detector.LogModule;
import detector.ThreatPattern.Pattern.HarmlessPattern;
import detector.ThreatPattern.Pattern.ThreatPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/*************************************************************
 * Parses white-list(filters) traffic patterns
 * from some outer resource
************************************************************/
public class AppFiltersStorage extends ReadableStorage
{
    private enum Mode{
        MODE_PARANOID,  // minimum filters
        MODE_ADEQUATE,  // normal filters
        MODE_CHILL      // all known filters
    }


    private Mode filterMode = null;


    @NotNull
    @Override
    public List<ThreatPattern> getItems()
    {
        List<ThreatPattern> items = new ArrayList<ThreatPattern>();

        try
        {
            JSONObject json = new JSONObject(getRawContent());
            if(json.has("ignores"))
            {
                JSONArray ignores = json.getJSONArray("ignores");
                for(int i=0; i<ignores.length(); i++)
                {
                    JSONObject jPattern = ignores.getJSONObject(i);
                    ThreatPattern threatPattern = createPattern(jPattern);
                    threatPattern.validate();
                    if(threatPattern != null)
                        items.add(threatPattern);
                }
            }
            else {
                LogModule.Warn("'Ignores' root tag not found for ignores-data-file!");
            }
        }
        catch (JSONException e)
        {
            LogModule.Warn("Filter file JSON format error: "+e.getMessage());
        }

        return items;
    }


    @Nullable
    public ThreatPattern createPattern(JSONObject jObject)
    {
        assert jObject != null;

        try
        {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

            ThreatPattern ThreatPattern = mapper.readValue(jObject.toString(), HarmlessPattern.class);
            return ThreatPattern;
        }
        catch (Exception e)
        {
            LogModule.Warn("Error while creating filter pattern from storable: "+e.getMessage());
            return null;
        }
    }
    

    @Nullable
    @Override
    protected InputStream getContentInputStream()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resStream = classLoader.getResourceAsStream(getResourceNameByMode());

        if(resStream==null)
            LogModule.Warn("No filters db resource found.");

        return resStream;
    }


    private void defineMode()
    {
        if(filterMode != null)
            return;

        String userPickedMode = System.getProperty("daemon.config.mode", "adequate");
        boolean isParanoid = userPickedMode.equals("paranoid");
        boolean isAdequate = userPickedMode.equals("adequate");
        boolean isChill = userPickedMode.equals("chill");

        if(isParanoid)
            filterMode = Mode.MODE_PARANOID;
        if(isAdequate)
            filterMode = Mode.MODE_ADEQUATE;
        if(isChill)
            filterMode = Mode.MODE_CHILL;

        if(filterMode == null)
            LogModule.Err(new Exception("Specified filter mode '"+userPickedMode+"' is incorrect!"));
        LogModule.Log("Chosen alert mode: "+filterMode);
    }


    private String getResourceNameByMode()
    {
        defineMode();

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
                LogModule.Warn("Filter mode is undefined!");
                return null;
        }

        return String.format("ignores.%s.json", name);
    }

}
