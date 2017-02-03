package detector.Data;

import detector.LogModule;
import detector.ThreatPattern.PatternParser.FiltersDbParser;
import detector.ThreatPattern.Threat;
import detector.ThreatPattern.ThreatPattern;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/************************************************
 * Provides a white-list of traffic patterns
 * (white filters).
 ************************************************/
public class HarmlessPatternsDB
{
    private static final HarmlessPatternsDB ourInstance =
            new HarmlessPatternsDB();
    private volatile List<ThreatPattern> harmlessList =
            new ArrayList<ThreatPattern>();


    public static HarmlessPatternsDB getInstance()
    {
        return ourInstance;
    }


    @Nullable
    public ThreatPattern findMatchingPattern(Threat threat)
    {
        for(ThreatPattern pattern : harmlessList)
        {
            if(pattern.matches(threat))
                return pattern;
        }
        return null;
    }


    private HarmlessPatternsDB()
    {

    }


    /*
    * Loads the patterns database from some outer-resource
    * */
    public void loadDB()
    {
        new FiltersDbParser().fillListWithData(harmlessList);

        /*for(ThreatPattern pattern : harmlessList) {
            pattern.loadDependencies();
            System.out.println("==="+pattern);
        }*/

        LogModule.Log("Filter patterns database loaded "+harmlessList.size()+" patterns.");
    }

}
