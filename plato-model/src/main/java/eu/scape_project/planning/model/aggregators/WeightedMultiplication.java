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

package eu.scape_project.planning.model.aggregators;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.tree.Node;
import eu.scape_project.planning.model.tree.TreeNode;


/**
 * This {@link Aggregator} class performs weighted multiplication,
 * i.e. the result value for a node is the result of multiplying
 * the values of the child nodes, and taking this <b>to the power of the relative weight</b>(!)
 * @author cbu
 * 
 */
public class WeightedMultiplication extends Multiplication {

	/**
     * 
     */
    private static final long serialVersionUID = -4333955592969487905L;

    /**
     * returns <ul>
     * <li>for a {@link Leaf}: the value of the Alternative provided as parameter, taken to the power of the relative weight of the node</li>
     * <li>for a {@link Node}: the result of multiplying the values of all children,, taken to the power of the relative weight of the node</li>
     * </ul>
     * @param n the {@link Node} for which the aggregated value shall be cmputed
     * @param a the {@link Alternative} for which the aggregated value shall be computed
     * @see Multiplication#getAggregatedValue(TreeNode, Alternative) 
     */
    public double getAggregatedValue(TreeNode n, Alternative a) {
		return Math.pow(super.getAggregatedValue(n,a),n.getWeight());
	}

}
