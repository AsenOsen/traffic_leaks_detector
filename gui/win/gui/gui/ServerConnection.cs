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
        private const string SERVER_PROTOCOL_PINGED = ":::daemon_protocol_pinged:::";
        private const string SERVER_PROTOCOL_FINISH = ":::daemon_protocol_finish:::";
        private const string SERVER_PROTOCOL_UNK_CMD = ":::daemon_protocol_unknown_command:::";
        private readonly Encoding SERVER_CHARSET = Encoding.UTF8;
        // end contract //

        private TcpClient client = new TcpClient();


        public ServerConnection()
        {
 
        }


        public String GetMessageFromServer()
        {
            
            if (!isConnectionEstablished())
                Connect();

            Ping();
            SendServerInput("get_alert\n");
            String serverMessage = ReadServerOutput().Replace(SERVER_PROTOCOL_NO_MSG, "").Trim();
            return serverMessage.Length == 0 ? null : serverMessage;
        }


        public void IgnoreAlertTemporary(ServerMessage alert)
        {
            if (!isConnectionEstablished())
                Connect();

            Ping();
            String callback = alert.getCallbackFilter();
            SendServerInput("ignore_tmp\n" + callback + "\n");
        }


        public void IgnoreAlertPermanently(ServerMessage alert)
        {
            if (!isConnectionEstablished())
                Connect();

            Ping();
            String callback = alert.getCallbackFilter();
            SendServerInput("ignore_permanent\n" + callback + "\n");
        }


        private void Ping()
        {
            SendServerInput("ping\n");
            string serverPong = ReadServerOutput().Trim();
            bool ponged = serverPong.Contains(SERVER_PROTOCOL_PINGED);

            if (!ponged)
                Connect();
        }


        private bool isConnectionEstablished()
        {
            return
                client != null &&
                client.Connected &&
                client.GetStream() != null;
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

                    // set the limit time of waiting data from server
                    client.GetStream().ReadTimeout  = 1000;
                    client.GetStream().WriteTimeout = 1000;

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
            if (client != null && client.Connected)
            {
                client.GetStream().Close();
                client.Close();
            }
        }


        private String ReadServerOutput()
        {
            if (!isConnectionEstablished())
                return "";

            StringBuilder output = new StringBuilder();
            const int BlockSize = 1024;
            byte[] msgBuffer = new byte[BlockSize];

            while (true)
            {
                try
                {
                    int read = client.GetStream().Read(msgBuffer, 0, BlockSize);
                    output.Append(SERVER_CHARSET.GetString(msgBuffer, 0, read));

                    if (read < BlockSize)
                        break;
                }
                catch (Exception)
                {
                    Disconnect();
                    break;
                }
            }

            String serverResult = output.ToString();

            if(serverResult.Contains(SERVER_PROTOCOL_FINISH))
            {
                Debug.Assert(false, "Server sent an end-protocol command? It is almost impossible!");
                return "";
            }
            if (serverResult.Contains(SERVER_PROTOCOL_UNK_CMD))
            {
                Debug.Assert(false, "Server do not know such a command!?");
                return "";
            }

            return serverResult;
        }


        private void SendServerInput(String data)
        {
            Debug.Assert(data != null, "Why to server`s stream was passed NULL?");

            if (!isConnectionEstablished())
                return;

            try
            {
                byte[] cmd = SERVER_CHARSET.GetBytes(data);
                client.GetStream().Write(cmd, 0, cmd.Length);
                client.GetStream().Flush();
            }
            catch (Exception)
            {
                Debug.Assert(false, "Could not sent data to server");
                Disconnect();
            }
        }

    }
}
