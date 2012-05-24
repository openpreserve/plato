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
package eu.scape_project.planning.xml.freemind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.manager.CriteriaManager;
import eu.scape_project.planning.model.PolicyNode;
import eu.scape_project.planning.model.kbrowser.CriteriaHierarchy;
import eu.scape_project.planning.model.kbrowser.CriteriaNode;
import eu.scape_project.planning.model.tree.TreeNode;

public class MindMap {
    private Node root;

    private static final Logger log = LoggerFactory.getLogger(MindMap.class);

    public void addChild(Node root) {
        if (this.root != null) {
            log.warn("root added twice on temp MindMap!");
        }
        this.root = root;
    }


    /**
     * This gets the complete ObjectiveTree out of the mindmap structure
     * @param hasUnits
     * @param hasLeaves deontes if the original mindmap contained leaves, or just nodes
     * @return {@link eu.scape_project.planning.model.tree.ObjectiveTree}
     */
    public TreeNode getObjectiveTreeRoot(boolean hasUnits, boolean hasLeaves) {
        return root.createNode(hasUnits, hasLeaves);
    }

    public PolicyNode getPolicyTreeRoot() {
        return root.createPolicyNode();
    }
    
    /**
     * Method responsible for converting this MindMap into its representing CriteriaHierarchy.
     * 
     * @param criteriaManager Class used to map CriteriaLeaves to correct criterion.
     * @return CriteriaHierarchy represented by this MindMap
     */
    public CriteriaHierarchy getRepresentingCriteriaHierarchy(CriteriaManager criteriaManager) {
    	CriteriaHierarchy criteriaHierarchy = new CriteriaHierarchy();
    	criteriaHierarchy.setName(root.getTEXT());
    	criteriaHierarchy.setCriteriaTreeRoot((CriteriaNode) root.createCriteriaTreeNode(criteriaManager));

    	return criteriaHierarchy;
    }

    public Node getRoot() {
        return root;
    }


    public void setRoot(Node root) {
        this.root = root;
    }
}
