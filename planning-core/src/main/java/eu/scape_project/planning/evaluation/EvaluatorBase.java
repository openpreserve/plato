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
/**
 * 
 */
package eu.scape_project.planning.evaluation;

import java.io.InputStream;
import java.io.StringReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.utils.FileUtils;

/**
 * Base class for evaluators.
 * - Can loads possible measurements from a measurement description file
 *   
 * @author kraxner
 *
 */
public class EvaluatorBase implements IEvaluator {
	private static Logger log = LoggerFactory.getLogger(EvaluatorBase.class);
    
    protected MeasurementsDescriptor descriptor;
    protected String descriptorStr;
    
    /**
     * @see eu.scape_project.planning.evaluation.IEvaluator#getPossibleMeasurements()
     */
    public String getPossibleMeasurements() {
        return descriptorStr;
    }
    
    /**
     * loads measurements description from the given file.
     * populates descriptor and descriptor String
     * 
     * @param filename
     * @return
     */
    protected boolean loadMeasurementsDescription(String filename) {
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
            if (in != null) {
                descriptorStr = new String(FileUtils.inputStreamToBytes(in), "UTF-8");
                descriptor= new MeasurementsDescriptor();
                descriptor.addCriteria(new StringReader(descriptorStr));
                return true;
            }
        } catch (Exception e) {
            log.error("failed to load measurements description from " + filename, e);
        }
        return false;
    }

}
