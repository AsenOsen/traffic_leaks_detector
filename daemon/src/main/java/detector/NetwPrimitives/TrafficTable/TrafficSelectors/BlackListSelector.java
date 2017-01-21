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
        Iterator<ThreatPattern> harmlessItr = DB_HarmlessPatterns.getInstance().getPatterns();
        Threat potentialThreat = new Threat(trafficFlow);

        while (harmlessItr.hasNext())
        {
            ThreatPattern harmless = harmlessItr.next();
            if(harmless.matches(potentialThreat)) {
                //System.out.println("Ignored: "+harmless);
                return false;
            }
        }

        return true;
    }
}
