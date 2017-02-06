package detector;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import detector.ThreatPattern.ThreatMessage;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayDeque;
import java.util.Queue;

/************************************************
 * This is a singleton.
 *
 * Wraps the logic for calling GUI messages
 ***********************************************/
public class GUIModule
{
    private static GUIModule instance = new GUIModule();

    private String guiAbsolutePath;
    private String guiHashSum;
    private boolean isGuiPresented;
    private volatile Queue<ThreatMessage> messagesForGui = new ArrayDeque<ThreatMessage>();


    public static GUIModule getInstance()
    {
        return instance;
    }


    public void runGui()
    {
        if(!isGuiPresented)
            return;

        try
        {
            if(!isGuiUntouched())
                LogModule.Warn("Security error: cant run gui because its file was modified!");
            else
                Runtime.getRuntime().exec(guiAbsolutePath);
        } catch (IOException e)
        {
            LogModule.Warn("Gui presented, but error when run it.");
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


    private GUIModule()
    {
        initGui();
    }


    private void initGui()
    {
        guiAbsolutePath = System.getProperty("daemon.config.gui", null);

        if(guiAbsolutePath == null) {
            guiFail();
            return;
        }

        if(new File(guiAbsolutePath).exists())
            guiSuccess();
        else
            guiFail();
    }


    private void guiSuccess()
    {
        guiHashSum = getGuiHash();
        isGuiPresented = true;
        LogModule.Log("SET: Daemon is accompanied with GUI - "+ guiAbsolutePath + ". Hash: "+guiHashSum);
    }


    private void guiFail()
    {
        guiHashSum = null;
        isGuiPresented = false;
        LogModule.Log("No GUI specified for daemon - GUI not specified or not exists.");
    }


    private String getGuiHash()
    {
        assert guiAbsolutePath != null : "Gui path cannot be null here.";

        try
        {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            FileInputStream fileStream = new FileInputStream(guiAbsolutePath);
            DigestInputStream digestStream = new DigestInputStream(fileStream, md5);

            int read;
            byte[] bytes = new byte[4096];
            while((read = digestStream.read(bytes)) != -1)
                md5.update(bytes, 0, read);

            fileStream.close();
            return HexBin.encode(md5.digest());
        }
        catch (Exception e)
        {
            LogModule.Warn("Securty error! Could not get the hashsum of GUI.");
            LogModule.Err(e);
        }

        return null;
    }


    private boolean isGuiUntouched()
    {
        String currentHashSum = getGuiHash();
        return guiHashSum!=null && currentHashSum!=null && currentHashSum.equals(guiHashSum);
    }

}
