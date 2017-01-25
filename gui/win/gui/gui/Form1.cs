using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace gui
{
    public partial class Form1 : Form
    {

        TcpClient client = null;
        NetworkStream serverStream = null;

        public Form1()
        {
            InitializeComponent();
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            if (client == null)
                return;


            byte[] cmd =Encoding.UTF8.GetBytes("get_alert\n") ;
            serverStream.Write(cmd, 0, cmd.Length);
            String serverMessage = readServerOutput().Trim().ToLower();
            if (serverMessage.CompareTo(":::daemon_protocol_no_msg:::") != 0)
                richTextBox1.AppendText(serverMessage);
            
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            for (int port = 5000; port <= 5025; port++)
            {
                try
                {
                    client = new TcpClient("localhost", port);
                }
                catch (SocketException)
                {
                    continue;
                }

                serverStream = client.GetStream();

                timer1.Start();
                break;
            }
        }


        private String readServerOutput()
        {
            StringBuilder output = new StringBuilder();

            const int SZ = 1024;
            byte[] msgBuffer = new byte[SZ];
            while(serverStream.DataAvailable)
            {
                int read = serverStream.Read(msgBuffer, 0, SZ);
                output.Append( Encoding.UTF8.GetString(msgBuffer, 0, read) );
            }

            return output.ToString();
        }
    }
}
