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
 */
public interface ITreeValidator {

    /**
     * This method validates the whole tree (branch) provided according to the INodeValidator that
     * is given to it. It traverses through the model
     * 
     * @param validator The Validator that calls the right methods in every TreeNode
     * @param errors List of Validation errors, one entry per TreeNode  
     * @return TRUE if tree validates, FALSE if not
     */
    boolean validate(TreeNode node, INodeValidator validator, List<ValidationError> errors);

}
