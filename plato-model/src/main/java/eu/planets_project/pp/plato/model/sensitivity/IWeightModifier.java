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

import eu.planets_project.pp.plato.model.tree.TreeNode;

/**
 * Implementations of this interface should modify the weights of the nodes during the sensitivity analysis.
 *  
 * @author Jan Zarnikov
 *
 */
public interface IWeightModifier {
    
    /**
     * Modify the weights of the given node. The weights of direct children are
     * saved before this call and restored afterwards. This means that you don't
     * have to worry about storing and restoring of the weight in your
     * implementation.
     * 
     * @param node
     *            The current node processed by the sensitivity analysis.
     * @return True if the current node should be processed once more (e.g. you
     *         want to perform a different modification on the same node).
     *         Otherwise the analysis will proceed to the next node.
     */
    public boolean performModification(TreeNode node);

}
