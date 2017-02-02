namespace gui
{
    partial class DetailedViewForm
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
            this.messageTable = new System.Windows.Forms.RichTextBox();
            this.timeTable = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // messageTable
            // 
            this.messageTable.BackColor = System.Drawing.SystemColors.Window;
            this.messageTable.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.messageTable.Font = new System.Drawing.Font("Arial", 14F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.messageTable.Location = new System.Drawing.Point(0, 1);
            this.messageTable.Name = "messageTable";
            this.messageTable.ReadOnly = true;
            this.messageTable.ScrollBars = System.Windows.Forms.RichTextBoxScrollBars.Vertical;
            this.messageTable.Size = new System.Drawing.Size(390, 283);
            this.messageTable.TabIndex = 0;
            this.messageTable.Text = "hjfgjhfd fdh fy rey rey rey re";
            // 
            // timeTable
            // 
            this.timeTable.AutoSize = true;
            this.timeTable.Location = new System.Drawing.Point(12, 287);
            this.timeTable.Name = "timeTable";
            this.timeTable.Size = new System.Drawing.Size(35, 13);
            this.timeTable.TabIndex = 1;
            this.timeTable.Text = "label1";
            this.timeTable.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // DetailedViewForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(392, 301);
            this.Controls.Add(this.timeTable);
            this.Controls.Add(this.messageTable);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedToolWindow;
            this.Name = "DetailedViewForm";
            this.ShowIcon = false;
            this.ShowInTaskbar = false;
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.TopMost = true;
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.RichTextBox messageTable;
        private System.Windows.Forms.Label timeTable;
    }
}