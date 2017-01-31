package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.Db.DB_HarmlessPatterns;
import detector.NetwPrimitives.TrafficTable.TrafficOperations.TrafficSelector;
import detector.ThreatPattern.Threat;
import detector.ThreatPattern.ThreatPattern;

/******************************************************************
 * Selects only those trafficFlows which ARE NOT in user white-list
 *****************************************************************/
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
