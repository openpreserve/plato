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
 * 
 * This work originates from the Planets project, co-funded by the European Union under the Sixth Framework Programme.
 ******************************************************************************/
package eu.scape_project.planning.model.sensitivity;

import eu.scape_project.planning.model.tree.TreeNode;

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
