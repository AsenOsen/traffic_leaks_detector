package detector;

import java.util.Date;

public class LogModule {


    /*
    * Errors after which application cannot recover
    * */
    public static synchronized void Err(Throwable error)
    {
        assert error != null : "Error message cant be null!";

        if(error!=null && error.getMessage() != null) {
            System.err.println(error.getMessage());
            error.printStackTrace();
        }
        else {
            System.err.println("Empty log message:");
            error.printStackTrace();
        }

        System.exit(-1);
    }


    /*
    * Errors after which application can keep working state
    * */
    public static synchronized void Warn(String warning)
    {
        assert warning!=null && warning.length()>0 : "Warning message cant be empty!";
        System.out.println("=== WARNING(!) "+warning+" === "+getErrorPlace());
    }


    /*
    * Just messages about details of program execution flow
    * */
    public static synchronized void Log(String log)
    {
        String fmt = "[%s]   %s";
        String time = new Date(System.currentTimeMillis()).toString();
        String msg = String.format(fmt, time, log);
        System.out.println(msg);
    }


    /*
    * Searches the log`s action initiator
    * */
    private static synchronized String getErrorPlace()
    {
        try
        {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            // 0 - getStackTract()
            // 1 - getErrorPlace()
            // 2 - Log|Warn|Err
            // 3 - our <initiator>
            return stackTrace[3].toString();
        }
        catch (Exception e) {
            assert false;
            return "Initiator undefined";
        }
    }

}
