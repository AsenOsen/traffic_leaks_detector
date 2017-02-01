package detector.AppStart.Threads;

import detector.InteractionModule;

/**************************************************
 * Manages the life cycle of communication module
 **************************************************/
public class CommunicationThread extends Thread
{
    @Override
    public void run()
    {
        Thread.currentThread().setName("__CommunicationModule");
        InteractionModule.getInstance().run();
    }

    @Override
    public void interrupt()
    {
        super.interrupt();
        assert false : "Communication thread suddenly died";
    }
}
