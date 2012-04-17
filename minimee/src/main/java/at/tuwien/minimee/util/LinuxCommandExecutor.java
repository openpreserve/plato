/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package at.tuwien.minimee.util;

import java.io.File;
import java.io.IOException;

public class LinuxCommandExecutor extends CommandExecutor {

    /**
     * In Linux we have to start a shell before we can execute the commandLine. Furthermore
     * each command must end with carriage return.
     */
    protected Process runCommandHelper(String commandLine) throws IOException {
        Process process = null;
        if (getWorkingDirectory() == null)
            process = Runtime.getRuntime().exec(commandLine, getEnvTokens());
        else {
            File workingDir = new File(getWorkingDirectory());
            workingDir.mkdir();
            
            //
            // -c is important!!!
            String[] cmds = { "/bin/bash", "-c", commandLine };
            
            process = Runtime.getRuntime().exec(cmds , getEnvTokens(), workingDir);
        }

        return process;
    }
    

}
