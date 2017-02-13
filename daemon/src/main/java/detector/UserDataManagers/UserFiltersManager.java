package detector.UserDataManagers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import detector.LogModule;
import detector.ThreatPattern.Pattern.HarmlessPattern;
import detector.ThreatPattern.Pattern.ThreatPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/*******************************************
 * Manages the user`s filter storage.
 ******************************************/
public class UserFiltersManager
{
    private static UserFiltersManager instance = new UserFiltersManager();


    private UserFiltersManager()
    {

    }


    public static UserFiltersManager getInstance()
    {
        return instance;
    }


    @NotNull
    public List<ThreatPattern> getUserFilters()
    {
        List<ThreatPattern> items = new ArrayList<ThreatPattern>();
        String parseData = getUserDataContents();

        // no user data was found
        if(parseData == null)
            return items;

        try
        {
            JSONObject json = new JSONObject(parseData);
            if(json.has("filters"))
            {
                JSONArray patterns = json.getJSONArray("filters");
                for(int i=0; i<patterns.length(); i++)
                {
                    JSONObject jObject = patterns.getJSONObject(i);
                    ThreatPattern threatPattern = fromStorable(jObject.toString());
                    threatPattern.validate();
                    if (threatPattern != null)
                        items.add(threatPattern);
                }
            }
            else {
                LogModule.Warn("'filters' root tag not found for user data file!");
            }
        }
        catch (JSONException e)
        {
            LogModule.Warn("User filters file JSON format error: "+e.getMessage());
        }

        return items;
    }


    public void addUserFilter(ThreatPattern pattern)
    {
        List<ThreatPattern> userFilters = getUserFilters();
        userFilters.add(pattern);
        updateUserFilters(userFilters);
    }


    /*
    * Creates storable object from filters array
    * */
    private void updateUserFilters(List<ThreatPattern> filters)
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        ArrayNode patternsNode = root.arrayNode();
        root.set("filters", patternsNode);

        for(ThreatPattern filter : filters)
        {
            ObjectNode node = mapper.valueToTree(filter);
            patternsNode.add(node);
        }

        String contents = null;
        try
        {
            contents = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (JsonProcessingException e)
        {
            LogModule.Warn("Error during parsing user`s filter: "+e.getMessage());
            assert false;
        }

        updateUserFileContents(contents);
    }


    /*
    * Saves storable object to file as string
    * */
    private void updateUserFileContents(String contents)
    {
        File dataFile = getDataFile();
        if(dataFile==null)
        {
            LogModule.Warn("Could not get place for saving user`s filters!");
            return;
        }

        try
        {
            PrintWriter fileWriter = new PrintWriter(dataFile);
            fileWriter.write(contents);
            fileWriter.close();
            LogModule.Log("User`s file with filters '"+dataFile.getAbsolutePath()+"' was successfully updated!");
        }
        catch (SecurityException e) {
            LogModule.Warn("Could not get access to '"+dataFile.getAbsolutePath()+"'! No rights to write!");
        }
        catch (IOException e) {
            LogModule.Warn("Could not close user`s filter file after writing: "+e.getMessage());
        }
    }


    /*
    * Converts ThreatPattern object to storable object,
    * which is appropriate for user storage format
    * */
    @Nullable
    public String toStorable(ThreatPattern pattern)
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        String result = null;
        try
        {
            result = mapper.writeValueAsString(pattern);
        }
        catch (JsonProcessingException e)
        {
            LogModule.Warn("Could not convert filter pattern to string: "+e.getMessage());
        }

        return result;
    }


    /*
    * Creates the ThreatPattern obhect from storable string
    * relying on the format which is appropriate for user storage format
    * */
    @Nullable
    public ThreatPattern fromStorable(String storable)
    {
        assert storable != null;

        try
        {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

            ThreatPattern harmlessPattern = mapper.readValue(storable, HarmlessPattern.class);
            return harmlessPattern;
        }
        catch (Exception e)
        {
            LogModule.Warn("Error while creating filter pattern from storable: "+e.getMessage());
            return null;
        }
    }

    @Nullable
    private File getDataFile()
    {
        String workingDir = System.getProperty("user.dir", null);

        assert workingDir != null;
        if(workingDir == null)
        {
            LogModule.Warn("System property 'user.dir' is not set in JRE!");
            return null;
        }

       return new File(workingDir + File.separator + "ignores.user.json");
    }


    @Nullable
    private InputStream getContentInputStream()
    {
        File userFiltersFile = getDataFile();
        if(userFiltersFile==null)
            return null;

        InputStream stream = null;

        try
        {
            LogModule.Log("Trying to open user`s filters file: " + userFiltersFile.getAbsolutePath());
            stream = new FileInputStream(userFiltersFile);
            LogModule.Log("User file was found! Opening is successful! ");
        }
        catch (FileNotFoundException e) {
            LogModule.Log("User`s filters file was not found.");
        }
        catch (SecurityException e) {
            LogModule.Warn("Could not get access to '"+userFiltersFile.getAbsolutePath()+"'! No rights to read!");
        }

        return stream;
    }


    @Nullable
    protected String getUserDataContents()
    {
        InputStream inputStream = getContentInputStream();
        if(inputStream==null)
            return null;

        Reader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader resReader = new BufferedReader(reader);
        StringBuilder resData = new StringBuilder();

        try
        {
            String line;
            while((line = resReader.readLine()) != null)
                resData.append(line);
        }
        catch (IOException e) {
            LogModule.Warn("Error reading patterns resource: "+e.getMessage());
            return null;
        }

        try
        {
            resReader.close();
            reader.close();
        }
        catch (IOException e) {
            LogModule.Warn("Could not close user filters file after reading it: "+e.getMessage());
        }

        return resData.toString();
    }
}
