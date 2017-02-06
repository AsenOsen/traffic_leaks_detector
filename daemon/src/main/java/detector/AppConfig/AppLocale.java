package detector.AppConfig;

import detector.LogModule;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;

/*********************************************************
 * Provides localization logic for application.
 ********************************************************/
public class AppLocale
{
    private static AppLocale instance = new AppLocale();

    private final String localeResource =
            "locale/locale_%s.properties";
    private Properties dictionary = new Properties();



    public static AppLocale getInstance()
    {
        return instance;
    }


    public String getLocalizedString(String id)
    {
        assert id != null;
        id = id==null ? null : id.toLowerCase().trim();

        if(id == null)
            return null;

        //assert dictionary != null;
        //assert dictionary.containsKey(id);

        return dictionary == null ? null : dictionary.getProperty(id);
    }


    private AppLocale()
    {
        setLocale();
    }


    private void setLocale()
    {
        String localeCode =
                System.getProperty("daemon.config.locale", null)
                        .trim().toLowerCase();

        if(localeCode == null || !isSupportedLocale(localeCode))
        {
            LogModule.Warn("Locale '"+localeCode+"' is NOT supported by app. English was set.");
            localeCode = "en";
        }

        try
        {
            String resourceFile =
                    String.format(localeResource, localeCode);
            InputStream resStream =
                    getClass().getClassLoader().
                    getResourceAsStream(resourceFile);
            Reader reader =
                    new InputStreamReader(resStream, Charset.forName("UTF-8"));

            Properties locProperties = new Properties();
            locProperties.load(reader);
            for(String property : locProperties.stringPropertyNames())
                dictionary.setProperty(property.toLowerCase().trim(), locProperties.getProperty(property));

            LogModule.Log("SET: Language was set: "+localeCode);
        }
        catch (Exception e)
        {
            LogModule.Warn("Could not load locale! Error: "+e.getMessage());
        }

    }


    private boolean isSupportedLocale(String localeCode)
    {
        assert localeCode != null;

        if(localeCode==null)
            return false;

        String resourceFile = String.format(localeResource, localeCode);
        URL res = AppLocale.class.getClassLoader().getResource(resourceFile);

        return res != null;
    }

}
