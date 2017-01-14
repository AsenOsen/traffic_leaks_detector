package detector.NetwPrimitives;


import detector.DB_OsProcessesInfo;
import detector.OsProcessesPrimitives.NetProcess;

public class Port {


    private int port = 0;


    /*
    * ------------------------------------ Constructors
    * */
    public Port(int value)
    {
        port = value > 0 ? value : 0;
    }
    public Port(Port port)
    {
        this(port==null ? 0 : port.port);
    }
    /*
    * ------------------------------------
    * */


    /*
    * Returns the NetProcess instance who opened this port number
    * */
    public NetProcess getOwnerProcess()
    {
        return DB_OsProcessesInfo.getInstance().getProcessOfPort(this);
    }


    public boolean isValid()
    {
        return port > 0;
    }


    @Override
    public String toString() {
        return port + "";
    }


    @Override
    public int hashCode() {
        return port;
    }


    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Port))
            return false;

        return obj.hashCode() == this.hashCode();
    }
}
