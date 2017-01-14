package detector;

public class LogHandler {


    /*
    * Serious errors
    * */
    public static synchronized void Err(Throwable error)
    {
        if(error!=null && error.getMessage() != null) {
            System.err.println(error.getMessage());
        }
        else {
            System.err.println("Empty log message:");
            error.printStackTrace();
        }

        // During debug mode:
        System.exit(-1);
    }


    /*
    * Not very important messages about errors
    * */
    public static synchronized void Warn(String warning)
    {
        System.out.println(warning);
    }


    /*
    * Just messages about details of program execution flow
    * */
    public static synchronized void Log(String log)
    {
        System.out.println(log);
    }

}
