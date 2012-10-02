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
package eu.scape_project.planning.model.beans;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.aggregators.IAggregator;
import eu.scape_project.planning.model.sensitivity.ISensitivityAnalysisResult;
import eu.scape_project.planning.model.sensitivity.ISensitivityTest;
import eu.scape_project.planning.model.sensitivity.IWeightModifier;
import eu.scape_project.planning.model.tree.ITreeNode;
import eu.scape_project.planning.model.tree.ITreeWalker;
import eu.scape_project.planning.model.tree.TreeNode;


public class ResultNode implements ITreeNode, Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = -8118525873048146001L;
    private List<ResultNode> children = new ArrayList<ResultNode>();
    private String name;
    /**
     * Aggregated results per alternative
     */
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

    /**
     * Creates a result node for the given TreeNode, applying the given aggregator a.
     * - applied to the root node of an objective tree this calculates the overall result for each alternative.
     *   
     * @param n
     * @param a
     * @param alternatives
     */
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
