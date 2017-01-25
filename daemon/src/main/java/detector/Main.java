package detector;

import detector.ThreatPattern.DB_HarmlessPatterns;
import detector.ThreatPattern.DB_KnownPatterns;


public class Main {


    private static Thread interceptorThread =
            new InterceptorThread();
    private static Thread analyzerThread =
            new AnalyzerThread();
    private static Thread processInfoUpdaterThread =
            new ProcessUpdaterThread();
    private static Thread communicationModuleThread =
            new CommunicationThread();


    public static void main(String[] args)
    {
        // Load traffic patterns databases
        DB_KnownPatterns.getInstance().loadDB();
        DB_HarmlessPatterns.getInstance().loadDB();
        // Lazy load GUI
        GUIWrapper.getInstance();

        // Start interceptor lifecycle monitor
        interceptorThread.start();
        // Start traffic analyzer
        analyzerThread.start();
        // Start process info updater
        processInfoUpdaterThread.start();
        // Start server for interaction with daemon
        communicationModuleThread.start();
    }


    private static class InterceptorThread extends Thread
    {
        @Override
        public void run() {

            Thread.currentThread().setName("__InterceptorLifecycle");

            while (true)
            {
                if(NetInterceptor.getInstance().isDown())
                    NetInterceptor.getInstance().startInterceptor();

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {}
            }
        }
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


    private static class CommunicationThread extends Thread
    {
        @Override
        public void run() {

            Thread.currentThread().setName("__CommunicationModule");
            InteractionModule.getInstance().run();
        }
    }

}
