using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace gui
{
    static class Program
    {
        static Mutex mutex = null;

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            mutex = new Mutex(false, "DaemonGui");
            if (!mutex.WaitOne(0, false))
            {
                mutex.Close();
                mutex = null;
            }

            if (mutex != null)
            {
                Application.EnableVisualStyles();
                Application.SetCompatibleTextRenderingDefault(false);
                Application.Run(new AlertForm());
            }
            else 
            {
                Application.Exit();
            }
        }
    }
}
