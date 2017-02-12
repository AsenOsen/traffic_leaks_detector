package detector.ThreatPattern.PatternStorage;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import detector.LogModule;
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
 * Parses all known traffic patterns
 * from some outer resource
 ************************************************************/
public class AppPatternsStorage extends ReadableStorage
{
    @NotNull
    @Override
    public List<ThreatPattern> getItems()
    {
        List<ThreatPattern> items = new ArrayList<ThreatPattern>();

        try
        {
            JSONObject json = new JSONObject(getRawContent());
            if(json.has("patterns"))
            {
                JSONArray patterns = json.getJSONArray("patterns");
                for(int i=0; i<patterns.length(); i++)
                {
                    JSONObject jObject = patterns.getJSONObject(i);
                    ThreatPattern threatPattern = createPattern(jObject);
                    threatPattern.validate();
                    if (threatPattern != null)
                        items.add(threatPattern);
                }
            }
            else {
                LogModule.Warn("'Ignores' root tag not found for ignores-data-file!");
            }
        }
        catch (JSONException e)
        {
            LogModule.Warn("Patterns file JSON format error: "+e.getMessage());
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

            ThreatPattern threatPattern = mapper.readValue(jObject.toString(), ThreatPattern.class);
            return threatPattern;
        }
        catch (Exception e)
        {
            LogModule.Warn("Error while creating filter pattern from storable: "+e.getMessage());
            return null;
        }
    }


    @Override
    protected InputStream getContentInputStream()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream resStream = classLoader.getResourceAsStream("patterns.json");

        if(resStream==null)
            LogModule.Warn("No 'patterns.json' resource found.");

        return resStream;
    }
}
