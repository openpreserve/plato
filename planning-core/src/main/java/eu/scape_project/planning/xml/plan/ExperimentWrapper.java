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
package eu.scape_project.planning.xml.plan;

import java.util.HashMap;
import java.util.Map;

import eu.scape_project.planning.model.DetailedExperimentInfo;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Experiment;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.XcdlDescription;

/**
 * Wrapper which provides a simple map <samplerecord-ids, uploads> - for
 * {@link eu.scape_project.planning.xml.ProjectImporter}
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
        tempXcdlDescriptions.put((String) record, (XcdlDescription) upload);
    }

    public void addResult(Object record, Object upload) {
        tempUploads.put((String) record, (DigitalObject) upload);
    }

    public void addDetailedInfo(Object key, Object info) {
        tempDetailedInfos.put((String) key, (DetailedExperimentInfo) info);
    }

    /**
     * Establishes the Uploads mapping by looking up the names of sample records
     * in the provided hashmap <code>records</code>.
     * 
     * @param records
     * @return Experiment instance with filled {@link Experiment#getResults()}
     *         mapping.
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
