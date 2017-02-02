package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.LogHandler;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.Data.HarmlessPatternsDB;
import detector.NetwPrimitives.TrafficTable.TrafficOperations.TrafficSelector;
import detector.ThreatPattern.Threat;
import detector.ThreatPattern.ThreatPattern;

/******************************************************************
 * Selects only those trafficFlows which ARE NOT in user white-list
 *****************************************************************/
public class UserFilterSelector implements TrafficSelector
{

    @Override
    public boolean select(TrafficFlow trafficFlow)
    {
        Threat potentialThreat = new Threat(trafficFlow);
        ThreatPattern harmless = HarmlessPatternsDB.getInstance().findMatchingPattern(potentialThreat);

        boolean isHarmless = harmless != null; // pattern in harmless list

        if(isHarmless)
            LogHandler.Log("Ignored: "+harmless.getName());

        return isHarmless ? false : true;      // if so, then do not select
    }

}
