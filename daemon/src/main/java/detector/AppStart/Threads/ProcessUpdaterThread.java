package detector.AppStart.Threads;

import detector.Data.ProcessInfoDB;
import detector.LogModule;

/**************************************************
 * Manages the processes updating
 **************************************************/
public class ProcessUpdaterThread extends Thread
{
    @Override
    public void run()
    {
        Thread.currentThread().setName("__ProcessesInfoUpdater");
        LogModule.Log("Process info updator is started.");

        while ( true )
        {
            ProcessInfoDB.getInstance().update();
        }

    }

    @Override
    public void interrupt()
    {
        super.interrupt();
        assert false : "Process thread suddenly stopped";
    }
}
