﻿using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace gui
{
    public partial class DetailedViewForm : Form
    {
        public DetailedViewForm()
        {
            InitializeComponent();
        }


        public void ShowDetailsFor(ServerMessage messsage)
        {
            messageTable.Text = messsage.getMessage();
            timeTable.Text = messsage.getMessageTime().ToString();
            this.Text = messsage.getLabel();

            this.Show();
        }

    }
}
