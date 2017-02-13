package detector.ThreatPattern.Pattern;

import detector.LogModule;

/*******************************************************
 * This is a modification of original ThreatPattern
 * which DOES NOT have any structural differences,
 * but MAY have different behaviour.
 ******************************************************/
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
