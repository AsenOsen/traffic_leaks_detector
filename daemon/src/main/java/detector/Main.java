package detector;

import detector.ThreatPattern.DB_HarmlessPatterns;
import detector.ThreatPattern.DB_KnownPatterns;

public class Main {


    private static Thread analyzerThread =
            new AnalyzerThread();
    private static Thread processInfoUpdaterThread =
            new ProcessUpdaterThread();


    public static void main(String[] args)
    {
        // Load patterns database
        DB_KnownPatterns.getInstance().loadDB();
        DB_HarmlessPatterns.getInstance().loadDB();

        // Begins traffic sniffing
        NetInterceptor.getInstance().startInterceptLoop();

        // Starts analyzing immediately
        analyzerThread.start();

        // Starts process info updater immediately
        processInfoUpdaterThread.start();
    }


    private static class AnalyzerThread extends Thread
    {
        @Override
        public void run() {

            Thread.currentThread().setName("__Analyzer");
            LogHandler.Log("Analyzer is started...");

            while ( true )
            {
                Analyzer.getInstance().analyze();
                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e) { }
            }
        }
    }


    private static class ProcessUpdaterThread extends Thread
    {
        @Override
        public void run() {

            Thread.currentThread().setName("__ProcessesInfoUpdater");
            LogHandler.Log("Process info updator is started...");

            while ( true )
            {
                DB_ProcessInfo.getInstance().update();
            }
        }
    }

}
