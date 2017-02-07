package detector.OsProcessesPrimitives;

import org.jetbrains.annotations.Nullable;

/*********************************************************************
* This class describes a system process which interacts with network
* *******************************************************************/
public class NetProcess {

    private Integer pid;
    private String name;
    //private String file;


    public NetProcess(int pid)
    {
        this.pid = pid>-1 ? new Integer(pid) : null;
    }


    public NetProcess setName(String name)
    {
        this.name = name;
        return this;
    }


    @Nullable
    public String getName()
    {
        return name;
    }


    @Nullable
    public Integer getPid()
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
        return String.format(
                "[%s|pid=%s]",
                (name==null ? "<undefined_process>" : name),
                (pid==null ? "<pid_undefined>":pid)
        );
    }
}
