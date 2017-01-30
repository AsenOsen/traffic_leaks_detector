using BrightIdeasSoftware;
using gui.Properties;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Media;
using System.Net.Sockets;
using System.Reflection;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Controls;
using System.Windows.Forms;

namespace gui
{
    public partial class AlertForm : Form
    {

        ServerConnection client = new ServerConnection();
        Thread connectionThread = null;
        SoundPlayer notificationSound = new SoundPlayer(Resources.notify);


        public AlertForm()
        {
            InitializeComponent();
            objectListView1.PrimarySortColumn = timeColumn;
            objectListView1.PrimarySortOrder = SortOrder.Descending;
            objectListView1.Sort();

            /*List<ServerMessage> messages = new List<ServerMessage>();
            for (int i = 0; i < 10; i++)
            {
                messages.Add(new ServerMessage("{\"user_message\":\"Chpock1Ch pock1Chpock1Chpo ck1Chpock1Chpock1Chpock1Chpo ck1Chpock1Chpock1CChpock1Ch pock1Chpock1Chpo ck1Chpock1Chpock1Chpock1Chpo ck1Chpock1Chpock1CChpock1Ch pock1Chpock1Chpo ck1Chpock1Chpock1Chpock1Chpo ck1Chpock1Chpock1C hpock1Chpock1Chpo ck1Chpock1Ch pock1Chpock1Chpock1Chpock 1Chpock1Ch pock1Chpock1Chpock1Chpock1\", \"label\":\"Chpock_Label1\", \"utc_timestamp\":\"1231314334\"}"));
                messages.Add(new ServerMessage("{\"user_message\":\"Chpock2\", \"label\":\"Chpock_Label2\", \"utc_timestamp\":\"1233131434\"}"));
                messages.Add(new ServerMessage("{\"user_message\":\"Chpock3\", \"label\":\"Chpock_Label3\", \"utc_timestamp\":\"1233131434\"}"));
                messages.Add(new ServerMessage("{\"user_message\":\"Chpock3\", \"label\":\"Chpock_Label3\", \"utc_timestamp\":\"1433131434\"}"));
                messages.Add(new ServerMessage("{\"user_message\":\"Chpock3\", \"label\":\"Chpock_Label3\", \"utc_timestamp\":\"1433131424\"}"));
                messages.Add(new ServerMessage("{\"user_message\":\"Chpock3\", \"label\":\"Chpock_Label3\", \"utc_timestamp\":\"1433131444\"}"));
            }
            objectListView1.Freeze();
            objectListView1.SetObjects(messages);
            objectListView1.Unfreeze();*/
        }


        private void messageChecker_Tick(object sender, EventArgs e)
        {
            if (connectionThread == null || !connectionThread.IsAlive)
            {
                connectionThread = new Thread(RunClientRoutine);
                connectionThread.Start();
            }  
        }


        private void RunClientRoutine()
        {           
            try
            {
                String nextAlert = client.GetMessageFromServer();
                if (nextAlert != null)
                    ProcessNewMessage(nextAlert);
                this.Invoke((MethodInvoker)delegate() { errorLabel.Hide(); });
            }
            catch (ServerUnreachableException)
            {
                this.Invoke((MethodInvoker)delegate() { errorLabel.Show(); });
            }     
        }


        private void ProcessNewMessage(String message)
        {
            ServerMessage newMessage = new ServerMessage(message);
            if (newMessage.isValid())
            {
                this.Invoke((MethodInvoker)delegate() { DisplayMessage(newMessage); });
            } 
        }


        private void DisplayMessage(ServerMessage newMessage)
        {
            String userMessage = newMessage.getMessage();
            String userLabel = newMessage.getLabel();

            // notification
            if (userLabel != null && userMessage != null && newMessage.isActual())
            {
                notifier.ShowBalloonTip(5000, userLabel, userMessage, ToolTipIcon.Info);              
                notificationSound.Play();
            }

            // logging
            if (newMessage.isValid())
            {
                Point scrollPos = objectListView1.LowLevelScrollPosition;

                objectListView1.AddObject(newMessage);
                //objectListView1.Sort(timeColumn, SortOrder.Descending);
                objectListView1.FindItemWithText(userMessage).BackColor = Color.FromArgb(255, 255, 229, 229);
                objectListView1.RedrawItems(0, objectListView1.Items.Count - 1, false);

                if(scrollPos.Y > 0)
                    objectListView1.LowLevelScroll(scrollPos.X, scrollPos.Y + objectListView1.RowHeightEffective);
            }
        }


        private void ShowForm()
        {
            this.Show();
            this.Opacity = 100;
            this.WindowState = FormWindowState.Normal;
            this.TopMost = true;         
        }


        private void HideForm()
        {
            this.Hide();
            this.Opacity = 100;
            this.WindowState = FormWindowState.Minimized;
            this.TopMost = false;           
        }


        private void AlertForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (e.CloseReason == CloseReason.UserClosing)
            {
                HideForm();
                e.Cancel = true;
            }
            else 
            {
                this.Close();
                e.Cancel = false;
            }
        }


        private void notifier_BalloonTipClicked(object sender, EventArgs e)
        {
            ShowForm();
        }


        private void formHider_Tick(object sender, EventArgs e)
        {
            HideForm();
            formHider.Stop();
        }


        private void toolStripMenuItem2_Click(object sender, EventArgs e)
        {
            Application.ExitThread();
        }

        private void showToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ShowForm();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            objectListView1.Items.Clear();
        }


        private void notifier_DoubleClick(object sender, EventArgs e)
        {
            ShowForm();
        }

        private void objectListView1_ButtonClick(object sender, CellClickEventArgs e)
        {
            if (e.ColumnIndex == moreInfoColumn.Index)
                MessageBox.Show(((ServerMessage)e.Model).getMessage());

            if (e.ColumnIndex == ignoreBtnColumn.Index)
                MessageBox.Show("We will!", "Surely", MessageBoxButtons.OK, MessageBoxIcon.Hand);
        }

        private void objectListView1_BeforeSorting(object sender, BeforeSortingEventArgs e)
        {
            e.SecondaryColumnToSort = timeColumn;
            e.SecondarySortOrder = SortOrder.Descending;
        }


        private void AlertForm_Resize(object sender, EventArgs e)
        {
            moreInfoColumn.Width = 100;
            ignoreBtnColumn.Width = 100;

            int leftWidth = Width - (moreInfoColumn.Width + ignoreBtnColumn.Width);

            msgColumn.Width = (int)(leftWidth * 0.5);
            labelColumn.Width = (int)(leftWidth * 0.25);
            timeColumn.Width = (int)(leftWidth * 0.20);

        }


        private void objectListView1_CellOver(object sender, CellOverEventArgs e)
        {
            if (e != null && e.Item != null) 
            { 
                e.Item.BackColor = Color.White;
                objectListView1.RedrawItems(0, objectListView1.Items.Count - 1, false);
            }
        }


    }
}
