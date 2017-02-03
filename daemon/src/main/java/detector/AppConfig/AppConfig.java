package detector.AppConfig;

import org.apache.commons.cli.*;

/***********************************************
 * Services global configuration for application.
 * Configuration start point - is command line args.
 ***********************************************/
public class AppConfig
{
    private static AppConfig ourInstance = new AppConfig();

    private CommandLine params = null;


    public static AppConfig getInstance()
    {
        return ourInstance;
    }


    private AppConfig()
    {

    }


    public void configure(String[] args)
    {
        params = loadConfig(args);
        configureAppEnvironment();
    }


    private void configureAppEnvironment()
    {
        // gui
        if(params.hasOption("gui"))
            System.setProperty("daemon.config.gui", params.getOptionValue("gui"));
        // mode
        if(params.hasOption("mode"))
            System.setProperty("daemon.config.mode", params.getOptionValue("mode"));
        // locale
        if(params.hasOption("gui"))
            System.setProperty("daemon.config.locale", params.getOptionValue("locale"));
        // analyzer
        if(params.hasOption("max-traffic-during-10-sec"))
            System.setProperty("daemon.config.max-traffic-during-10-sec",
                    params.getOptionValue("max-traffic-during-10-sec"));
        if(params.hasOption("min-leak-size"))
            System.setProperty("daemon.config.min-leak-size",
                    params.getOptionValue("min-leak-size"));
    }


    private CommandLine loadConfig(String[] args)
    {
        HelpFormatter formatter = new HelpFormatter();
        Options options = new Options();

        // GUI
        Option guiOpt = new Option(
                "g", "gui",
                true,
                "Sets the GUI-handler for daemon.\nValue: absolute path to GUI."
        );
        guiOpt.setRequired(false);
        guiOpt.setType(String.class);
        options.addOption(guiOpt);

        // mode
        Option modeOpt = new Option(
                "m", "mode",
                true,
                "Sets the working mode for daemon.\nValue: paranoid|adequate|chill"
        );
        modeOpt.setRequired(false);
        modeOpt.setType(String.class);
        options.addOption(modeOpt);

        // locale
        Option locOpt = new Option(
                "l", "locale",
                true,
                "Sets the locale for daemon strings.\nValue: RU|EN"
        );
        locOpt.setRequired(false);
        locOpt.setType(String.class);
        options.addOption(locOpt);

        // max traffic per 10 seconds
        Option maxTrafficOpt = new Option(
                "b", "max-traffic-during-10-sec",
                true,
                "Sets the maximum allowed traffic per 10 seconds.\nValue: amount of bytes"
        );
        maxTrafficOpt.setRequired(false);
        locOpt.setType(int.class);
        options.addOption(maxTrafficOpt);

        // minimal leak size
        Option minLeakOpt = new Option(
                "s", "min-leak-size",
                true,
                "Sets the minimum size which will be considered as a leak.\nValue: amount of bytes"
        );
        minLeakOpt.setRequired(false);
        locOpt.setType(int.class);
        options.addOption(minLeakOpt);


        try
        {
            CommandLineParser parser = new DefaultParser();
            return parser.parse(options, args);
        }
        catch (ParseException e)
        {
            formatter.printHelp("java -jar daemon.jar [args]", options);
            System.exit(0);
            return null;
        }
    }

}
