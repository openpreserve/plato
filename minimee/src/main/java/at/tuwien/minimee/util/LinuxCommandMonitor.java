/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package at.tuwien.minimee.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;

public class LinuxCommandMonitor {
    
    private String tempDir = OS.getTmpPath();
    
    private ExecutionFootprintList taskPerformance = null;
    
    private String workingDirectory;
    
    private String monitorShellScript;
    
    private boolean topCalledImplicit = false;
    
    public LinuxCommandMonitor() {
        this.topCalledImplicit = false;
    }
    
    public LinuxCommandMonitor(boolean topCalledImplicit) {
        this.topCalledImplicit = topCalledImplicit;
    }
    
    public void prepareWorkingDirectory() throws Exception {

        // assemble the working directory from timestamp
        workingDirectory = tempDir + "/profile_" + System.nanoTime();
        
        // create the working directory
        (new File(workingDirectory)).mkdir();
        
        //
        // copy the shell script to the working directory
        //
        String monitorCallShellScript = "data/scripts/monitorcall.sh";
        URL monitorCallShellScriptUrl = Thread.currentThread().getContextClassLoader().getResource(monitorCallShellScript);
        
        File inScriptFile = null;
        
        try {
            inScriptFile = new File (monitorCallShellScriptUrl.toURI());
        } catch (URISyntaxException e) {
            throw e;
        }
        
        monitorShellScript = workingDirectory + "/monitorcall.sh";
        
        File outScriptFile = new File(monitorShellScript);                
        
        FileChannel inChannel = new FileInputStream(inScriptFile).getChannel();        
        FileChannel outChannel = new FileOutputStream(outScriptFile).getChannel();
        
        try {
            inChannel.transferTo(0, inChannel.size(),
                    outChannel);            
        } 
        catch (IOException e) {
            throw e;
        }
        finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
        
        //
        // This seems kind of hard core, but we have to set execution rights for the shell script, 
        // otherwise we wouldn't be allowed to execute it.
        // The Java-way with FilePermission didn't work for some reason.
        //
        try {
            LinuxCommandExecutor cmdExecutor = new LinuxCommandExecutor();
            cmdExecutor.runCommand("chmod 777 " + monitorShellScript);
        } catch(Exception e) {
            throw e;
        }
    }
    
    public void monitor (String command) {

        //
        // if no working directory is set, we create one
        //
        try {
            if (workingDirectory == "") {
                prepareWorkingDirectory();
            }
        } catch (Exception e) {
            
        }
        
        LinuxCommandExecutor cmdExecutor = new LinuxCommandExecutor();
        cmdExecutor.setWorkingDirectory(workingDirectory);
        
        String commandLine = "";
        if (topCalledImplicit) {
            commandLine = command;
        } else {
            commandLine = monitorShellScript + " " + workingDirectory + " 0 " + command;
            System.out.println ("to execute: " + commandLine);
        }
        
        try {
            cmdExecutor.runCommand(commandLine);
            
            String error = cmdExecutor.getCommandError();
            String out = cmdExecutor.getCommandOutput();
            
            System.out.println ("OUT: " + out);
            System.out.println("ERR: " + error);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            collectPerformanceValues();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * @throws FileNotFoundException
     */
    
    private void collectPerformanceValues() throws FileNotFoundException {
        
        TopParser topParser = new TopParser(workingDirectory + "/top.log");
        topParser.parse();
        
        taskPerformance = topParser.getList();
    }

    public ExecutionFootprintList getTaskPerformance() {
        return taskPerformance;
    }

    public void setTaskPerformance(ExecutionFootprintList taskPerformance) {
        this.taskPerformance = taskPerformance;
    }

    public boolean isTopCalledImplicit() {
        return topCalledImplicit;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getTempDir() {
        return tempDir;
    }

    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }


}
