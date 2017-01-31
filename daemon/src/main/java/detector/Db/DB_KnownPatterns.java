package detector.Db;

import com.sun.istack.internal.Nullable;
import detector.LogHandler;
import detector.ThreatPattern.PatternParser.PatternsDbParser;
import detector.ThreatPattern.Threat;
import detector.ThreatPattern.ThreatPattern;

import java.util.*;

/**************************************************************************
 * This is a singleton.
 *
 * Stores all known traffic patterns in ordered(by its priority) way.
 * Patterns with higher priority goes first.
 **************************************************************************/
public class DB_KnownPatterns
{

    private static final DB_KnownPatterns ourInstance =
            new DB_KnownPatterns();
    private List<ThreatPattern> priorityPatternList =  // Patters stores strictly in ordered way!
            new ArrayList<ThreatPattern>();
    private HashMap<String, ThreatPattern> patternDB =
            new HashMap<String, ThreatPattern>();


    public static DB_KnownPatterns getInstance()
    {
        return ourInstance;
    }


    @Nullable
    public ThreatPattern findMatchingPattern(Threat threat)
    {
        for(ThreatPattern pattern : priorityPatternList)
        {
            if(pattern.matches(threat))
                return pattern;
        }
        return null;
    }


    public Set<String> getNames()
    {
        return patternDB.keySet();
    }


    @Nullable
    public ThreatPattern getPatternByName(String name)
    {
        return patternDB.get(name);
    }


    private DB_KnownPatterns()
    {

    }


    /*
    * Loads the patterns database from some outer-resource
    * */
    public void loadDB()
    {
        new PatternsDbParser().fillListWithData(priorityPatternList);
        Collections.sort(priorityPatternList);

        for(ThreatPattern pattern : priorityPatternList)
            patternDB.put(pattern.getName(), pattern);

        /*for(ThreatPattern pattern : priorityPatternList) {
            System.out.println("==="+pattern.getName());
            pattern.loadDependencies();
        }*/

        LogHandler.Log("Threats patterns database loaded "+priorityPatternList.size()+" patterns...");
    }

}
