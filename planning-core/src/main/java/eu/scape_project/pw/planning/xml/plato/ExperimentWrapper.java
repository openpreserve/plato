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

package eu.scape_project.pw.planning.xml.plato;

import java.util.HashMap;
import java.util.Map;

import eu.planets_project.pp.plato.model.DetailedExperimentInfo;
import eu.planets_project.pp.plato.model.Experiment;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.XcdlDescription;

/**
 * Wrapper which provides a simple map <samplerecord-ids, uploads> - for {@link eu.planets_project.pp.plato.xml.ProjectImporter}
 *  
 * @author Michael Kraxner
 */
public class ExperimentWrapper extends Experiment {
    /**
     * 
     */
    private static final long serialVersionUID = -4398195918297082973L;
    
    private Map<String, DigitalObject> tempUploads = new HashMap<String, DigitalObject>();
    private Map<String, XcdlDescription> tempXcdlDescriptions = new HashMap<String, XcdlDescription>();    
    private Map<String, DetailedExperimentInfo> tempDetailedInfos = new HashMap<String, DetailedExperimentInfo>();    

    public void addXcdlDescription(Object record, Object upload) {
        tempXcdlDescriptions.put((String)record, (XcdlDescription)upload);
    }

    
    public void addResult(Object record, Object upload) {
        tempUploads.put((String)record, (DigitalObject)upload);
    }
    
    
    public void addDetailedInfo(Object key, Object info) {
        tempDetailedInfos.put((String)key, (DetailedExperimentInfo)info);
    }
    /**
     * Establishes the Uploads mapping by looking up the names of sample records in
     * the provided hashmap <code>records</code>.  
     *   
     * @param records
     * @return Experiment instance with filled {@link Experiment#getResults()} mapping.
     */
    public Experiment getExperiment(HashMap<String, SampleObject> records) {
        
        /*
         * map sample records to uploads and their xcdlDescriptions, if present
         */
        for (String key : tempUploads.keySet()) {
            SampleObject rec = records.get(key);
            if (rec != null) {
                DigitalObject result = tempUploads.get(key);
                getResults().put(rec, result);
            }
        }
        
        /*
         * map sample records to detailedInfos
         */
        for (String key : tempDetailedInfos.keySet()) {
            SampleObject rec = records.get(key);
            if (rec != null) {
               getDetailedInfo().put(rec, tempDetailedInfos.get(key));
            }
        }

        return this; 
    }
}
