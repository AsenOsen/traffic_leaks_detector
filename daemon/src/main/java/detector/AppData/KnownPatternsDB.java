package detector.AppData;

import detector.LogModule;
import detector.ThreatPattern.Pattern.ThreatPattern;
import detector.ThreatPattern.PatternStorage.AppPatternsStorage;
import detector.ThreatPattern.Threat;
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

    private static final KnownPatternsDB instance =
            new KnownPatternsDB();
    private volatile List<ThreatPattern> priorityPatternList =  // Patterns store STRICTLY in ordered way!
            new ArrayList<ThreatPattern>();
    private volatile HashMap<String, ThreatPattern> patternDB =
            new HashMap<String, ThreatPattern>();


    public static KnownPatternsDB getInstance()
    {
        return instance;
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


    /*
   * Loads the patterns database from some outer-resource
   * */
    public void loadDB()
    {
        priorityPatternList.addAll(new AppPatternsStorage().getItems());
        Collections.sort(priorityPatternList);

        for(ThreatPattern pattern : priorityPatternList)
            patternDB.put(pattern.getName(), pattern);

        /*for(ThreatPattern pattern : priorityPatternList) {
            System.out.println("==="+pattern.getName());
            pattern.loadDependencies();
        }*/

        LogModule.Log("Threats patterns database loaded "+priorityPatternList.size()+" patterns.");
    }


    private KnownPatternsDB()
    {

    }

}
