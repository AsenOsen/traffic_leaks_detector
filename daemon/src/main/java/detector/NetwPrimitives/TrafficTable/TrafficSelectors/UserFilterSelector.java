package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.AppData.HarmlessPatternsDB;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.NetwPrimitives.TrafficTable.TrafficOperations.TrafficSelector;
import detector.ThreatPattern.Pattern.ThreatPattern;
import detector.ThreatPattern.Threat;

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

        //if(isHarmless)
        //    LogModule.Log("Ignored: "+harmless.getName());

        return isHarmless ? false : true;      // if so, then do not select
    }

}
