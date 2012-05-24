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

import java.util.HashMap;
import java.util.List;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.util.CriterionUri;
import eu.scape_project.planning.model.values.Value;

/**
 * This is the interface for all evaluation plugins that are providing
 * measurements which do not require looking at a specific object or the experiment
 * produced by applying an action to a specific object, but instead look at
 * the actions or the format that results from applying certain actions.
 * Correspondingly, these will usually belong to the categories <ul>
 * <li>outcome format and</li>
 * <li>action static.</li>
 * </ul>
 * The interface defines constant URIs for core properties that are implemented 
 * and integrated in Plato.
 * @author cb
 */public interface IActionEvaluator extends IEvaluator {
    
    /**
     * how many tools are there for the given format ?  (measure, positive integer)
     * 
     * atm: http://p2-registry.ecs.soton.ac.uk/pronom/SoftwareLink  (../Open/ /Save/
     */
    String FORMAT_NUMBEROFTOOLS = "outcome://format/sustainability/adaption/toolSupport/nrOfTools";
    String FORMAT_NUMBEROFTOOLS_OPEN = "outcome://format/numberOfTools/open";
    String FORMAT_NUMBEROFTOOLS_SAVE = "outcome://format/numberOfTools/save";
    String FORMAT_NUMBEROFTOOLS_OTHERS = "outcome://format/numberOfTools/others";

    /**
     * P2 Risk categories:
     * "This ontology encapsulates those elements of risk analysis which are potentially opinionated 
     * and thus can and should be overidden by the local repository"
     */
    
    /**
     * is the format complex, simple ?  (measure, free text)
     * 
     * atm: http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/complexity/../comment
     */
    String FORMAT_COMPLEXITY    = "outcome://format/complexity";

    /**
     * is the format open?  (measure, free text)
     * 
     * atm: http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/disclosure/../comment
     */
    String FORMAT_DISCLOSURE    = "outcome://format/disclosure";

    /**
     * is the format used widely, ..?  (measure, free text)
     * 
     * atm: http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/ubiquity/../comment
     */
    String FORMAT_UBIQUITY      = "outcome://format/ubiquity";
    
    /**
     * how stable is the format (measure, free text)
     * 
     * atm: http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/stability/../comment
     */
    String FORMAT_STABILITY = "outcome://format/stability";    
    
    /**
     * what is the quality of the format's documentation (measure, free text)
     * 
     * atm: http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/documentation_quality/../comment
     */
    String FORMAT_DOCUMENTATION_QUALITY = "outcome://format/documentation/quality";    


    /**
     *  open/ipr_protected/proprietary
     */
    String FORMAT_SUSTAINABILITY_RIGHTS = "outcome://format/sustainability/rights";
    
    /**
     * rights (measure, free text)
     * 
     * atm: http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/rights
     * 
     */
     String FORMAT_LICENSE = "outcome://format/license";    

//    /**
//     * rights: ipr protected (measure, boolean)
//     * 
//     * atm: http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/rights/ipr_protected
//     */
//    String FORMAT_LICENSE_IPR_PROTECTED = "outcome://format/license/iprProtected";    
//
//    /**
//     * rights: open (measure, boolean)
//     * 
//     * atm: http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/rights/open
//     */
//    String FORMAT_LICENSE_OPEN = "outcome://format/license/open";    
//
//    /**
//     * rights: proprietary (measure, boolean)
//     * 
//     * atm: http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/rights/proprietary
//     */
//    String FORMAT_LICENSE_PROPRIETARY = "outcome://format/license/proprietary";    
    
        
    /**
     * is it an open source tool?  (measure, ordinal)
     */
    String ACTION_BUSINESS_LICENCING_SCHEMA = "action://business/licencingSchema";
    
    /**
     * which type of license applies to the tool?  (measure, free text)
     * 
     * atm: PCDL /License
     */
    String ACTION_LICENSE = "action://business/licence";
    
    /**quality
     * is there batch support?  (measure, boolean)
     */
    String ACTION_BATCH_SUPPORT = "action://compatibility/interoperability/interfaces/batchProcessingSupport";
    
    /**
     * is the filename retained?  (measure, boolean)
     */
    String ACTION_RETAIN_FILENAME = "action://functionalSuitability/functionalCompleteness/generic/retainFilename";    
    
    /**
     * evaluates an action with regard to the given critera defined in leaves
     * returns a list of values, one per leaf
     * 
     * It is not nice that leaves are passed to the evaluator, and a map of leaves to values is returned
     * 
     * This information is really needed:
     *  - how this criterion is measured (Criterion)
     *  - what is type of the evaluated value (Scale)
     *  
     * @param alternative
     * @param criterionUris
     * @param listener
     * @return
     * @throws EvaluatorException
     */
    public HashMap<CriterionUri, Value> evaluate(
            Alternative alternative,
            List<CriterionUri> criterionUris, IStatusListener listener) throws EvaluatorException;
    
}
