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

package eu.planets_project.pp.plato.model;

import eu.planets_project.pp.plato.model.tree.TreeNode;

/**
 * This is the return value of {@link TreeNode#getEvaluationStatus()}, which can be
 * on of the following:
 * <ul>
 * <li> NONE: no evaluation results present
 * <li> PARTLY: some leaves have evaluation results, but not all
 * <li> COMPLETE: evaluation results are complete
 * </ul>
 * @author Christoph Becker
 *
 */
public enum EvaluationStatus {
	NONE,
	PARTLY,
	COMPLETE
}
