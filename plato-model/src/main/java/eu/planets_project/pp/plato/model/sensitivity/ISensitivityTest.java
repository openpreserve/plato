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
package eu.planets_project.pp.plato.model.sensitivity;

import eu.planets_project.pp.plato.model.beans.ResultNode;

/**
 * Implementation of this inteface are used during the sensitivity analysis to
 * evaluate the results.
 * 
 * The sensitivity analysis performes modifications to the tree and its weights.
 * Methods of this intervace are called to evaluate the effects of these modifications. 
 * 
 * Each node of the tree is processed during the sensitivity analysis at least once.
 * 
 * @author Jan Zarnikov
 * 
 */
public interface ISensitivityTest {

    /**
     * Called before a new node is processed.
     * @param node The node that will be subject to the modifications.
     */
    public void beforeNode(ResultNode node);

    /**
     * After the node has been processed.
     * You probably want to store the result for this node. To do so use ResultNode.setSensitivityAnalysisResult().
     * @param node
     */
    public void afterNode(ResultNode node);

    /**
     * Each node can be processed several times (more rounds of modifications). This method is called before each iteration.
     * @param node
     */
    public void beforeIteration(ResultNode node);

    /**
     * Each node can be processed several times (more rounds of modifications). This method is called after each iteration.
     * @param node
     */
    public void afterIteration(ResultNode node);

}
