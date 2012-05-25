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
package eu.scape_project.planning.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
 
/**
 * Usage of following class can go as ...
 * <P><PRE><CODE>
 *              SysCommandExecutor cmdExecutor = new SysCommandExecutor();
 *              cmdExecutor.setOutputLogDevice(new LogDevice());
 *              cmdExecutor.setErrorLogDevice(new LogDevice());
 *              int exitStatus = cmdExecutor.runCommand(commandLine);
 * </CODE></PRE></P>
 * 
 * OR
 * 
 * <P><PRE><CODE>
 *              SysCommandExecutor cmdExecutor = new SysCommandExecutor();              
 *              int exitStatus = cmdExecutor.runCommand(commandLine);
 * 
 *              String cmdError = cmdExecutor.getCommandError();
 *              String cmdOutput = cmdExecutor.getCommandOutput(); 
 * </CODE></PRE></P> 
 */
public class CommandExecutor
{       
        private ILogDevice fOuputLogDevice = null;
        private ILogDevice fErrorLogDevice = null;
        private String fWorkingDirectory = null;
        private List fEnvironmentVarList = null;
        
        private StringBuffer fCmdOutput = null;
        private StringBuffer fCmdError = null;
        private AsyncStreamReader fCmdOutputThread = null;
        private AsyncStreamReader fCmdErrorThread = null;       
        
        public void setOutputLogDevice(ILogDevice logDevice)
        {
                fOuputLogDevice = logDevice;
        }
        
        public void setErrorLogDevice(ILogDevice logDevice)
        {
                fErrorLogDevice = logDevice;
        }
        
        public void setWorkingDirectory(String workingDirectory) {
                fWorkingDirectory = workingDirectory;
        }
        
        public String getWorkingDirectory() {
            return fWorkingDirectory;
        }

        public void setEnvironmentVar(String name, String value)
        {
                if( fEnvironmentVarList == null )
                        fEnvironmentVarList = new ArrayList();
                
                fEnvironmentVarList.add(new EnvironmentVar(name, value));
        }
        
        public String getCommandOutput() {              
                return fCmdOutput.toString();
        }
        
        public String getCommandError() {
                return fCmdError.toString();
        }
        
        public int runCommand(String commandLine) throws Exception
        {
                /* run command */
                Process process = runCommandHelper(commandLine);
                
                /* start output and error read threads */
                startOutputAndErrorReadThreads(process.getInputStream(), process.getErrorStream());
            
                /* wait for command execution to terminate */
                int exitStatus = -1;
                try {
                        exitStatus = process.waitFor();
                                        
                } catch (Throwable ex) {
                        throw new Exception(ex.getMessage());
                        
                } finally {
                        /* notify output and error read threads to stop reading */
                        notifyOutputAndErrorReadThreadsToStopReading();
                        InputStream in = process.getInputStream();
                        in.close();
                }
                
                return exitStatus;
        }       
        
        protected Process runCommandHelper(String commandLine) throws IOException
        {
            String[] cmdArray = null;
            Process process = null;         
            if (("Linux".compareTo(System.getProperty("os.name")) == 0)|| 
            	("Mac OS X".compareTo(System.getProperty("os.name")) == 0)) {
                cmdArray = new String[] { "/bin/bash", "-c", commandLine };
                if( fWorkingDirectory == null ) {
                    process = Runtime.getRuntime().exec(cmdArray, getEnvTokens());
                } else {
                    process = Runtime.getRuntime().exec(cmdArray, getEnvTokens(), new File(fWorkingDirectory));
                }
            }  else {
            	cmdArray = new String[] {"cmd", "/c", commandLine};
                if( fWorkingDirectory == null ) {
                    process = Runtime.getRuntime().exec(cmdArray);
                } else {
                    process = Runtime.getRuntime().exec(cmdArray, getEnvTokens(), new File(fWorkingDirectory));
                }
            }
                
            return process;
        }
        
        private void startOutputAndErrorReadThreads(InputStream processOut, InputStream processErr)
        {
                fCmdOutput = new StringBuffer();
                fCmdOutputThread = new AsyncStreamReader(processOut, fCmdOutput, fOuputLogDevice, "OUTPUT");            
                fCmdOutputThread.start();
                
                fCmdError = new StringBuffer();
                fCmdErrorThread = new AsyncStreamReader(processErr, fCmdError, fErrorLogDevice, "ERROR");
                fCmdErrorThread.start();
        }
        
        private void notifyOutputAndErrorReadThreadsToStopReading()
        {
                fCmdOutputThread.stopReading();
                fCmdErrorThread.stopReading();
        }
        
        protected String[] getEnvTokens()
        {
                if( fEnvironmentVarList == null )
                        return null;
                
                String[] envTokenArray = new String[fEnvironmentVarList.size()];
                Iterator envVarIter = fEnvironmentVarList.iterator();
                int nEnvVarIndex = 0; 
                while (envVarIter.hasNext() == true)
                {
                        EnvironmentVar envVar = (EnvironmentVar)(envVarIter.next());
                        String envVarToken = envVar.fName + "=" + envVar.fValue;
                        envTokenArray[nEnvVarIndex++] = envVarToken;
                }
                
                return envTokenArray;
        }       
}
 
class AsyncStreamReader extends Thread
{
        private StringBuffer fBuffer = null;
        private InputStream fInputStream = null;
        private String fThreadId = null;
        private boolean fStop = false;
        private ILogDevice fLogDevice = null;
        
        private String fNewLine = null;
        
        public AsyncStreamReader(InputStream inputStream, StringBuffer buffer, ILogDevice logDevice, String threadId)
        {
                fInputStream = inputStream;
                fBuffer = buffer;
                fThreadId = threadId;
                fLogDevice = logDevice;
                
                fNewLine = System.getProperty("line.separator");
        }       
        
        public String getBuffer() {             
                return fBuffer.toString();
        }
        
        public void run()
        {
                try {
                        readCommandOutput();
                } catch (Exception ex) {
                        //ex.printStackTrace(); //DEBUG
                }
        }
        
        private void readCommandOutput() throws IOException
        {               
                BufferedReader bufOut = new BufferedReader(new InputStreamReader(fInputStream));                
                String line = null;
                while ( (fStop == false) && ((line = bufOut.readLine()) != null) )
                {
                        fBuffer.append(line + fNewLine);
                        printToDisplayDevice(line);
                }               
                bufOut.close();
                //printToConsole("END OF: " + fThreadId); //DEBUG
        }
        
        public void stopReading() {
                fStop = true;
        }
        
        private void printToDisplayDevice(String line)
        {
                if( fLogDevice != null )
                        fLogDevice.log(line);
                else
                {
                        printToConsole(line);//DEBUG
                }
        }
        
        private synchronized void printToConsole(String line) {
                System.out.println(line);
        }
}
 
class EnvironmentVar
{
        public String fName = null;
        public String fValue = null;
        
        public EnvironmentVar(String name, String value)
        {
                fName = name;
                fValue = value;
        }
}
 

