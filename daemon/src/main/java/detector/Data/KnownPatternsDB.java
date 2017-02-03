package detector.Data;

import detector.LogModule;
import detector.ThreatPattern.PatternParser.PatternsDbParser;
import detector.ThreatPattern.Threat;
import detector.ThreatPattern.ThreatPattern;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**************************************************************************
 * This is a singleton.
 *
 * Stores all known traffic patterns in ordered(by its priority) way.
 * Patterns with higher priority goes first.
 **************************************************************************/
public class KnownPatternsDB
{

    private static final KnownPatternsDB ourInstance =
            new KnownPatternsDB();
    private volatile List<ThreatPattern> priorityPatternList =  // Patters stores strictly in ordered way!
            new ArrayList<ThreatPattern>();
    private volatile HashMap<String, ThreatPattern> patternDB =
            new HashMap<String, ThreatPattern>();


    public static KnownPatternsDB getInstance()
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


    private KnownPatternsDB()
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

        LogModule.Log("Threats patterns database loaded "+priorityPatternList.size()+" patterns.");
    }

}
