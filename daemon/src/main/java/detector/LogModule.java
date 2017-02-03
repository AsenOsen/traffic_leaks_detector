package detector;

import java.util.Date;

public class LogModule {


    /*
    * Errors after which application cannot executes anymore
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

        // During debug mode:
        System.exit(-1);
    }


    /*
    * Errors, but after which application can save working state
    * */
    public static synchronized void Warn(String warning)
    {
        assert warning!=null && warning.length()>0 : "Warning message cant be empty!";
        System.out.println("=== WARNING(!) "+warning);
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

}
