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
package eu.scape_project.planning.evaluation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import eu.scape_project.planning.evaluation.IActionEvaluator;
import eu.scape_project.planning.evaluation.IEvaluator;
import eu.scape_project.planning.evaluation.IObjectEvaluator;


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
        
        register("myExperiment", "eu.scape_project.planning.services.evaluation.taverna.SSHTavernaEvaluationService");
        
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
        String[] keys = new String[]{"myExperiment", "experiment", "object", "imagecomp"};
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
//    public Collection<Attribute> getPossibleMeasurements() {
//        
//        return descriptor.getPossibleMeasurements();
//    }
    
//    public MeasurementsDescriptor getMeasurementsDescriptor() {
//        return descriptor;
//    }
}
