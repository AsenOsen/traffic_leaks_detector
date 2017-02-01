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
            if(NetInterceptor.getInstance().isDown())
                NetInterceptor.getInstance().startInterceptor();

            try
            {
                Thread.sleep(5000);
            } catch (InterruptedException e) {

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
