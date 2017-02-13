package detector.AppStart.Threads;

import detector.NetInterceptor;


/**************************************************
 * Manages the life cycle of network interceptor
 **************************************************/
public class InterceptorThread extends Thread
{
    @Override
    public void run()
    {
        Thread.currentThread().setName("__InterceptorLifecycle");

        while (true)
        {
            NetInterceptor.getInstance().makeActive();

            try
            {
                Thread.sleep(5000);
            }
            catch (InterruptedException e) {

            }

        }
    }

    @Override
    public void interrupt()
    {
        super.interrupt();
        assert false : "Interceptor thread suddenly stopped.";
    }
}
