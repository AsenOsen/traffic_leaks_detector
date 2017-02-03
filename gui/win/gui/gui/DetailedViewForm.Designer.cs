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
            this.timeBox = new System.Windows.Forms.TextBox();
            this.causeBox = new System.Windows.Forms.TextBox();
            this.SuspendLayout();
            // 
            // messageTable
            // 
            this.messageTable.BackColor = System.Drawing.SystemColors.Window;
            this.messageTable.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.messageTable.Font = new System.Drawing.Font("Arial", 14F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(204)));
            this.messageTable.Location = new System.Drawing.Point(-1, 27);
            this.messageTable.Name = "messageTable";
            this.messageTable.ReadOnly = true;
            this.messageTable.ScrollBars = System.Windows.Forms.RichTextBoxScrollBars.Vertical;
            this.messageTable.Size = new System.Drawing.Size(390, 283);
            this.messageTable.TabIndex = 0;
            this.messageTable.Text = "";
            // 
            // timeBox
            // 
            this.timeBox.Location = new System.Drawing.Point(1, 310);
            this.timeBox.Name = "timeBox";
            this.timeBox.Size = new System.Drawing.Size(388, 20);
            this.timeBox.TabIndex = 1;
            // 
            // causeBox
            // 
            this.causeBox.Location = new System.Drawing.Point(1, 3);
            this.causeBox.Name = "causeBox";
            this.causeBox.Size = new System.Drawing.Size(388, 20);
            this.causeBox.TabIndex = 2;
            // 
            // DetailedViewForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(392, 335);
            this.Controls.Add(this.causeBox);
            this.Controls.Add(this.timeBox);
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
        private System.Windows.Forms.TextBox timeBox;
        private System.Windows.Forms.TextBox causeBox;
    }
}