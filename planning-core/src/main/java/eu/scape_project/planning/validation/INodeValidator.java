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
package eu.scape_project.planning.validation;

import java.util.List;

import eu.scape_project.planning.model.tree.TreeNode;
import eu.scape_project.planning.validation.ValidationError;


/**
 * Validation of nodes in the objective tree, following the visitor pattern.
 * 
 * The tree has to be traversed in everyone of these classes,
 * so what we do is put the algorithm for Traversing into another class. This
 * one then gets an INodeValidator object and calls its validateNode() Method. This
 * Method then callse the iscompletelyspecified, iscompletelyevaluated or
 * iscompletelytransformed Method for a certain Node and returns if the node
 * validates.
 * 
 * @author florian Motlik
 * 
 */
public interface INodeValidator {

    /**
     * checks if the Node is valid
     * 
     * @param node
     *            The node to validate
     * @param errors TODO
     * @return if the Node validates in the certain circumstances (specified,
     *         evaluated, transformed)
     *  @see TreeNode#isCompletelyEvaluated(List, List)
     *  @see TreeNode#isCompletelySpecified(List<ValidationError>)
     *  @see TreeNode#isCompletelyTransformed(List)
     *  @see TreeNode#isCorrectlyWeighted(List)
     */
    public boolean validateNode(TreeNode node, List<ValidationError> errors);
}
