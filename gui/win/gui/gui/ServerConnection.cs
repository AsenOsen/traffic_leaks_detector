using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace gui
{
    class ServerConnection
    {
        // Server`s contract:
        private const int SERVER_PORT_RANGE_START = 5000;
        private const int SERVER_PORT_RANGE_END = 5010;
        private const string SERVER_PROTOCOL_START = ":::daemon_protocol_start:::";
        private const string SERVER_PROTOCOL_NO_MSG = ":::daemon_protocol_no_msg:::";
        private const string SERVER_PROTOCOL_FINISH = ":::daemon_protocol_finish:::";
        private const string SERVER_PROTOCOL_UNK_CMD = ":::daemon_protocol_unknown_command:::";

        private TcpClient client = new TcpClient();


        public ServerConnection()
        {
 
        }


        public String GetMessageFromServer()
        {
            SendServerInput("get_alert\n");
            String serverMessage = ReadServerOutput().Replace(SERVER_PROTOCOL_NO_MSG, "").Trim();
            return serverMessage.Length == 0 ? null : serverMessage;
        }


        private void ReconnectIfDown()
        {
            if (client == null || !client.Connected || client.GetStream() == null)
                Connect();          
        }


        /// <exception cref="ServerUnreachableException">If server is unreachable duringlong time</exception>
        private void Connect()
        {
            for (int port = SERVER_PORT_RANGE_START; port <= SERVER_PORT_RANGE_END; port++)
            {
                try
                {
                    Disconnect();

                    // looking for available network application
                    client = new TcpClient("localhost", port);
                    if (!client.Connected)
                        continue;

                    // wait for server`s respond
                    Thread.Sleep(1000);

                    // checking if this application is our daemon
                    String protocolStart = ReadServerOutput().Trim().ToLower();
                    if (protocolStart.CompareTo(SERVER_PROTOCOL_START) == 0)
                        break;                  
                }
                catch (SocketException)
                {
                    continue;
                }
            }

            // no running server was detected...
            if (!client.Connected)
                throw new ServerUnreachableException();
            
        }


        private void Disconnect()
        {
            if (client.Connected)
            {
                client.GetStream().Close();
                client.Close();
            }
        }


        private String ReadServerOutput()
        {
            ReconnectIfDown();
            Debug.Assert(client.GetStream().CanRead, "Connection is established, but cant read!");

            StringBuilder output = new StringBuilder();
            byte[] msgBuffer = new byte[1024];

            while (client.GetStream().DataAvailable)
            {
                try
                {
                    int read = client.GetStream().Read(msgBuffer, 0, 1024);
                    output.Append(Encoding.UTF8.GetString(msgBuffer, 0, read));
                }
                catch (Exception)
                {
                    break;
                }
            } 

            String serverResult = output.ToString();

            if(serverResult.Contains(SERVER_PROTOCOL_FINISH))
            {
                Debug.Assert(false, "Server sent an end-protocol command? It is almost impossible!");
                ReconnectIfDown();
                return "";
            }
            if (serverResult.Contains(SERVER_PROTOCOL_UNK_CMD))
            {
                Debug.Assert(false, "Server do not know such a command!");
                return "";
            }

            return serverResult;
        }


        private void SendServerInput(String data)
        {
            ReconnectIfDown();
            Debug.Assert(data != null, "Why to server`s stream was passed NULL?");
            Debug.Assert(client.GetStream().CanWrite, "Client could not send the message to available server!");

            try
            {
                byte[] cmd = Encoding.UTF8.GetBytes(data);
                client.GetStream().Write(cmd, 0, cmd.Length);
                client.GetStream().Flush();
            }
            catch (Exception)
            {
                ReconnectIfDown();
            }
        }

    }
}
