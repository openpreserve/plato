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

package eu.planets_project.pp.plato.model.beans;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.aggregators.IAggregator;
import eu.planets_project.pp.plato.model.sensitivity.ISensitivityAnalysisResult;
import eu.planets_project.pp.plato.model.sensitivity.ISensitivityTest;
import eu.planets_project.pp.plato.model.sensitivity.IWeightModifier;
import eu.planets_project.pp.plato.model.tree.ITreeNode;
import eu.planets_project.pp.plato.model.tree.ITreeWalker;
import eu.planets_project.pp.plato.model.tree.TreeNode;


public class ResultNode implements ITreeNode, Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = -8118525873048146001L;
    private List<ResultNode> children = new ArrayList<ResultNode>();
    private String name;
    private HashMap<String, String> resultStrings = new HashMap<String, String>();
    private HashMap<String, Double> results = new HashMap<String, Double>();
    private static DecimalFormat format = new DecimalFormat("#0.00");
    
    private ResultNode parent = null;
    private int id;
    
 private ISensitivityAnalysisResult sensitivityAnalysisResult;
    
    private HashMap<TreeNode, Double> nodeWeights = new HashMap<TreeNode, Double>();

    private TreeNode treeNode;
    

    public String getStyle () {
        return (isSensitive() ? "sensitiveNode" :
             (isAnyChildSensitive() ? "sensitiveNodeChildren"
                     : ""));
    }
    
//    private List<Alternative> alternatives;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addChild(ResultNode n) {
        children.add(n);
        n.setParent(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ResultNode> getChildren() {
        return children;
    }

    public void setChildren(List<ResultNode> children) {
        this.children = children;
    }

    public ResultNode() {
    
    }
    
    private boolean leaf;
    
    
    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public ResultNode(TreeNode n, IAggregator a, List<Alternative> alternatives) {
        this.treeNode = n;
        setName(n.getName());
        setId(n.getId());
        setLeaf(n.isLeaf());
          for (Alternative alt : alternatives) {
             resultStrings.put(alt.getName(), format.format(a.getAggregatedValue(n,alt)));
             results.put(alt.getName(), a.getAggregatedValue(n,alt));
          }
        for (TreeNode node: n.getChildren()) {
            addChild(new ResultNode(node,a,alternatives));
        }
    }
    
    public ResultNode getParent() {
        return parent;
    }

    public void setParent(ResultNode parent) {
        this.parent = parent;
    }

    public HashMap<String, Double> getResults() {
        return results;
    }
    public HashMap<String, String> getResultStrings() {
        return resultStrings;
    }

    public void analyseSensitivity(IWeightModifier modificator, ISensitivityTest test) {
        if(treeNode.isLeaf()) {
            return;
        }
        
        saveNodeWeights(treeNode);
        test.beforeNode(this);
        while(true) {
            test.beforeIteration(this);
            boolean repeat = modificator.performModification(treeNode);
            treeNode.normalizeWeights(false);
            test.afterIteration(this);
            restoreNodeWeights(treeNode);
            if(!repeat) {
                break;
            }
        }
        test.afterNode(this);
        for(ResultNode child : children) {
            child.analyseSensitivity(modificator, test);
        }

    }
    
    private void saveNodeWeights(TreeNode n) {
        nodeWeights.clear();
        for(TreeNode child : n.getChildren()) {
            nodeWeights.put(child, child.getWeight());
        }
    }
    
    private void restoreNodeWeights(TreeNode n) {
        for(TreeNode child : n.getChildren()) {
            child.setWeight(nodeWeights.get(child));
        }
    }
    
    /**
     * Equivalent to isSensitive(false).
     * 
     * @return true if the importance factors (weights) of this node are
     *         unstable. This means that small changes can result in change in
     *         ordering of the alternatives. Otherwise false.
     */
    public boolean isSensitive() {
        return isSensitive(false);
    }
    

    /**
     * @param recoursive if true the direct and indirect children are also inspected.
     * 
     * @return true if the importance factors (weights) of this node are
     *         unstable. This means that small changes can result in change in
     *         ordering of the alternatives. Otherwise false.
     */
    public boolean isSensitive(boolean recursive) {
        if(sensitivityAnalysisResult != null) {
            if(sensitivityAnalysisResult.isSensitive()) {
                return true;
            }
            boolean result = false;
            if(recursive) {
                for(ResultNode child : children) {
                    result |= child.isAnyChildSensitive();
                }
            }
            return result;
        } else {
            return false;
        }
    }
    
    /**
     * @return True if any of the children of this node is sensitive.
     */
    public boolean isAnyChildSensitive() {
        if(sensitivityAnalysisResult != null) {
            boolean result = false;
            for(ResultNode child : children) {
                result |= child.isSensitive(true);
            }
            return result;
        } else {
            return false;
        }
    }

    public void setSensitivityAnalysisResult(ISensitivityAnalysisResult analysisResult) {
        this.sensitivityAnalysisResult = analysisResult;
    }
        
    public ISensitivityAnalysisResult getSensitivityAnalysisResult() {
        return sensitivityAnalysisResult;
    }

    public TreeNode getTreeNode() {
        return treeNode;
    }

	@Override
	public void walkTree(ITreeWalker treeWalker) {
    	treeWalker.walk(this);
    	for (ResultNode node : children) {
    		node.walkTree(treeWalker);
    	}
	}
}
