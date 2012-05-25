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
package eu.scape_project.planning.validation;

import java.util.List;

import javax.inject.Inject;
import javax.swing.tree.TreeModel;

import org.slf4j.Logger;

import eu.scape_project.planning.model.tree.TreeNode;
import eu.scape_project.planning.validation.ValidationError;

/**
 * Performs validation for the tree.
 *
 * @author Florian Motlik
 */
public class TreeValidator implements ITreeValidator {

    @Inject private Logger log;
    
    /**
     * Traverses through the CoreTreeTable and validates each TreeNode in the CoreTreeTable.
     *
     * Implements {@link eu.scape_project.planning.validation.ITreeValidator#validate(TreeNode, INodeValidator, INodeValidator, List)}
     *
     * @see eu.scape_project.planning.validation.ITreeValidator#validate(TreeModel, CoreTreeTable, INodeValidator, List)
     */
    public boolean validate(TreeNode node, INodeValidator validator, List<ValidationError> errors) {
        return validateRow(node, validator, errors);
    }

    /**
     * This Method is for recursively traversing through the tree and
     * validating every single TreeNode in the tree.
     *
     * @param nodeList
     *            ErrorMessages to be displayed
     * @param nodes
     *            Nodes and Leaves can add themselves to this list if they fail
     * @return True if the Tree Validates, false if not.
     */
    private boolean validateRow(TreeNode node, INodeValidator validator, List<ValidationError> errors) {
        boolean validates = true;
        /*
         * Call to the Validator, which then calls the appropriate
         * Method of a TreeNode for validation.
         */
        boolean valid = validator.validateNode(node, errors);
        log.debug("Validator is: " + valid + " " + node);
        if (!valid) {
            log.debug("Not Completely Specified");
            validates = false;

            // TODO Display erroneous node itself, put it into a list...? 
        }

        /*
         * If the model is a container (has children) we recursively tell it
         * to validate its children
         */
        if (!node.isLeaf()) {
            for (TreeNode n: node.getChildren()) {
                if (!validateRow(n, validator, errors)) {
                    validates = false;
                }
            }
        }
        return validates;
    }
}
