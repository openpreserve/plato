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
package eu.planets_project.pp.plato.evaluation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;


public class MiniRED implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject private Logger log;

	private Map<String, String> evaluatorClasses = new HashMap<String, String>();
    
    public MiniRED() {
        reloadEvaluators();
    }
    
    public void reloadEvaluators(){
//        descriptor.clearCriteria();

        // These are deactivated for now since they are experimental. The plan is to reintroduce them in Plato 3.1
//        register("metadata",    "eu.scape_project.planning.evaluation.evaluators.ImageMetadataEvaluator");
//        register("imagecompjava", "eu.scape_project.planning.evaluation.evaluators.imagecomparison.java.ImageComparisonEvaluator");
        
        register("pcdl",    "eu.scape_project.planning.evaluation.evaluators.PCDLEvaluator");
        register("experiment", "eu.scape_project.planning.evaluation.evaluators.ExperimentEvaluator");
        register("object", "eu.scape_project.planning.evaluation.evaluators.ObjectEvaluator");
        register("minireef", "eu.scape_project.planning.evaluation.evaluators.MiniREEFEvaluator");
        register("imagecomp", "eu.scape_project.planning.evaluation.evaluators.ImageComparisonEvaluator");
        
        //register("consolidated", "eu.scape_project.planning.evaluation.evaluators.ConsolidatedEvaluator");
    }
    
    public IEvaluator createEvaluator(String schema) {
        String className = evaluatorClasses.get(schema);
        
        try {
            IEvaluator eval = (IEvaluator) Class.forName(className).newInstance();
            return eval;
        } catch (Exception e) {
            log.error("Could not create an IEvaluator for schema:"+schema, e);
            return null;
        } 
    }
    
    public List<IObjectEvaluator> getObjectEvaluationSequence() {
        LinkedList<IObjectEvaluator> evaluators = new LinkedList<IObjectEvaluator>();
        // "metadata", 
        String[] keys = new String[]{"experiment", "object", "imagecomp"};
        for (String s: keys) {
            if (evaluatorClasses.containsKey(s)) {
                evaluators.add((IObjectEvaluator)createEvaluator(s));
            }
        }
        return evaluators;
    }
    
    public List<IActionEvaluator> getActionEvaluationSequence() {
        LinkedList<IActionEvaluator> evaluators = new LinkedList<IActionEvaluator>();

        String[] keys = new String[]{"pcdl","minireef"};
        for (String s: keys) {
            if (evaluatorClasses.containsKey(s)) {
                evaluators.add((IActionEvaluator)createEvaluator(s));
            }
        }
        return evaluators;
    }
    
    public String echo(String s) {
        return s;
    }
    /** 
     * temporarily this is a double entry point
     * @see #createEvaluator(String)
     */
    public IEvaluator discover(String name) {
        return createEvaluator(name);
    }

    public void register(String name, String classname) {
        evaluatorClasses.put(name,classname);
    }

//    /**
//     * returns all measurements, which can be evaluated with the currently registered evaluators
//     * 
//     * @return uri of measurement, see  {@link CriterionUri}
//     */
//    public Collection<MeasurableProperty> getPossibleMeasurements() {
//        
//        return descriptor.getPossibleMeasurements();
//    }
    
//    public MeasurementsDescriptor getMeasurementsDescriptor() {
//        return descriptor;
//    }
}
