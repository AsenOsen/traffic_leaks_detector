package detector.AppConfig;

import detector.AppStart.Main;
import detector.LogModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Parses an info file and provides methods to file`s properties
 */
public class AppInfo
{
    private static AppInfo instance = new AppInfo();
    private static final String INFO_RESOURCE = "/project.prop";
    private Properties props = new Properties();

    public static AppInfo getInstance()
    {
        return instance;
    }

    private AppInfo()
    {
        try
        {
            InputStream stream = Main.class.getResourceAsStream(INFO_RESOURCE);
            props.load(stream);
            stream.close();
        }
        catch (IOException e)
        {
            LogModule.Warn("Could not load app info file - '"+INFO_RESOURCE+"'");
        }
    }

    public String getVersion()
    {
        return props.getProperty("version");
    }

    public String getReleaseName()
    {
        return props.getProperty("name");
    }
}
