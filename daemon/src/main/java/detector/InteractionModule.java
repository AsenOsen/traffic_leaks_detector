package detector;

import detector.AppData.HarmlessPatternsDB;
import detector.ThreatPattern.Pattern.ThreatPattern;
import detector.ThreatPattern.ThreatMessage;
import detector.UserDataManagers.UserFiltersManager;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/******************************************************
 * Provides an interface for other applications
 * to communicate with this app (daemon)
 *
 * Describes communication protocol.
 ******************************************************/
public class InteractionModule
{
    private static InteractionModule instance = new InteractionModule();

    // Server`s contract:
    private static final int SERVER_PORT_RANGE_START = 5000;
    private static final int SERVER_PORT_RANGE_END = 5010;
    private static final String SERVER_PROTOCOL_START = ":::daemon_protocol_start:::";
    private static final String SERVER_PROTOCOL_NO_MSG = ":::daemon_protocol_no_msg:::";
    private static final String SERVER_PROTOCOL_PINGED = ":::daemon_protocol_pinged:::";
    private static final String SERVER_PROTOCOL_FINISH = ":::daemon_protocol_finish:::";
    private static final String SERVER_PROTOCOL_UNK_CMD = ":::daemon_protocol_unknown_command:::";
    private static final Charset SERVER_CHARSET = Charset.forName("UTF-8");

    private int serverPort = -1;
    private ServerSocket communicationServer;
    private BufferedReader clientInput = null;
    private OutputStream clientOutput = null;

    
    public static InteractionModule getInstance()
    {
        return instance;
    }


    /*
    * Available means that it was binded to port in ports range
    * */
    public boolean isAvailable()
    {
        return SERVER_PORT_RANGE_START <= serverPort && serverPort <= SERVER_PORT_RANGE_END;
    }


    private InteractionModule()
    {

    }


    public void run()
    {
        startServer();
        if(isAvailable())
            acceptClients();
    }


    private void startServer()
    {
        IOException error = null;

        for(int port = SERVER_PORT_RANGE_START; port <= SERVER_PORT_RANGE_END; port++)
        {
            try
            {
                communicationServer = new ServerSocket(port);
                serverPort = port;
                LogModule.Log("Communication server is successfully started on localhost:"+serverPort);
                break;
            }
            catch (IOException e) {
                error = e;
                continue;
            }
        }

        if(serverPort == -1)
            LogModule.Warn("Could not find any available port in range: " +
                    SERVER_PORT_RANGE_START + ".." + SERVER_PORT_RANGE_END +
                    "\nError: " + (error==null ? "": error.getMessage()));
    }


    private void restartServer()
    {
        if(!communicationServer.isClosed())
        {
            try
            {
                communicationServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        startServer();
    }


    private void acceptClients()
    {
        while(true)
        {
            try
            {
                Socket client = communicationServer.accept();
                LogModule.Log("New communication client: "+client.toString());

                clientInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
                clientOutput = client.getOutputStream();

                sendToClient(SERVER_PROTOCOL_START);

                while(true) {
                    if(!HandleClientQuery()) {
                        LogModule.Log("Client terminated: "+client.toString());
                        client.close();
                        clientInput.close();
                        clientOutput.close();
                        break;
                    }
                }
            }
            // if some error happened, it because server is down and it SHOULD be restarted
            catch (IOException e)
            {
                LogModule.Warn("Server went down.");
                try
                {
                    Thread.sleep(1000);
                    restartServer();
                }
                catch (InterruptedException iException) { }
            }
        }
    }


    @Nullable
    private String getClientLine()
    {
        try
        {
            return clientInput.readLine().trim();
        }
        catch (Exception e) {
            return null;
        }
    }


    private void sendToClient(String data)
    {
        try
        {
            clientOutput.write(data.getBytes(SERVER_CHARSET));
            clientOutput.flush();
        }
        catch (Exception e) {
            return;
        }
    }


    /*
    * Handles client`s message.
    * Returns FALSE if client wants quit, FALSE - otherwise
    * */
    private boolean HandleClientQuery()
    {
        String command = getClientLine();
        boolean isQuit = command==null ||
                command.trim().length()==0 ||
                command.equalsIgnoreCase("quit");

        if(isQuit)
        {
            commandQuit();
            return false;
        }
        else if(command.equalsIgnoreCase("ping"))
        {
            commandPing();
        }
        else if(command.equalsIgnoreCase("get_alert"))
        {
            commandGetAlert();
        }
        else if(command.equalsIgnoreCase("ignore_tmp"))
        {
            commandAddTmpIgnore();
        }
        else if(command.equalsIgnoreCase("ignore_permanent"))
        {
            commandAddPermanentIgnore();
        }
        else if(command.equalsIgnoreCase("grab_all_user_ignores"))
        {

        }
        else if(command.equalsIgnoreCase("delete_ignore"))
        {

        }
        else
        {
            commandUnknown();
        }

        return true;
    }


    private void commandQuit()
    {
        sendToClient(SERVER_PROTOCOL_FINISH);
    }


    private void commandPing()
    {
        sendToClient(SERVER_PROTOCOL_PINGED);
    }


    private void commandGetAlert()
    {
        ThreatMessage message = GUIModule.getInstance().takeMessageForGui();
        if(message != null)
        {
            sendToClient(message.produceGuiMessage());
            LogModule.Log("Message was sent to client through socket.");
        }else{
            sendToClient(SERVER_PROTOCOL_NO_MSG);
        }
    }


    private void commandAddTmpIgnore()
    {
        LogModule.Log("Client asked for temporary ignorance.");
        String filter = getClientLine();
        ThreatPattern pattern = UserFiltersManager.getInstance().fromStorable(filter);
        if(pattern!=null)
        {
            HarmlessPatternsDB.getInstance().addTemporaryPattern(pattern);
            LogModule.Log("New temporary filter was added: "+pattern);
        }
    }


    private void commandAddPermanentIgnore()
    {
        LogModule.Log("Client asked for permanent ignorance.");
        String filter = getClientLine();
        ThreatPattern pattern = UserFiltersManager.getInstance().fromStorable(filter);
        if(pattern!=null)
        {
            HarmlessPatternsDB.getInstance().addPermanentPattern(pattern);
            LogModule.Log("New permanent filter was added: "+pattern);
        }
    }


    private void commandUnknown()
    {
        sendToClient(SERVER_PROTOCOL_UNK_CMD);
    }

}
