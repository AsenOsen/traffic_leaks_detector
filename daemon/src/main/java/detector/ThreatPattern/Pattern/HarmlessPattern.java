package detector.ThreatPattern.Pattern;

import detector.LogModule;

/**
 * Created by SAMSUNG on 11.02.2017.
 */
public class HarmlessPattern extends ThreatPattern
{

    @Override
    public void validate()
    {
        // "not-empty filter" convention
        if((traffic==null || traffic.isEmpty()) && relatedPatterns==null)
            LogModule.Warn("Filter '"+codeName+"' SHOULD have at least 1 rule!");
    }

}
