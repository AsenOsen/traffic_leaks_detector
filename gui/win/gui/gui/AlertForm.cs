using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace gui
{
    public partial class AlertForm : Form
    {

        ClientConnection client = new ClientConnection();
        ServerMessage newMessage = new ServerMessage();


        public AlertForm()
        {
            InitializeComponent();
        }


        private void messageChecker_Tick(object sender, EventArgs e)
        {
            if(!client.isConnected())
                client.ConnectToServer();

            String nextAlert = client.GetMessageFromServer();
            if (nextAlert != null)
                ProcessNewMessage(nextAlert);
        }


        private void ProcessNewMessage(String message)
        {
            if (newMessage.Parse(message))
                DisplayMessage(newMessage.getMessage(), newMessage.getLabel());
        }


        private void DisplayMessage(String userMessage, String userLabel)
        {
            if (userLabel == null || userMessage == null)
                return;

            notifier.ShowBalloonTip(5000, userLabel, userMessage, ToolTipIcon.Info);
        }


        private void AlertForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            this.Hide();
            e.Cancel = true;
        }


        private void notifier_BalloonTipClicked(object sender, EventArgs e)
        {
            this.Opacity = 100;
            this.WindowState = FormWindowState.Normal;
            this.Show();
        }


        private void formHider_Tick(object sender, EventArgs e)
        {
            Hide();
            formHider.Stop();
        }


        private void notifier_Click(object sender, EventArgs e)
        {
            notifier_BalloonTipClicked(sender, e);
        }


        private void toolStripMenuItem2_Click(object sender, EventArgs e)
        {
            Application.ExitThread();
        }

    }
}
