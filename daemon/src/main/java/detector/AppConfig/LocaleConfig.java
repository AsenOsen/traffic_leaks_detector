package detector.AppConfig;

import detector.LogModule;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

/*********************************************************
 * Provides localization logic for application.
 ********************************************************/
public class LocaleConfig
{
    private static LocaleConfig instance = new LocaleConfig();

    private final String localeResource =
            "locale/locale_%s.properties";
    private final String[] supportedLocales = new String[]{
            "ru",
            "en"
    };
    private Properties dictionary = new Properties();



    public static LocaleConfig getInstance()
    {
        return instance;
    }


    public String getLocalizedString(String id)
    {
        assert dictionary != null;
        assert dictionary.containsKey(id);

        return dictionary == null ? null : dictionary.getProperty(id);
    }


    private LocaleConfig()
    {
        setLocale();
    }


    private void setLocale()
    {
        String localeCode =
                System.getProperty("daemon.config.locale", null);

        if(localeCode == null || !isSupportedLocale(localeCode))
        {
            LogModule.Warn("Locale '"+localeCode+"' is NOT supported by app. English was set.");
            localeCode = "en";
        }

        try
        {
            InputStream resStream =
                    getClass().getClassLoader().
                    getResourceAsStream(
                            String.format(localeResource, localeCode)
                    );
            Reader reader = new InputStreamReader(resStream, "UTF-8");
            dictionary.load(reader);
            LogModule.Log("Language was set: "+localeCode);
        }
        catch (Exception e)
        {
            LogModule.Warn("Could not load language lib!");
        }

    }


    private boolean isSupportedLocale(String localeCode)
    {
        assert localeCode != null;

        if(localeCode==null)
            return false;

        for(String locale : supportedLocales)
            if(locale.compareToIgnoreCase(localeCode) == 0)
                return true;

        return false;
    }

}
