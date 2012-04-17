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
package eu.planets_project.pp.plato.evaluation.evaluators;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.planets_project.pp.plato.evaluation.EvaluatorBase;
import eu.planets_project.pp.plato.evaluation.EvaluatorException;
import eu.planets_project.pp.plato.evaluation.IObjectEvaluator;
import eu.planets_project.pp.plato.evaluation.IStatusListener;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.util.CriterionUri;
import eu.planets_project.pp.plato.model.values.PositiveFloatValue;
import eu.planets_project.pp.plato.model.values.Value;

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
