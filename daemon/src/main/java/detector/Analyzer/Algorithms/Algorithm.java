package detector.Analyzer.Algorithms;

import detector.NetwPrimitives.Packet;
import detector.NetwPrimitives.TrafficTable.TrafficTable;

/**
 * Created by SAMSUNG on 29.01.2017.
 */
public interface Algorithm
{

    /*
    * Cleans some algorithm`s data
    * */
    void cleanData();

    
    /*
    * Offers to algorithm a new piece of data
    * */
    void offerData(Packet packet);


    /*
    * Returns traffic table with potential threats
    * */
    TrafficTable processData();

}
