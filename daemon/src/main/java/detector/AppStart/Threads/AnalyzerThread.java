package detector.AppStart.Threads;

import detector.Analyzer.Analyzer;
import detector.LogHandler;

/**************************************************
 * Manages the life cycle of analyzer
 **************************************************/
public class AnalyzerThread extends Thread
{
    @Override
    public void run()
    {
        Thread.currentThread().setName("__Analyzer");
        LogHandler.Log("Analyzer is started.");

        while ( true )
        {

            Analyzer.getInstance().analyze();

            try
            {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }

        }
    }

    @Override
    public void interrupt()
    {
        super.interrupt();
        assert false : "Analyzer thread suddenly stopped";
    }
}