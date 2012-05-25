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
package eu.scape_project.planning.model.aggregators;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.tree.Node;
import eu.scape_project.planning.model.tree.TreeNode;

/**
 * This {@link Aggregator} class performs multiplication,
 * i.e. the result value for a node is the result of multiplying
 * the values of the child nodes. 
 * Does not consider weightings!
 * @author cbu
 *
 */
public class Multiplication extends Aggregator {

    /**
     * 
     */
    private static final long serialVersionUID = 4826097775805428124L;

    /**
     * returns <ul>
     * <li>for a {@link Leaf}: the value of the Alternative provided as parameter</li>
     * <li>for a {@link Node}: the result of multiplying the values of all children.</li>
     * </ul>
     * @param n the node for which the aggregated value shall be cmputed
     * @param alternative the Alternative for which the aggregated value shall be computed 
     */
    public double getAggregatedValue(TreeNode n, Alternative alternative) {
            
            if (n instanceof Leaf) {
                return ((Leaf)n).getResult(alternative);
            } else {
                // multiply the values of all children
                double d = 1.0;
                for (TreeNode child: n.getChildren()) {
                        d *= getAggregatedValue(child,alternative);
                }
                return d;
            }
	}

}
