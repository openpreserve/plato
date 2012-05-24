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
import eu.scape_project.planning.model.measurement.MeasurableProperty;
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
    
    //protected LinuxCommandMonitor monitor = new LinuxCommandMonitor();
    
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
        //log.debug(performance.toString());
        
        for (MeasurableProperty property: getMeasurableProperties()) {
            if (!property.getName().startsWith("machine:")) {
                Measurement m = new Measurement();
                m.setProperty(property);
                PositiveFloatValue v = (PositiveFloatValue) property.getScale().createValue();

                if (property.getName().equals(MigrationResult.MIGRES_USED_TIME)) {
                    v.setValue(performance.getTotalCpuTimeUsed());
                }
                if (property.getName().equals(MigrationResult.MIGRES_MEMORY_GROSS)) {
                    v.setValue(performance.getMaxVirtualMemory());
                }
                if (property.getName().equals(MigrationResult.MIGRES_MEMORY_NET)) {
                    v.setValue(performance.getMaxResidentSize());
                }


                if (property.getName().equals("performance:averageResidentSize")) {
                    v.setValue(performance.getAverageResidentSize());
                } else if (property.getName().equals("performance:averageSharedMemory")) {
                    v.setValue(performance.getAverageSharedMemory());
                } else if (property.getName().equals("performance:averageVirtualMemory")) {
                    v.setValue(performance.getAverageVirtualMemory());
                } else if (property.getName().equals("performance:maxResidentSize")) {
                    v.setValue(performance.getMaxResidentSize());
                } else if (property.getName().equals("performance:maxSharedMemory")) {
                    v.setValue(performance.getMaxSharedMemory());
                } else if (property.getName().equals("performance:maxVirtualMemory")) {
                    v.setValue(performance.getMaxVirtualMemory());
                } else if (property.getName().equals("performance:totalCpuTimeUsed")) {
                    v.setValue(performance.getTotalCpuTimeUsed());
                }
                m.setValue(v);
                result.getMeasurements().put(property.getName(), m);
            }
        }
        
    }

}
