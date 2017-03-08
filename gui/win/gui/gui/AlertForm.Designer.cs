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
            this.closeToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.showToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.formHider = new System.Windows.Forms.Timer(this.components);
            this.clearBtn = new System.Windows.Forms.Button();
            this.errorLabel = new System.Windows.Forms.Label();
            this.alertsTable = new BrightIdeasSoftware.ObjectListView();
            this.msgColumn = ((BrightIdeasSoftware.OLVColumn)(new BrightIdeasSoftware.OLVColumn()));
            this.timeColumn = ((BrightIdeasSoftware.OLVColumn)(new BrightIdeasSoftware.OLVColumn()));
            this.leakCauseColumn = ((BrightIdeasSoftware.OLVColumn)(new BrightIdeasSoftware.OLVColumn()));
            this.leakTypeColumn = ((BrightIdeasSoftware.OLVColumn)(new BrightIdeasSoftware.OLVColumn()));
            this.moreInfoColumn = ((BrightIdeasSoftware.OLVColumn)(new BrightIdeasSoftware.OLVColumn()));
            this.ignoreBtnColumn = ((BrightIdeasSoftware.OLVColumn)(new BrightIdeasSoftware.OLVColumn()));
            this.appMenu = new System.Windows.Forms.MenuStrip();
            this.applicationToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.exitToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.helpToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.notifierMenu.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.alertsTable)).BeginInit();
            this.appMenu.SuspendLayout();
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
            this.notifier.Text = "Traffic Leaks";
            this.notifier.Visible = true;
            this.notifier.BalloonTipClicked += new System.EventHandler(this.notifier_BalloonTipClicked);
            this.notifier.BalloonTipShown += new System.EventHandler(this.notifier_BalloonTipShown);
            this.notifier.DoubleClick += new System.EventHandler(this.notifier_DoubleClick);
            // 
            // notifierMenu
            // 
            this.notifierMenu.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.closeToolStripMenuItem,
            this.showToolStripMenuItem});
            this.notifierMenu.Name = "contextMenuStrip1";
            this.notifierMenu.Size = new System.Drawing.Size(104, 48);
            this.notifierMenu.Text = "dg";
            // 
            // closeToolStripMenuItem
            // 
            this.closeToolStripMenuItem.Name = "closeToolStripMenuItem";
            this.closeToolStripMenuItem.Size = new System.Drawing.Size(103, 22);
            this.closeToolStripMenuItem.Text = "Close";
            this.closeToolStripMenuItem.Click += new System.EventHandler(this.toolStripMenuItem2_Click);
            // 
            // showToolStripMenuItem
            // 
            this.showToolStripMenuItem.Name = "showToolStripMenuItem";
            this.showToolStripMenuItem.Size = new System.Drawing.Size(103, 22);
            this.showToolStripMenuItem.Text = "Show";
            this.showToolStripMenuItem.Click += new System.EventHandler(this.showToolStripMenuItem_Click);
            // 
            // formHider
            // 
            this.formHider.Enabled = true;
            this.formHider.Interval = 1;
            this.formHider.Tick += new System.EventHandler(this.formHider_Tick);
            // 
            // clearBtn
            // 
            this.clearBtn.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.clearBtn.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.clearBtn.Location = new System.Drawing.Point(1059, 596);
            this.clearBtn.Name = "clearBtn";
            this.clearBtn.Size = new System.Drawing.Size(88, 23);
            this.clearBtn.TabIndex = 2;
            this.clearBtn.Text = "Clear";
            this.clearBtn.UseVisualStyleBackColor = true;
            this.clearBtn.Click += new System.EventHandler(this.button1_Click);
            // 
            // errorLabel
            // 
            this.errorLabel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.errorLabel.AutoSize = true;
            this.errorLabel.BackColor = System.Drawing.Color.Red;
            this.errorLabel.Font = new System.Drawing.Font("Arial", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.errorLabel.Location = new System.Drawing.Point(-3, 598);
            this.errorLabel.Name = "errorLabel";
            this.errorLabel.Padding = new System.Windows.Forms.Padding(5);
            this.errorLabel.Size = new System.Drawing.Size(439, 24);
            this.errorLabel.TabIndex = 3;
            this.errorLabel.Text = "Connection to server is lost. Can not monitor traffic. Restart the application..." +
    "";
            this.errorLabel.Visible = false;
            // 
            // alertsTable
            // 
            this.alertsTable.AllColumns.Add(this.msgColumn);
            this.alertsTable.AllColumns.Add(this.timeColumn);
            this.alertsTable.AllColumns.Add(this.leakCauseColumn);
            this.alertsTable.AllColumns.Add(this.leakTypeColumn);
            this.alertsTable.AllColumns.Add(this.moreInfoColumn);
            this.alertsTable.AllColumns.Add(this.ignoreBtnColumn);
            this.alertsTable.AlternateRowBackColor = System.Drawing.Color.FromArgb(((int)(((byte)(192)))), ((int)(((byte)(192)))), ((int)(((byte)(255)))));
            this.alertsTable.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.alertsTable.CellEditActivation = BrightIdeasSoftware.ObjectListView.CellEditActivateMode.DoubleClick;
            this.alertsTable.CellEditEnterChangesRows = true;
            this.alertsTable.CellEditUseWholeCell = false;
            this.alertsTable.CheckedAspectName = "";
            this.alertsTable.Columns.AddRange(new System.Windows.Forms.ColumnHeader[] {
            this.msgColumn,
            this.timeColumn,
            this.leakCauseColumn,
            this.leakTypeColumn,
            this.moreInfoColumn,
            this.ignoreBtnColumn});
            this.alertsTable.Cursor = System.Windows.Forms.Cursors.Default;
            this.alertsTable.Font = new System.Drawing.Font("Arial Unicode MS", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.alertsTable.HeaderWordWrap = true;
            this.alertsTable.Location = new System.Drawing.Point(0, 27);
            this.alertsTable.Name = "alertsTable";
            this.alertsTable.RowHeight = 65;
            this.alertsTable.SelectAllOnControlA = false;
            this.alertsTable.SelectColumnsOnRightClick = false;
            this.alertsTable.SelectColumnsOnRightClickBehaviour = BrightIdeasSoftware.ObjectListView.ColumnSelectBehaviour.None;
            this.alertsTable.ShowItemToolTips = true;
            this.alertsTable.Size = new System.Drawing.Size(1147, 568);
            this.alertsTable.SortGroupItemsByPrimaryColumn = false;
            this.alertsTable.Sorting = System.Windows.Forms.SortOrder.Descending;
            this.alertsTable.TabIndex = 1;
            this.alertsTable.UseCompatibleStateImageBehavior = false;
            this.alertsTable.UseFilterIndicator = true;
            this.alertsTable.UseHotControls = false;
            this.alertsTable.View = System.Windows.Forms.View.Details;
            this.alertsTable.BeforeSorting += new System.EventHandler<BrightIdeasSoftware.BeforeSortingEventArgs>(this.objectListView1_BeforeSorting);
            this.alertsTable.ButtonClick += new System.EventHandler<BrightIdeasSoftware.CellClickEventArgs>(this.objectListView1_ButtonClick);
            this.alertsTable.CellOver += new System.EventHandler<BrightIdeasSoftware.CellOverEventArgs>(this.objectListView1_CellOver);
            // 
            // msgColumn
            // 
            this.msgColumn.AspectName = "getMessage";
            this.msgColumn.Groupable = false;
            this.msgColumn.HeaderFont = new System.Drawing.Font("Arial Narrow", 11.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.msgColumn.HeaderForeColor = System.Drawing.Color.Black;
            this.msgColumn.HeaderTextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.msgColumn.Hideable = false;
            this.msgColumn.MinimumWidth = 400;
            this.msgColumn.Searchable = false;
            this.msgColumn.Sortable = false;
            this.msgColumn.Text = "Leak Message";
            this.msgColumn.ToolTipText = "";
            this.msgColumn.UseFiltering = false;
            this.msgColumn.Width = 400;
            this.msgColumn.WordWrap = true;
            // 
            // timeColumn
            // 
            this.timeColumn.AspectName = "getMessageTime";
            this.timeColumn.CellVerticalAlignment = System.Drawing.StringAlignment.Center;
            this.timeColumn.HeaderFont = new System.Drawing.Font("Arial Narrow", 11.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.timeColumn.HeaderForeColor = System.Drawing.Color.Black;
            this.timeColumn.HeaderTextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.timeColumn.MinimumWidth = 100;
            this.timeColumn.Text = "Time";
            this.timeColumn.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.timeColumn.Width = 100;
            this.timeColumn.WordWrap = true;
            // 
            // leakCauseColumn
            // 
            this.leakCauseColumn.AspectName = "getCause";
            this.leakCauseColumn.HeaderFont = new System.Drawing.Font("Arial Narrow", 11.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.leakCauseColumn.HeaderForeColor = System.Drawing.Color.Black;
            this.leakCauseColumn.HeaderTextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.leakCauseColumn.IsEditable = false;
            this.leakCauseColumn.MinimumWidth = 100;
            this.leakCauseColumn.Text = "Cause";
            this.leakCauseColumn.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.leakCauseColumn.Width = 180;
            this.leakCauseColumn.WordWrap = true;
            // 
            // leakTypeColumn
            // 
            this.leakTypeColumn.AspectName = "getLeakType";
            this.leakTypeColumn.HeaderFont = new System.Drawing.Font("Arial Narrow", 11.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.leakTypeColumn.HeaderForeColor = System.Drawing.Color.Black;
            this.leakTypeColumn.HeaderTextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.leakTypeColumn.IsEditable = false;
            this.leakTypeColumn.MinimumWidth = 100;
            this.leakTypeColumn.Text = "Kind";
            this.leakTypeColumn.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.leakTypeColumn.Width = 180;
            // 
            // moreInfoColumn
            // 
            this.moreInfoColumn.AspectName = "More...";
            this.moreInfoColumn.AspectToStringFormat = "More...";
            this.moreInfoColumn.HeaderTextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.moreInfoColumn.IsButton = true;
            this.moreInfoColumn.MaximumWidth = 100;
            this.moreInfoColumn.MinimumWidth = 100;
            this.moreInfoColumn.Searchable = false;
            this.moreInfoColumn.ShowTextInHeader = false;
            this.moreInfoColumn.Sortable = false;
            this.moreInfoColumn.Text = "MoreBtn";
            this.moreInfoColumn.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.moreInfoColumn.UseFiltering = false;
            this.moreInfoColumn.Width = 100;
            // 
            // ignoreBtnColumn
            // 
            this.ignoreBtnColumn.AspectName = "Ignore";
            this.ignoreBtnColumn.AspectToStringFormat = "Ignore";
            this.ignoreBtnColumn.HeaderTextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.ignoreBtnColumn.IsButton = true;
            this.ignoreBtnColumn.MaximumWidth = 100;
            this.ignoreBtnColumn.MinimumWidth = 100;
            this.ignoreBtnColumn.Searchable = false;
            this.ignoreBtnColumn.ShowTextInHeader = false;
            this.ignoreBtnColumn.Sortable = false;
            this.ignoreBtnColumn.Text = "IgnoreBtb";
            this.ignoreBtnColumn.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.ignoreBtnColumn.UseFiltering = false;
            this.ignoreBtnColumn.Width = 100;
            // 
            // appMenu
            // 
            this.appMenu.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.applicationToolStripMenuItem,
            this.helpToolStripMenuItem});
            this.appMenu.Location = new System.Drawing.Point(0, 0);
            this.appMenu.Name = "appMenu";
            this.appMenu.Size = new System.Drawing.Size(1148, 24);
            this.appMenu.TabIndex = 5;
            this.appMenu.Text = "menuStrip2";
            // 
            // applicationToolStripMenuItem
            // 
            this.applicationToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.exitToolStripMenuItem});
            this.applicationToolStripMenuItem.Name = "applicationToolStripMenuItem";
            this.applicationToolStripMenuItem.Size = new System.Drawing.Size(80, 20);
            this.applicationToolStripMenuItem.Text = "Application";
            // 
            // exitToolStripMenuItem
            // 
            this.exitToolStripMenuItem.Name = "exitToolStripMenuItem";
            this.exitToolStripMenuItem.Size = new System.Drawing.Size(92, 22);
            this.exitToolStripMenuItem.Text = "Exit";
            this.exitToolStripMenuItem.Click += new System.EventHandler(this.exitToolStripMenuItem_Click);
            // 
            // helpToolStripMenuItem
            // 
            this.helpToolStripMenuItem.Name = "helpToolStripMenuItem";
            this.helpToolStripMenuItem.Size = new System.Drawing.Size(44, 20);
            this.helpToolStripMenuItem.Text = "Help";
            this.helpToolStripMenuItem.Click += new System.EventHandler(this.feedbackToolStripMenuItem_Click);
            // 
            // AlertForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1148, 620);
            this.Controls.Add(this.appMenu);
            this.Controls.Add(this.errorLabel);
            this.Controls.Add(this.clearBtn);
            this.Controls.Add(this.alertsTable);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "AlertForm";
            this.Opacity = 0.01D;
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "Traffic Theft Logs";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.AlertForm_FormClosing);
            this.Resize += new System.EventHandler(this.AlertForm_Resize);
            this.notifierMenu.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.alertsTable)).EndInit();
            this.appMenu.ResumeLayout(false);
            this.appMenu.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Timer messageChecker;
        private System.Windows.Forms.NotifyIcon notifier;
        private System.Windows.Forms.ContextMenuStrip notifierMenu;
        private System.Windows.Forms.ToolStripMenuItem closeToolStripMenuItem;
        private System.Windows.Forms.Timer formHider;
        private System.Windows.Forms.ToolStripMenuItem showToolStripMenuItem;
        private BrightIdeasSoftware.ObjectListView alertsTable;
        private BrightIdeasSoftware.OLVColumn msgColumn;
        private BrightIdeasSoftware.OLVColumn leakCauseColumn;
        private BrightIdeasSoftware.OLVColumn timeColumn;
        private BrightIdeasSoftware.OLVColumn ignoreBtnColumn;
        private System.Windows.Forms.Button clearBtn;
        private BrightIdeasSoftware.OLVColumn moreInfoColumn;
        private System.Windows.Forms.Label errorLabel;
        private BrightIdeasSoftware.OLVColumn leakTypeColumn;
        private System.Windows.Forms.MenuStrip appMenu;
        private System.Windows.Forms.ToolStripMenuItem applicationToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem helpToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem exitToolStripMenuItem;
    }
}

