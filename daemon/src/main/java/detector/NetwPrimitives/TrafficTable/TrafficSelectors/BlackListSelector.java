package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.ThreatPattern.DB_HarmlessPatterns;
import detector.ThreatPattern.Threat;
import detector.ThreatPattern.ThreatPattern;


import java.util.Iterator;

/**
 * Selects only those trafficFlows which ARE NOT in white-list
 */
public class BlackListSelector implements TrafficSelector
{

    @Override
    public boolean select(TrafficFlow trafficFlow)
    {
        Threat potentialThreat = new Threat(trafficFlow);
        ThreatPattern harmless = DB_HarmlessPatterns.getInstance().findMatchingPattern(potentialThreat);

        boolean isHarmless = harmless != null; // pattern in harmless list
        return isHarmless ? false : true;      // if so, then do not select
    }
}
