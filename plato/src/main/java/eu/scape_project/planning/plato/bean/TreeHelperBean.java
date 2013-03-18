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
package eu.scape_project.planning.plato.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import eu.scape_project.planning.model.tree.ITreeNode;
import eu.scape_project.planning.model.tree.ITreeWalker;

/**
 * Class used to programmatically expand and collapse nodes in a rich:tree.
 * 
 */
public class TreeHelperBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<ITreeNode, Boolean> expandedNodes = new HashMap<ITreeNode, Boolean>();

    private ITreeWalker treeExpander = new ITreeWalker() {
        @Override
        public void walk(ITreeNode node) {
            expandedNodes.put(node, true);
        }
    };

    /**
     * Returns a map of nodes and the expanded status.
     * 
     * Can be used for rich:treeNode's attribute <code>expanded</code> to set
     * the expanded status of a node
     * 
     * @return map of nodes with expanded status
     */
    public Map<ITreeNode, Boolean> getExpandedNodes() {
        return expandedNodes;
    }

    /**
     * Sets this node to collapsed.
     * 
     * @param node
     *            the node to collapse
     */
    public void closeNode(ITreeNode node) {
        expandedNodes.remove(node);
    }

    /**
     * Checks if the provided node is expaneded.
     * 
     * @param node
     *            the node check
     * @return true if it is expanded, false otherwise
     */
    public boolean isNodeExpanded(ITreeNode node) {
        if (node == null) {
            return false;
        }
        Boolean value = expandedNodes.get(node);
        return (value == null) ? false : value;
    }

    /**
     * Expands this node and all parent nodes, too.
     * 
     * @param node
     *            the node to expand
     */
    public void expandNode(ITreeNode node) {
        if (node == null) {
            return;
        }
        ITreeNode parent = node.getParent();
        if (parent != null) {
            if (!isNodeExpanded(parent)) {
                expandNode(parent);
            }
        }

        expandedNodes.put(node, true);
    }

    /**
     * Removes all nodes with a marker, which sets them to collapsed.
     */
    public void resetAllNodes() {
        expandedNodes.clear();
    }

    /**
     * Marks all nodes to collapsed.
     */
    public void collapseAll() {
        resetAllNodes();
    }

    /**
     * Expands the start node and all children recursively.
     * 
     * @param startNode
     *            the start node
     */
    public void expandAll(ITreeNode startNode) {
        if (startNode != null) {
            startNode.walkTree(treeExpander);
        }
    }
}
