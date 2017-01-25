package detector;

import com.sun.istack.internal.Nullable;
import detector.ThreatPattern.ThreatMessage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

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

    private Queue<ThreatMessage> messagesForGui = new ArrayDeque<ThreatMessage>();


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


    public synchronized void offerMessageForGui(ThreatMessage message)
    {
        messagesForGui.add(message);
    }


    @Nullable
    public synchronized ThreatMessage takeMessageForGui()
    {
        return messagesForGui.poll();
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
