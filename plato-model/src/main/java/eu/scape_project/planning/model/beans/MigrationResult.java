/*******************************************************************************
 * Copyright 2012 Vienna University of Technology
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
 * 
 * This work originates from the Planets project, co-funded by the European Union under the Sixth Framework Programme.
 ******************************************************************************/
package eu.scape_project.planning.model.beans;

import java.io.Serializable;
import java.util.HashMap;

import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.model.measurement.Measurement;
/**
 * Container for results of a migration action
 * 
 * @author Michael Kraxner
 *
 */
public class MigrationResult implements Serializable{
    
    private static final long serialVersionUID = 3672304540894998240L;
    
    private String report;
    private boolean successful = false;
    
    private long feedbackKey = 0;
    
    /**
     * Holds additional information about the migration outcome, i.e. performance measurements
     * Known keys are defined below (MIGRES_*)
     *  
     * NOTE: It is crucial to store values with the proper unit, as stated in corresponding comments
     */
    private HashMap<String,Measurement> measurements = new HashMap<String, Measurement>();
    
    private FormatInfo sourceFormat = new FormatInfo();
    private FormatInfo targetFormat = new FormatInfo();
    
    private DigitalObject migratedObject = new DigitalObject();

    
    /**
     * Could object be migrated
     * "true"/"false"  
     */
    public static final String MIGRES_SUCCESS = "process:success";
    /**
     * Report about outcome of migration
     * String 
     */
    public static final String MIGRES_REPORT = "process:report";
    /**
     * CPU time elapsed during migration. 
     * Unit: [msec] .. milliseconds  
     */
    public static final String MIGRES_ELAPSED_TIME = "performance:time:elapsed";

    
    /**
     * CPU time elapsed during migration, per MB of input file size
     * Unit: [msec] .. milliseconds  
     */
    public static final String MIGRES_ELAPSED_TIME_PER_MB = "performance:time:elapsedPerMB";

    
    /**
     * CPU time used during migration
     * Unit: [msec] .. milliseconds  
     */
    public static final String MIGRES_USED_TIME = "performance:time:used";
    
    /**
     * CPU time used during migration, per Megabyte
     * Unit: [msec] .. milliseconds  
     */
    public static final String MIGRES_USED_TIME_PER_MB = "performance:time:usedPerMB";
    

    
    /**
     * GROSS Memory used during migration, including virtual mem / VM memory
     * Unit: [msec] .. milliseconds  
     */
    public static final String MIGRES_MEMORY_GROSS = "performance:memory:gross";
    
    /**
     * GROSS Memory used during migration, EXcluding virtual mem / VM memory
     * Unit: [msec] .. milliseconds  
     */
    public static final String MIGRES_MEMORY_NET = "performance:memory:net";
    
    
    /**
     * Memory used during migration
     * Unit: [MB]  .. megabytes 
     */
    public static final String MIGRES_MEMORY_USED = "performance:memoryUsed";
    
    /**
     * Average CPU load
     * Unit: [%] .. percent [0.0 .. 100]% 
     */
    public static final String MIGRES_AVERAGE_CPU_LOAD = "performance:averageCPULoad";
    
    /**
     * Average of memory used during migration
     * Unit: [MB] .. megabytes 
     */
    public static final String MIGRES_AVERAGE_MEMORY_LOAD = "performance:averageMemoryLoad";
    
    /**
     * File size of migration result
     * Unit: [MB] .. megabytes
     */
    public static final String MIGRES_RESULT_FILESIZE = "result:filesize";
    
    /**
     * Resulting file size in relation to size of original file
     * Unit: [%] ratio [0.0 .. ) 
     */
    public static final String MIGRES_RELATIVE_FILESIZE = "result:relativeFilesizePercent";
    
    public String getReport() {
        return report;
    }
    public void setReport(String report) {
        this.report = report;
    }
    public FormatInfo getSourceFormat() {
        return sourceFormat;
    }
    public void setSourceFormat(FormatInfo sourceFormat) {
        this.sourceFormat = sourceFormat;
    }
    public FormatInfo getTargetFormat() {
        return targetFormat;
    }
    public void setTargetFormat(FormatInfo targetFormat) {
        this.targetFormat = targetFormat;
    }
    public boolean isSuccessful() {
        return successful;
    }
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
    public DigitalObject getMigratedObject() {
        return migratedObject;
    }
    public void setMigratedObject(DigitalObject migratedObject) {
        this.migratedObject = migratedObject;
    }
    public HashMap<String, Measurement> getMeasurements() {
        return measurements;
    }
    public void setMeasurements(HashMap<String, Measurement> measurements) {
        this.measurements = measurements;
    }
    public long getFeedbackKey() {
        return feedbackKey;
    }
    public void setFeedbackKey(long feedbackKey) {
        this.feedbackKey = feedbackKey;
    }
  
}
