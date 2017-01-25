namespace gui
{
    partial class AlertForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(AlertForm));
            this.messageChecker = new System.Windows.Forms.Timer(this.components);
            this.notifier = new System.Windows.Forms.NotifyIcon(this.components);
            this.notifierMenu = new System.Windows.Forms.ContextMenuStrip(this.components);
            this.toolStripMenuItem2 = new System.Windows.Forms.ToolStripMenuItem();
            this.formHider = new System.Windows.Forms.Timer(this.components);
            this.notifierMenu.SuspendLayout();
            this.SuspendLayout();
            // 
            // messageChecker
            // 
            this.messageChecker.Enabled = true;
            this.messageChecker.Interval = 500;
            this.messageChecker.Tick += new System.EventHandler(this.messageChecker_Tick);
            // 
            // notifier
            // 
            this.notifier.ContextMenuStrip = this.notifierMenu;
            this.notifier.Icon = ((System.Drawing.Icon)(resources.GetObject("notifier.Icon")));
            this.notifier.Text = "Traffic Alert";
            this.notifier.Visible = true;
            this.notifier.BalloonTipClicked += new System.EventHandler(this.notifier_BalloonTipClicked);
            this.notifier.Click += new System.EventHandler(this.notifier_Click);
            // 
            // notifierMenu
            // 
            this.notifierMenu.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripMenuItem2});
            this.notifierMenu.Name = "contextMenuStrip1";
            this.notifierMenu.Size = new System.Drawing.Size(129, 26);
            // 
            // toolStripMenuItem2
            // 
            this.toolStripMenuItem2.Name = "toolStripMenuItem2";
            this.toolStripMenuItem2.Size = new System.Drawing.Size(128, 22);
            this.toolStripMenuItem2.Text = "Terminate";
            this.toolStripMenuItem2.Click += new System.EventHandler(this.toolStripMenuItem2_Click);
            // 
            // formHider
            // 
            this.formHider.Enabled = true;
            this.formHider.Interval = 1;
            this.formHider.Tick += new System.EventHandler(this.formHider_Tick);
            // 
            // AlertForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(338, 292);
            this.Name = "AlertForm";
            this.Opacity = 0.01D;
            this.Text = "Alerter";
            this.TopMost = true;
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.AlertForm_FormClosing);
            this.notifierMenu.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Timer messageChecker;
        private System.Windows.Forms.NotifyIcon notifier;
        private System.Windows.Forms.ContextMenuStrip notifierMenu;
        private System.Windows.Forms.ToolStripMenuItem toolStripMenuItem2;
        private System.Windows.Forms.Timer formHider;
    }
}

