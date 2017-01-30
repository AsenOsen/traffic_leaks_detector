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
            this.showToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.formHider = new System.Windows.Forms.Timer(this.components);
            this.button1 = new System.Windows.Forms.Button();
            this.objectListView1 = new BrightIdeasSoftware.ObjectListView();
            this.msgColumn = ((BrightIdeasSoftware.OLVColumn)(new BrightIdeasSoftware.OLVColumn()));
            this.timeColumn = ((BrightIdeasSoftware.OLVColumn)(new BrightIdeasSoftware.OLVColumn()));
            this.labelColumn = ((BrightIdeasSoftware.OLVColumn)(new BrightIdeasSoftware.OLVColumn()));
            this.moreInfoColumn = ((BrightIdeasSoftware.OLVColumn)(new BrightIdeasSoftware.OLVColumn()));
            this.ignoreBtnColumn = ((BrightIdeasSoftware.OLVColumn)(new BrightIdeasSoftware.OLVColumn()));
            this.simpleItemStyle1 = new BrightIdeasSoftware.SimpleItemStyle();
            this.errorLabel = new System.Windows.Forms.Label();
            this.notifierMenu.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.objectListView1)).BeginInit();
            this.SuspendLayout();
            // 
            // messageChecker
            // 
            this.messageChecker.Enabled = true;
            this.messageChecker.Interval = 1000;
            this.messageChecker.Tick += new System.EventHandler(this.messageChecker_Tick);
            // 
            // notifier
            // 
            this.notifier.ContextMenuStrip = this.notifierMenu;
            this.notifier.Icon = ((System.Drawing.Icon)(resources.GetObject("notifier.Icon")));
            this.notifier.Text = "Traffic Alert";
            this.notifier.Visible = true;
            this.notifier.BalloonTipClicked += new System.EventHandler(this.notifier_BalloonTipClicked);
            this.notifier.DoubleClick += new System.EventHandler(this.notifier_DoubleClick);
            // 
            // notifierMenu
            // 
            this.notifierMenu.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripMenuItem2,
            this.showToolStripMenuItem});
            this.notifierMenu.Name = "contextMenuStrip1";
            this.notifierMenu.Size = new System.Drawing.Size(129, 48);
            // 
            // toolStripMenuItem2
            // 
            this.toolStripMenuItem2.Name = "toolStripMenuItem2";
            this.toolStripMenuItem2.Size = new System.Drawing.Size(128, 22);
            this.toolStripMenuItem2.Text = "Terminate";
            this.toolStripMenuItem2.Click += new System.EventHandler(this.toolStripMenuItem2_Click);
            // 
            // showToolStripMenuItem
            // 
            this.showToolStripMenuItem.Name = "showToolStripMenuItem";
            this.showToolStripMenuItem.Size = new System.Drawing.Size(128, 22);
            this.showToolStripMenuItem.Text = "Show";
            this.showToolStripMenuItem.Click += new System.EventHandler(this.showToolStripMenuItem_Click);
            // 
            // formHider
            // 
            this.formHider.Enabled = true;
            this.formHider.Interval = 1;
            this.formHider.Tick += new System.EventHandler(this.formHider_Tick);
            // 
            // button1
            // 
            this.button1.Anchor = System.Windows.Forms.AnchorStyles.Bottom;
            this.button1.Location = new System.Drawing.Point(547, 502);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(88, 31);
            this.button1.TabIndex = 2;
            this.button1.Text = "Clear!";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            // 
            // objectListView1
            // 
            this.objectListView1.AllColumns.Add(this.msgColumn);
            this.objectListView1.AllColumns.Add(this.timeColumn);
            this.objectListView1.AllColumns.Add(this.labelColumn);
            this.objectListView1.AllColumns.Add(this.moreInfoColumn);
            this.objectListView1.AllColumns.Add(this.ignoreBtnColumn);
            this.objectListView1.AlternateRowBackColor = System.Drawing.Color.FromArgb(((int)(((byte)(192)))), ((int)(((byte)(192)))), ((int)(((byte)(255)))));
            this.objectListView1.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.objectListView1.CellEditActivation = BrightIdeasSoftware.ObjectListView.CellEditActivateMode.DoubleClick;
            this.objectListView1.CellEditEnterChangesRows = true;
            this.objectListView1.CellEditUseWholeCell = false;
            this.objectListView1.CheckedAspectName = "";
            this.objectListView1.Columns.AddRange(new System.Windows.Forms.ColumnHeader[] {
            this.msgColumn,
            this.timeColumn,
            this.labelColumn,
            this.moreInfoColumn,
            this.ignoreBtnColumn});
            this.objectListView1.Cursor = System.Windows.Forms.Cursors.Default;
            this.objectListView1.Font = new System.Drawing.Font("Arial Unicode MS", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.objectListView1.HeaderWordWrap = true;
            this.objectListView1.Location = new System.Drawing.Point(0, 0);
            this.objectListView1.Name = "objectListView1";
            this.objectListView1.RowHeight = 50;
            this.objectListView1.SelectAllOnControlA = false;
            this.objectListView1.SelectColumnsOnRightClick = false;
            this.objectListView1.SelectColumnsOnRightClickBehaviour = BrightIdeasSoftware.ObjectListView.ColumnSelectBehaviour.None;
            this.objectListView1.ShowItemToolTips = true;
            this.objectListView1.Size = new System.Drawing.Size(1159, 483);
            this.objectListView1.SortGroupItemsByPrimaryColumn = false;
            this.objectListView1.Sorting = System.Windows.Forms.SortOrder.Descending;
            this.objectListView1.TabIndex = 1;
            this.objectListView1.UseCompatibleStateImageBehavior = false;
            this.objectListView1.UseFilterIndicator = true;
            this.objectListView1.UseHotControls = false;
            this.objectListView1.View = System.Windows.Forms.View.Details;
            this.objectListView1.BeforeSorting += new System.EventHandler<BrightIdeasSoftware.BeforeSortingEventArgs>(this.objectListView1_BeforeSorting);
            this.objectListView1.ButtonClick += new System.EventHandler<BrightIdeasSoftware.CellClickEventArgs>(this.objectListView1_ButtonClick);
            this.objectListView1.CellOver += new System.EventHandler<BrightIdeasSoftware.CellOverEventArgs>(this.objectListView1_CellOver);
            // 
            // msgColumn
            // 
            this.msgColumn.AspectName = "getMessage";
            this.msgColumn.Groupable = false;
            this.msgColumn.HeaderFont = new System.Drawing.Font("Microsoft Sans Serif", 11.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.msgColumn.HeaderForeColor = System.Drawing.Color.Black;
            this.msgColumn.HeaderTextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.msgColumn.Hideable = false;
            this.msgColumn.MinimumWidth = 500;
            this.msgColumn.Searchable = false;
            this.msgColumn.Sortable = false;
            this.msgColumn.Text = "Leak Message";
            this.msgColumn.ToolTipText = "";
            this.msgColumn.UseFiltering = false;
            this.msgColumn.Width = 500;
            this.msgColumn.WordWrap = true;
            // 
            // timeColumn
            // 
            this.timeColumn.AspectName = "getMessageTime";
            this.timeColumn.CellVerticalAlignment = System.Drawing.StringAlignment.Center;
            this.timeColumn.HeaderFont = new System.Drawing.Font("Microsoft Sans Serif", 11.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.timeColumn.HeaderForeColor = System.Drawing.Color.Black;
            this.timeColumn.HeaderTextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.timeColumn.MinimumWidth = 150;
            this.timeColumn.Text = "Time";
            this.timeColumn.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.timeColumn.Width = 219;
            this.timeColumn.WordWrap = true;
            // 
            // labelColumn
            // 
            this.labelColumn.AspectName = "getLabel";
            this.labelColumn.HeaderFont = new System.Drawing.Font("Microsoft Sans Serif", 11.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.labelColumn.HeaderForeColor = System.Drawing.Color.Black;
            this.labelColumn.HeaderTextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.labelColumn.IsEditable = false;
            this.labelColumn.MinimumWidth = 150;
            this.labelColumn.Text = "Kind";
            this.labelColumn.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.labelColumn.Width = 155;
            this.labelColumn.WordWrap = true;
            // 
            // moreInfoColumn
            // 
            this.moreInfoColumn.AspectName = "More...";
            this.moreInfoColumn.AspectToStringFormat = "More...";
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
            this.ignoreBtnColumn.IsEditable = false;
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
            // errorLabel
            // 
            this.errorLabel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.errorLabel.AutoSize = true;
            this.errorLabel.BackColor = System.Drawing.Color.Red;
            this.errorLabel.Location = new System.Drawing.Point(-3, 523);
            this.errorLabel.Name = "errorLabel";
            this.errorLabel.Padding = new System.Windows.Forms.Padding(5);
            this.errorLabel.Size = new System.Drawing.Size(374, 23);
            this.errorLabel.TabIndex = 3;
            this.errorLabel.Text = "Connection to server is lost. Can not monitor traffic. Restart the application..." +
    "";
            this.errorLabel.Visible = false;
            // 
            // AlertForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1160, 545);
            this.Controls.Add(this.errorLabel);
            this.Controls.Add(this.button1);
            this.Controls.Add(this.objectListView1);
            this.Name = "AlertForm";
            this.Opacity = 0.01D;
            this.Text = "Alert Log";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.AlertForm_FormClosing);
            this.Resize += new System.EventHandler(this.AlertForm_Resize);
            this.notifierMenu.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.objectListView1)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Timer messageChecker;
        private System.Windows.Forms.NotifyIcon notifier;
        private System.Windows.Forms.ContextMenuStrip notifierMenu;
        private System.Windows.Forms.ToolStripMenuItem toolStripMenuItem2;
        private System.Windows.Forms.Timer formHider;
        private System.Windows.Forms.ToolStripMenuItem showToolStripMenuItem;
        private BrightIdeasSoftware.ObjectListView objectListView1;
        private BrightIdeasSoftware.OLVColumn msgColumn;
        private BrightIdeasSoftware.OLVColumn labelColumn;
        private BrightIdeasSoftware.OLVColumn timeColumn;
        private BrightIdeasSoftware.OLVColumn ignoreBtnColumn;
        private BrightIdeasSoftware.SimpleItemStyle simpleItemStyle1;
        private System.Windows.Forms.Button button1;
        private BrightIdeasSoftware.OLVColumn moreInfoColumn;
        private System.Windows.Forms.Label errorLabel;
    }
}

