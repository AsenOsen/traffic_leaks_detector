package detector.ThreatPattern;

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


    public Iterator<ThreatPattern> getPatterns()
    {
        return harmlessList.iterator();
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
        LogHandler.Log("Filter patterns database loaded "+harmlessList.size()+" patterns...");
    }

}
