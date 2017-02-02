package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.Data.KnownPatternsDB;
import detector.NetwPrimitives.TrafficFlow.TrafficFlow;
import detector.NetwPrimitives.TrafficTable.TrafficOperations.TrafficSelector;
import detector.ThreatPattern.Threat;
import detector.ThreatPattern.ThreatPattern;

/****************************************************************
 * Selects the traffic which belongs to not-standard application.
 * *************************************************************/
public class UnusualAppSelector implements TrafficSelector
{

    private ThreatPattern knownApps = null;


    @Override
    public boolean select(TrafficFlow trafficFlow)
    {
        // load pattern
        if(knownApps == null)
            knownApps = KnownPatternsDB.getInstance().getPatternByName("Pattern.StdApps");

        if(knownApps == null)
            return false;
        // if pattern is loaded, but traffic mismatches to it, than select this traffic
        else {
            Threat threat = new Threat(trafficFlow);
            return  !knownApps.matches(threat);
        }

    }

}
