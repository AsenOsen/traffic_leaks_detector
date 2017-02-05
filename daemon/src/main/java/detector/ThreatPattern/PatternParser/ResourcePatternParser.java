package detector.ThreatPattern.PatternParser;

import com.fasterxml.jackson.databind.ObjectMapper;
import detector.LogModule;
import detector.ThreatPattern.ThreatPattern;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

/*************************************************************
* Class encapsulates all the logic about outer resource format.
* It fills the passed List with java objects.
*********************************************************** */
public abstract class ResourcePatternParser
{
    private String resourceData;


    public ResourcePatternParser()
    {
        readDataFromResource();
    }


    /*
    * Fills passed List with parsed objects
    * */
    public abstract void fillListWithData(List<ThreatPattern> listToFill);
    /*
    * Returns the implementation-specific stream reader
    * */
    protected abstract InputStream getPatternDataInputStream();


    protected String getResourceData()
    {
        return resourceData;
    }


    /*
    * Creates new java object from json object
    * */
    protected ThreatPattern createPattern(JSONObject jPattern)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            ThreatPattern newThreatPattern = mapper.readValue(jPattern.toString(), ThreatPattern.class);
            newThreatPattern.validate();
            return newThreatPattern;
        }
        catch (Exception e)
        {
            LogModule.Warn("Error while parsing object: "+e.getMessage());
            return null;
        }
    }


    /*
    * Loads the patterns from some outer-resource
    * */
    private final void readDataFromResource()
    {
        InputStream inputStream = getPatternDataInputStream();
        Reader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader resReader = new BufferedReader(reader);
        StringBuilder resData = new StringBuilder();

        try
        {
            String line;
            while((line = resReader.readLine()) != null)
                resData.append(line);
        }
        catch (IOException e)
        {
            LogModule.Warn("Error read patterns resource: "+e.getMessage());
            return;
        }

        // Set!
        this.resourceData = resData.toString();
    }
}
