package detector.OsProcessesPrimitives.platforms.routine;


import detector.LogModule;
import detector.OsProcessesPrimitives.ProcessTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class ProcessTableExtractor {


    /*
    * Returns actual process table for a specific platform
    * */
    public abstract ProcessTable getActualTable();


    /*
    * Runs the OS-specific shell command
    * */
    protected StringBuilder runNativeCmd(String cmd)
    {
        StringBuilder output = new StringBuilder();

        Process process;
        try {
            process = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            LogModule.Err(e);
            return output;
        }

        if(process == null)
            return output;

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        try {
            String line;
            while((line = reader.readLine()) != null)
                output.append(line + "\n");
        } catch (IOException e) {
            LogModule.Warn("Error reading CMD output: "+e.getMessage());
        }

        return output;
    }

}
