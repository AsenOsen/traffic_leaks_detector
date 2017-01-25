using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace gui
{
    class ClientConnection
    {
        private const int SERVER_PORT_RANGE_START = 5000;
        private const int SERVER_PORT_RANGE_END = 5025;
        private const string SERVER_PROTOCOL_START = ":::daemon_protocol_start:::";
        private const string SERVER_PROTOCOL_NO_MSG = ":::daemon_protocol_no_msg:::";

        private TcpClient client = new TcpClient();
        private NetworkStream serverStream = null;


        public ClientConnection()
        {
 
        }


        public bool isConnected()
        {
            return client.Connected;
        }


        public void ConnectToServer()
        {
            if (isConnected())
                client.Close();

            for (int port = SERVER_PORT_RANGE_START; port <= SERVER_PORT_RANGE_END; port++)
            {
                try
                {
                    client.Connect("localhost", port);
                    serverStream = client.GetStream();

                    String protocolStart = ReadServerOutput().Trim().ToLower();
                    if (protocolStart.CompareTo(SERVER_PROTOCOL_START) == 0)  
                        break;
                }
                catch (SocketException)
                {
                    continue;
                }
            }
        }


        public String GetMessageFromServer()
        {
            SendServerInput("get_alert\n");
            String serverMessage = ReadServerOutput().Replace(SERVER_PROTOCOL_NO_MSG, "").Trim();
            return serverMessage.Length == 0 ? null : serverMessage; 
        }


        private String ReadServerOutput()
        {
            if (serverStream == null || !serverStream.DataAvailable)
                return "";

            StringBuilder output = new StringBuilder();
            const int SZ = 1024;
            byte[] msgBuffer = new byte[SZ];

            while (serverStream.DataAvailable)
            {
                int read = serverStream.Read(msgBuffer, 0, SZ);
                output.Append(Encoding.UTF8.GetString(msgBuffer, 0, read));
            }

            return output.ToString();
        }


        private void SendServerInput(String data)
        {
            byte[] cmd = Encoding.UTF8.GetBytes(data);
            serverStream.Write(cmd, 0, cmd.Length);
        }

    }
}
