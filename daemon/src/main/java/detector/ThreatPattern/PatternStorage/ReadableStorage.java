package detector.ThreatPattern.PatternStorage;

import detector.LogModule;
import detector.ThreatPattern.Pattern.ThreatPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by SAMSUNG on 12.02.2017.
 */
public abstract class ReadableStorage
{

    /*
    * Input stream for reading storage
    * */
    @Nullable
    protected abstract InputStream getContentInputStream();


    /*
    * Gets the data of the storage
    * */
    @NotNull
    public abstract List<ThreatPattern> getItems();


    /*
   * Loads the patterns from some outer-resource
   * */
    @Nullable
    protected String getRawContent()
    {
        InputStream inputStream = getContentInputStream();
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
            return null;
        }

        // Set!
        return resData.toString();
    }

}
