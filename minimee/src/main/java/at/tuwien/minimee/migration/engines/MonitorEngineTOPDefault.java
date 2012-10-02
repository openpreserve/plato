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
package at.tuwien.minimee.migration.engines;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.tuwien.minimee.model.ToolConfig;
import at.tuwien.minimee.util.ExecutionFootprintList;
import at.tuwien.minimee.util.LinuxCommandExecutor;
import at.tuwien.minimee.util.TopParser;
import eu.scape_project.planning.model.beans.MigrationResult;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.measurement.MeasureConstants;
import eu.scape_project.planning.model.measurement.Measurement;
import eu.scape_project.planning.model.values.PositiveFloatValue;

/**
 * This migration engine uses the Unix tool <em>top</em> to monitor
 * migration processes on Unix/Linux environments.
 * @author kulovits
 * TODO HK add documentation
 */
public class MonitorEngineTOPDefault extends MiniMeeDefaultMigrationEngine {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    private String monitorScript = "topmonitorcall.sh";
    
    @Override
    protected void cleanup(long time, String inputFile, String outputFile) {
        super.cleanup(time, inputFile, outputFile);
        String workingDir = makeWorkingDirName(time);
        new File(workingDir + "/" + monitorScript).delete();
        new File(workingDir + "/top.log").delete();
    }
    
    
    protected String makeWorkingDirName(long time) {
        return getTempDir() + "profile_" + time;
    }
    
    @Override
    protected String prepareWorkingDirectory(long time) throws Exception {
        
        // assemble the working directory from timestamp
        String workingDirectory = makeWorkingDirName(time);
        
        // create the working directory
        (new File(workingDirectory)).mkdir();
        
        //
        // copy script files
        //
        
        String from, to;
        
        //
        // copy script: monitorcall.sh
        //
        from = "data/scripts/" + monitorScript; 
        to = workingDirectory + "/" + monitorScript;

        copyFile(from, to, workingDirectory);
        
        return workingDirectory;
    }
    
    @Override
    protected String prepareCommand(ToolConfig config, String params, 
            String inputFile, String outputFile, long time) throws Exception {
                
        prepareWorkingDirectory(time);

        // we calculate the timeout for the migration process
        File file = new File(inputFile);        
        long timeout = Math.max((file.length() / (1000000))*6, 120);
        
        String monitoringCmd = prepareMonitoringCommand(time, timeout);
        
        String command = monitoringCmd + " " + config.getTool().getExecutablePath() + " \"" + config.getParams() + " "+ inputFile;
        
        // SPECIAL STUFF, UNLIKELY TO REMAIN HERE:
        if (!config.isNoOutFile()) {
            command = command + " " + outputFile;
        }
        
        command += "\"";
        
        log.debug("TOP MONITORING COMMAND: "+command);
        return command;
    }

    protected String prepareMonitoringCommand(long time, long timeout) {
        return makeWorkingDirName(time) + "/" + monitorScript + " " + makeWorkingDirName(time) + " " + timeout;
    }
    
    /**
     * Copies resource file 'from' from destination 'to' and set execution permission.
     * 
     * @param from
     * @param to
     * @throws Exception
     */
    protected void copyFile(String from, String to, String workingDirectory) throws Exception {
        
        //
        // copy the shell script to the working directory
        //
        URL monitorCallShellScriptUrl = Thread.currentThread().getContextClassLoader().getResource(from);
        File f = new File(monitorCallShellScriptUrl.getFile());
        String directoryPath = f.getAbsolutePath();
        // if the application was created as exploded archive, the absolute path is a real filename
        String fileName = from;
        InputStream in = null;
        if (directoryPath.indexOf(".jar!") > -1) {
            // this class is not in an exploded archive, extract the filename  
            URL urlJar = new URL(directoryPath.substring(
                    directoryPath.indexOf("file:"),
                    directoryPath.indexOf('!')));
            
            JarFile jf = new JarFile(urlJar.getFile());
            JarEntry je = jf.getJarEntry(from);
            fileName = je.getName();
            in = Thread.currentThread()
            .getContextClassLoader().getResourceAsStream(
                    fileName);
        } else {
            in = new FileInputStream(f);
        }
        
                
        File outScriptFile = new File(to);
        
        FileOutputStream fos = new FileOutputStream(outScriptFile);
        int nextChar;
        while ((nextChar = in.read()) != -1)
            fos.write(nextChar);
        
        fos.flush();
        fos.close();

        //
        // This seems kind of hard core, but we have to set execution rights for the shell script, 
        // otherwise we wouldn't be allowed to execute it.
        // The Java-way with FilePermission didn't work for some reason.
        //
        try {
            LinuxCommandExecutor cmdExecutor = new LinuxCommandExecutor();
            cmdExecutor.setWorkingDirectory(workingDirectory);
            
            cmdExecutor.runCommand("chmod 777 " + to);
        } catch(Exception e) {
            throw e;
        }                
    }
    
    protected void collectData(ToolConfig config, long time, MigrationResult result) {
        super.collectData(config, time, result);
        
        TopParser p = new TopParser(makeWorkingDirName(time) + "/top.log");
        p.parse();
        
        ExecutionFootprintList performance = p.getList();
        
        for (Measure measure: getMeasures()) {
            if (!measure.getUri().startsWith("machine:")) {
                Measurement m = new Measurement();
                m.setMeasureId(measure.getUri());
                PositiveFloatValue v = (PositiveFloatValue) measure.getScale().createValue();

                if (measure.getUri().equals(MeasureConstants.ELAPSED_TIME_PER_OBJECT)) {
                    v.setValue(performance.getTotalCpuTimeUsed());
                }
                if (measure.getUri().equals(MigrationResult.MIGRES_MEMORY_GROSS)) {
                    v.setValue(performance.getMaxVirtualMemory());
                }
                if (measure.getUri().equals(MigrationResult.MIGRES_MEMORY_NET)) {
                    v.setValue(performance.getMaxResidentSize());
                }


                if (measure.getUri().equals("performance:averageResidentSize")) {
                    v.setValue(performance.getAverageResidentSize());
                } else if (measure.getUri().equals("performance:averageSharedMemory")) {
                    v.setValue(performance.getAverageSharedMemory());
                } else if (measure.getUri().equals("performance:averageVirtualMemory")) {
                    v.setValue(performance.getAverageVirtualMemory());
                } else if (measure.getUri().equals("performance:maxResidentSize")) {
                    v.setValue(performance.getMaxResidentSize());
                } else if (measure.getUri().equals("performance:maxSharedMemory")) {
                    v.setValue(performance.getMaxSharedMemory());
                } else if (measure.getUri().equals("performance:maxVirtualMemory")) {
                    v.setValue(performance.getMaxVirtualMemory());
                } else if (measure.getUri().equals("performance:totalCpuTimeUsed")) {
                    v.setValue(performance.getTotalCpuTimeUsed());
                }
                m.setValue(v);
                result.getMeasurements().put(measure.getUri(), m);
            }
        }
        
    }

}
