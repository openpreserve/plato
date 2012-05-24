/*******************************************************************************
 * Copyright 2012 Vienna University of Technology
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
 * 
 * This work originates from the Planets project, co-funded by the European Union under the Sixth Framework Programme.
 ******************************************************************************/
package eu.scape_project.planning.model.aggregators;

import java.io.Serializable;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.tree.TreeNode;

/**
 * This interface defines the aggregation function 
 * to be applied on a TreeNode
 * 
 * @author Christoph Becker
 */
public interface IAggregator extends Serializable {
    public double getAggregatedValue(TreeNode n, Alternative a);
}
