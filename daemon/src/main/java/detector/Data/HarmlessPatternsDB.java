package detector.Data;

import detector.LogModule;
import detector.ThreatPattern.Pattern.ThreatPattern;
import detector.ThreatPattern.PatternStorage.AppFiltersStorage;
import detector.ThreatPattern.PatternStorage.UserFiltersStorage;
import detector.ThreatPattern.Threat;
import detector.UserFiltersManager;
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


    public void addTemporaryPattern(ThreatPattern pattern)
    {
        harmlessList.add(pattern);
    }


    public void addPermanentPattern(ThreatPattern pattern)
    {
        addTemporaryPattern(pattern);
        UserFiltersManager.getInstance().addUserFilter(pattern);
    }


    private HarmlessPatternsDB()
    {

    }


    /*
    * Loads the patterns database from some outer-resource
    * */
    public void loadDB()
    {
        List<ThreatPattern> appFilters = new AppFiltersStorage().getItems();
        List<ThreatPattern> userFilters = new UserFiltersStorage().getItems();

        harmlessList.addAll(appFilters);
        harmlessList.addAll(userFilters);

        /*for(ThreatPattern pattern : harmlessList) {
            pattern.loadDependencies();
            System.out.println("==="+pattern);
        }*/

        LogModule.Log("Filter patterns database loaded: "+
                harmlessList.size()+" filters found. "+
                "Amongst them "+userFilters.size()+" custom user`s filters.");
    }

}
