package detector.ThreatPattern;

import com.sun.istack.internal.Nullable;
import detector.LogHandler;
import detector.ThreatPattern.PatternParser.FiltersDbParser;
import detector.ThreatPattern.PatternParser.PatternsDbParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by SAMSUNG on 20.01.2017.
 */
public class DB_HarmlessPatterns
{
    private static final DB_HarmlessPatterns ourInstance =
            new DB_HarmlessPatterns();
    private List<ThreatPattern> harmlessList =
            new ArrayList<ThreatPattern>();


    public static DB_HarmlessPatterns getInstance()
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


    private DB_HarmlessPatterns()
    {

    }


    /*
    * Loads the patterns database from some outer-resource
    * */
    public void loadDB()
    {
        new FiltersDbParser().fillListWithData(harmlessList);

        /*for(ThreatPattern pattern : harmlessList) {
            System.out.println("==="+pattern.getName());
            pattern.loadDependencies();
        }*/

        LogHandler.Log("Filter patterns database loaded "+harmlessList.size()+" patterns...");
    }

}
