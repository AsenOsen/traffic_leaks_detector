package detector.Alerter;

import detector.LogHandler;

import java.io.File;
import java.io.IOException;

/**
 * GUI runner for Alerter class
 */
public class AlerterGUI
{
    private static AlerterGUI instance = new AlerterGUI();

    private static String guiExecutable;
    private static long guiHashSum;
    private static boolean isGuiPresented;


    public static AlerterGUI getInstance()
    {
        return instance;
    }


    public void runGui()
    {
        if(!isGuiPresented)
            return;

        try
        {
            if(new File(guiExecutable).length() != guiHashSum)
                LogHandler.Warn("Security error: cant run gui because its file was modified.");
            else
                Runtime.getRuntime().exec(guiExecutable);
        } catch (IOException e)
        {
            LogHandler.Warn("Gui presented, but error when run it.");
        }
    }


    private AlerterGUI()
    {
        initGui();
    }


    private void initGui()
    {
        guiExecutable = System.getProperty("gui", null);
        System.out.println(guiExecutable);

        if(guiExecutable == null) {
            guiFail();
            return;
        }

        if(new File(guiExecutable).exists())
            guiSuccess();
        else
            guiFail();
    }


    private void guiSuccess()
    {
        assert guiExecutable!=null : "Gui path cannot be null here.";

        guiHashSum = new File(guiExecutable).length();
        isGuiPresented = true;
        LogHandler.Log("Daemon is accompanied with GUI - "+guiExecutable);
    }


    private void guiFail()
    {
        guiHashSum = 0;
        isGuiPresented = false;
        LogHandler.Log("No GUI presented for daemon.");
    }

}
