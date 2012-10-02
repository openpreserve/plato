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

import eu.scape_project.planning.model.beans.ResultNode;

/**
 * Implementation of this inteface are used during the sensitivity analysis to
 * evaluate the results.
 * 
 * The sensitivity analysis performes modifications to the tree and its weights.
 * Methods of this intervace are called to evaluate the effects of these modifications. 
 * 
 * Each node of the tree is processed during the sensitivity analysis at least once.
 * 
 * @author Jan Zarnikov
 * 
 */
public interface ISensitivityTest {

    /**
     * Called before a new node is processed.
     * @param node The node that will be subject to the modifications.
     */
    public void beforeNode(ResultNode node);

    /**
     * After the node has been processed.
     * You probably want to store the result for this node. To do so use ResultNode.setSensitivityAnalysisResult().
     * @param node
     */
    public void afterNode(ResultNode node);

    /**
     * Each node can be processed several times (more rounds of modifications). This method is called before each iteration.
     * @param node
     */
    public void beforeIteration(ResultNode node);

    /**
     * Each node can be processed several times (more rounds of modifications). This method is called after each iteration.
     * @param node
     */
    public void afterIteration(ResultNode node);

}
