package detector;

import java.io.File;
import java.io.IOException;

/**
 * This is a singleton.
 * Wraps the logic for calling GUI messages
 */
public class GUIWrapper
{
    private static GUIWrapper instance = new GUIWrapper();

    private static String guiExecutable;
    private static long guiHashSum;
    private static boolean isGuiPresented;


    public static GUIWrapper getInstance()
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


    private GUIWrapper()
    {
        initGui();
    }


    private void initGui()
    {
        guiExecutable = System.getProperty("gui", null);

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
        LogHandler.Log("No GUI specified for daemon.");
    }

}
