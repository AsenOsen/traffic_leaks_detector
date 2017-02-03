package detector;

import detector.ThreatPattern.ThreatMessage;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

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
    private static final String SERVER_PROTOCOL_FINISH = ":::daemon_protocol_finish:::";
    private static final String SERVER_PROTOCOL_UNK_CMD = ":::daemon_protocol_unknown_command:::";

    private int serverPort = -1;
    private ServerSocket communicationServer;
    private BufferedReader clientInput = null;
    private PrintWriter clientOutput = null;

    
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
            } catch (IOException e)
            {
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
                clientOutput = new PrintWriter(client.getOutputStream(), true);

                clientOutput.println(SERVER_PROTOCOL_START);
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
                LogModule.Warn("Server is down.");
                try
                {
                    Thread.sleep(1000);
                    restartServer();
                }
                catch (InterruptedException iException) { }
            }
        }
    }


    private boolean HandleClientQuery()
    {
        String command = getClientLine();
        String param;

        if(isQuitCommand(command))
        {
            sendToClient(SERVER_PROTOCOL_FINISH);
            return false;
        }
        else
        if(command.equalsIgnoreCase("test"))
        {
            param = getClientLine();
            sendToClient("Tested. Param: " + param);
        }
        else
        if(command.equalsIgnoreCase("get_alert"))
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
        else
        {
            sendToClient(SERVER_PROTOCOL_UNK_CMD);
        }

        return true;
    }


    @Nullable
    private String getClientLine()
    {
        try
        {
            return clientInput.readLine().trim();
        }
        catch (Exception e)
        {
            return null;
        }
    }


    private void sendToClient(String data)
    {
        try
        {
            clientOutput.println(data);
        }
        catch (Exception e)
        {
            return;
        }
    }


    private boolean isQuitCommand(String command)
    {
       return command==null || command.trim().length()==0 || command.equalsIgnoreCase("quit");
    }
}
