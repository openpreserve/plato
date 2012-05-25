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
package eu.scape_project.planning.evaluation.evaluators;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.evaluation.EvaluatorBase;
import eu.scape_project.planning.evaluation.EvaluatorException;
import eu.scape_project.planning.evaluation.IObjectEvaluator;
import eu.scape_project.planning.evaluation.IStatusListener;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.scales.Scale;
import eu.scape_project.planning.model.util.CriterionUri;
import eu.scape_project.planning.model.values.PositiveFloatValue;
import eu.scape_project.planning.model.values.Value;

/**
 * This class entails functions for analysing original and transformed
 * objects, ranging from a simple comparison of file sizes to general
 * format-concerned issues such as well-formedness and validity to 
 * specific issues wrt image quality, where it delegates to @link {@link ImageComparisonEvaluator}
 * @author cb
 *
 */
public class ObjectEvaluator extends EvaluatorBase implements IObjectEvaluator {
	private static Logger log = LoggerFactory.getLogger(ObjectEvaluator.class);
    
    public ObjectEvaluator() {
        // load information about measurements
        loadMeasurementsDescription("data/evaluation/measurementsConsolidated.xml");
    }

    
    public HashMap<CriterionUri, Value> evaluate(Alternative alternative,
            SampleObject sample, DigitalObject result, List<CriterionUri> criterionUris,
            IStatusListener listener) throws EvaluatorException {

        listener.updateStatus("Objectevaluator: Start evaluation"); //" for alternative: %s, samle: %s", NAME, alternative.getName(), sample.getFullname()));
        
        HashMap<CriterionUri, Value> results = new HashMap<CriterionUri, Value>();
        
        for(CriterionUri criterionUri: criterionUris) {
            String propertyURI = criterionUri.getAsURI();
            // uri = scape://criterion#123
            if (OBJECT_FORMAT_RELATIVEFILESIZE.equals(propertyURI)) {
            	if (result != null) {
	            	Scale scale = descriptor.getMeasurementScale(criterionUri);
	                // evaluate here
	                PositiveFloatValue v = (PositiveFloatValue) scale.createValue();
	                double d = ((double)result.getData().getSize())/sample.getData().getSize()*100;
	                long l = Math.round(d);
	                d = ((double)l)/100;
	                v.setValue(d);
	                results.put(criterionUri, v);
	                listener.updateStatus(String.format("Objectevaluator: evaluated measurement: %s = %s", criterionUri.getAsURI(), v.toString()));
            	}
            }
        }
        criterionUris.removeAll(results.keySet());
        FITSEvaluator fitsEval = new FITSEvaluator();
        HashMap<CriterionUri, Value> fitsResults = fitsEval.evaluate(alternative, sample, result, criterionUris, listener);
        fitsResults.putAll(results);
        
        return fitsResults;
    }
    
}
