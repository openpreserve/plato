/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.scape_project.pw.planning.plato.bean;

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
     * Returns a map of nodes to their expanded status.
     * 
     * Can be used for rich:treeNode's attribute <code>expanded</code> to set the expanded status of a node
     * 
     * @return
     */
	public Map<ITreeNode, Boolean> getExpandedNodes() {
		return expandedNodes;
	}
    
	/**
	 * Sets this node to collapsed.
	 * 
	 * @param node
	 */
    public void closeNode(ITreeNode node) {
        expandedNodes.remove(node);
    }
    
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
     * @return
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
     * Removes all nodes with a marker, which sets them to collapsed
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
     * Expands the node, and all it children recursively
     *  
     * @param startNode
     */
    public void expandAll(ITreeNode startNode) {
    	if (startNode != null) {
    		startNode.walkTree(treeExpander);
    	}
    }
}