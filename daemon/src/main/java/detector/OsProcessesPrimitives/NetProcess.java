package detector.OsProcessesPrimitives;

/*********************************************************************
* This class describes a system process which interacts with network
* *******************************************************************/
public class NetProcess {

    private int pid;
    private String name;
    //private String file;


    public NetProcess(int pid)
    {
        this.pid = pid>-1 ? pid : -1;
    }


    public NetProcess setName(String name)
    {
        this.name = name;
        return this;
    }


    public String getName()
    {
        return name==null ? "<undefined_process>" : name;
    }


    public int getPid()
    {
        return pid;
    }


    @Override
    public int hashCode()
    {
        return pid;
    }


    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof NetProcess))
            return false;

        return obj.hashCode() == this.hashCode();
    }


    @Override
    public String toString()
    {
        return String.format("[%s|pid=%s]", getName(), getPid());
    }
}
