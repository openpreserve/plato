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
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.tuwien.minimee.migration.parser.Jip_Parser;
import at.tuwien.minimee.model.ToolConfig;
import eu.scape_project.planning.model.beans.MigrationResult;
import eu.scape_project.planning.model.measurement.MeasurableProperty;
import eu.scape_project.planning.model.measurement.Measurement;
import eu.scape_project.planning.model.values.PositiveFloatValue;

/**
 * This engine uses the Java Interactive Profiler (JIP) to monitor
 * native Java migration processes
 * @author cbu
 *
 */
public class MonitorEngineJIP extends MiniMeeDefaultMigrationEngine {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Override
    protected void cleanup(long time, String inputFile, String outputFile) {
        super.cleanup(time, inputFile, outputFile);
        new File(makeConfigfileName(time)).delete();
        new File(makeJIPOutFilename(time)+".txt").delete();
    }
    
    private String makeConfigfileName(long time) {
        return getTempDir()+time+"jip.properties";
    }
    
    /**
     * this returns the parameter we are providing to JIP.
     * NOTE THAT JIP WILL APPEND .txt TO THIS!
     * @param time
     * @return
     */
    private String makeJIPOutFilename(long time) {
        return getTempDir()+time+"jip.out";
    }
    
    private void writeJIPconfigFile(long time) throws IOException {
        FileWriter w =new FileWriter(new File(makeConfigfileName(time)));
        w.write("ClassLoaderFilter.1=com.mentorgen.tools.profile.instrument.clfilter.StandardClassLoaderFilter\n");
        w.write("thread.compact.threshold.ms=1\n");
        w.write("method.compact.threshold.ms=1\n");
        w.write("file=");
        w.write(makeJIPOutFilename(time)+"\n"); //JIP will append .txt!
        w.write("track.object.alloc=on\n");
        w.write("output=text\n");
        w.write("output-method-signatures=yes");
        w.flush();
        w.close();
    }
    
    @Override
    protected String prepareCommand(ToolConfig config, String params, String inputFile, String outputFile, long time) throws Exception {
        writeJIPconfigFile(time);
        
        StringBuffer command = new StringBuffer();
        command.append("java -Xmx512m -javaagent:");
        command.append(getConfigParam());
        command.append(" -Dprofile.properties=");
        command.append(makeConfigfileName(time));
        command.append(" -jar ");
        command.append(config.getTool().getExecutablePath());
        command.append(" ");
        command.append(config.getParams());
        command.append(" ");
        command.append(inputFile);
        command.append(" ");
        command.append(outputFile);
        
        String cmd = command.toString();
        log.info("Command JIP: "+cmd);
        return cmd;
    }
    
    
    @Override
    protected void collectData(ToolConfig config, long time, MigrationResult result) {
        super.collectData(config, time, result);
        double totalTime = new Jip_Parser().getTotalTime(makeJIPOutFilename(time)+".txt");
        
        for (MeasurableProperty property: getMeasurableProperties()) {
            Measurement m = new Measurement();
            m.setProperty(property);
            PositiveFloatValue v = (PositiveFloatValue) property.getScale().createValue();
            if (property.getName().equals("performance:totalTimeInJava")) {
                v.setValue(totalTime);
            }
//            if (property.getName().equals(MigrationResult.MIGRES_USED_TIME)) {
//                v.setValue(totalTime);
//            }

            m.setValue(v);
            result.getMeasurements().put(property.getName(), m);
        }
    }
}
