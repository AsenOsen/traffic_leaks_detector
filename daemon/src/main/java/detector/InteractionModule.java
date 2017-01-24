package detector;

import com.sun.istack.internal.Nullable;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Lets another applications to communicate with this app (daemon)
 */
public class InteractionModule
{
    private static InteractionModule instance = new InteractionModule();

    private ServerSocket communicationServer;
    private int portIntervalStart = 5000;
    private int getPortIntervalEnd = 6000;
    private int serverPort = -1;
    private int reconnectAttempts = 0;


    public static InteractionModule getInstance()
    {
        return instance;
    }


    public boolean isAvailable()
    {
        return portIntervalStart <= serverPort && serverPort <=getPortIntervalEnd;
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


    private boolean startServer()
    {
        IOException error = null;

        for(int port = portIntervalStart; port < getPortIntervalEnd; port++)
        {
            try
            {
                communicationServer = new ServerSocket(port);
                serverPort = port;
                LogHandler.Log("Communication server is successfully started on localhost:"+serverPort+"...");
                return true;
            } catch (IOException e)
            {
                error = e;
                continue;
            }
        }

        if(serverPort == -1)
            LogHandler.Warn("Could not find any available port in range: " +
                    portIntervalStart + ".." + getPortIntervalEnd+"\nError: "+error.getMessage());

        return false;
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
                BufferedReader clientInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter clientOutput = new PrintWriter(client.getOutputStream(), true);
                clientOutput.println(":::daemon_leaks_analyzer_protocol_start:::");

                while(true) {
                    if(!HandleClientQuery(clientInput, clientOutput)) {
                        client.close();
                        break;
                    }
                }
            }
            // if some error happened, it because server is down and it SHOULD be restarted
            catch (IOException e)
            {
                LogHandler.Warn("Server is down.");
                try
                {
                    Thread.sleep(1000);
                    restartServer();
                }
                catch (InterruptedException iException) { }
            }
        }
    }


    private boolean HandleClientQuery(BufferedReader clientInput, PrintWriter clientOutput)
    {
        String command = getClientLine(clientInput);
        String param;

        if(isQuitCommand(command))
            return false;

        if(command.equalsIgnoreCase("test"))
        {
            param = getClientLine(clientInput);
            clientOutput.println("Tested. Param: " + param);
        }

        return true;
    }


    @Nullable
    private String getClientLine(BufferedReader clientInput)
    {
        try
        {
            return clientInput.readLine();
        }
        catch (IOException e)
        {
            return null;
        }
    }


    private boolean isQuitCommand(String command)
    {
       return command==null || command.trim().length()==0 || command.equalsIgnoreCase("quit");
    }
}
