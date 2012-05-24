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
package eu.scape_project.planning.model;

import eu.scape_project.planning.model.tree.TreeNode;

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
