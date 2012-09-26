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
package eu.scape_project.planning.xml.freemind;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.manager.CriteriaManager;
import eu.scape_project.planning.model.Policy;
import eu.scape_project.planning.model.PolicyNode;
import eu.scape_project.planning.model.kbrowser.CriteriaLeaf;
import eu.scape_project.planning.model.kbrowser.CriteriaNode;
import eu.scape_project.planning.model.kbrowser.CriteriaTreeNode;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.scales.BooleanScale;
import eu.scape_project.planning.model.scales.FreeStringScale;
import eu.scape_project.planning.model.scales.IntRangeScale;
import eu.scape_project.planning.model.scales.OrdinalScale;
import eu.scape_project.planning.model.scales.PositiveFloatScale;
import eu.scape_project.planning.model.scales.RestrictedScale;
import eu.scape_project.planning.model.scales.Scale;
import eu.scape_project.planning.model.scales.YanScale;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.tree.TreeNode;

/**
 * Helper class for importing FreeMind MindMaps into the objective tree structure of Plato.
 * This class holds both text and a list of children.
 * All the getter,setter and adder are for the Digester import,
 * createNode does the real work.
 * @author Christoph Becker
 * @see eu.scape_project.planning.xml.TreeLoader#loadFreeMindPolicyMap(java.io.InputStream)
 */
public class Node {
    private static Logger log = LoggerFactory.getLogger(Node.class);
    
    /**
     * property corresponding to a freemind xml mindmap
     */
    private String TEXT;

    /**
     * property corresponding to a freemind xml mindmap
     */
    private String CREATED;

    /**
     * property corresponding to a freemind xml mindmap
     */
    private String MODIFIED;

    private String DESCRIPTION;
    
    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String description) {
        DESCRIPTION = description;
    }

    private List<Node> children = new ArrayList<Node>();

    private Node parent;

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    /**
     * creates an appropriate scale out of myself,
     * one of the subtypes in the package eu.scape_project.planning.model.scales
     * @return {@link Scale}
     */
    public Scale createScale() {
        try {
            if (TEXT == null) {
                return null;
            }
            
            if (("Y/N".equals(TEXT)) ||
                ("Yes/No".equals(TEXT))){
                return new BooleanScale();
            }

            YanScale yan = new YanScale();
            if ((yan.getRestriction().equals(TEXT)) ||
                ("Y/A/N".equals(TEXT))) {
                return yan;
            }

            if ("?".equals(TEXT)) {
                return null;
            }
            
            if ("FREE TEXT".equals(TEXT.toUpperCase())) {
                return new FreeStringScale();
            }

            if (TEXT.indexOf(Scale.SEPARATOR) != -1 ) {
                RestrictedScale v = null;
//              try {
//              v = new IntRangeScale();
//              v.setRestriction(TEXT);
//              } catch (IllegalArgumentException e) {
//              // do nothing, that's ok, thrown by IntRange -
//              // it means we don't have numbers, but an ordinary scale.
//              // proceed:
//              v = new OrdinalScale();
//              v.setRestriction(TEXT);
//              }
                v = new IntRangeScale();
                if (! ((IntRangeScale)v).validateAndSetRestriction(TEXT)) {
                    v= new OrdinalScale();
                    v.setRestriction(TEXT);
                }
                return v;
            }
        } catch (Exception e) {
            log.warn("invalid scale format, ignoring: "+TEXT,e);
        }
        //default behaviour: float scale and the TEXT as unit
        PositiveFloatScale v = new PositiveFloatScale();
        v.setUnit(TEXT);
        return v;
    }

    /**
     * Creates a complete {@link eu.scape_project.planning.model.tree.TreeNode} out of myself
     * and my children. Works recursively!
     * So calling this on the root actually constructs the whole Plato-conforming
     * ObjectiveTree out of a FreeMind MindMap.
     * @return {@link Node} or {@link Leaf}
     * @param hasUnits If this is set to true, the leafs are ignored.
     * This can be useful if the tree to be imported already contains all the measurement
     * units, but defined as leaf nodes (We found that to be very useful during workshops.)
     * The measurement units are not imported yet if it is set to true, we might want to
     * add this at a later stage.
     */
    public TreeNode createNode(boolean hasUnits, boolean hasLeaves) {

        if (hasLeaves && isLeaf()) {
            // i am a leaf, so create a Leaf.
            // this is called only if hasUnits == false, see below.
            // So we don't need to check that again.
            Leaf leaf = new Leaf();
            setNameAndWeight(leaf);
            setDescription(leaf);
            setMIU(leaf);
            return leaf;
        } else {
            // We start with assuming that I'm a Node
            TreeNode node =  new eu.scape_project.planning.model.tree.Node();

            setNameAndWeight(node);
            setDescription(node);
            for (Node n : children) {
                if (hasLeaves && n.isLeaf()) {
                    // Case 1: we don't have units in the tree, so n is in fact a LEAF
                    // and I am a NODE as assumed above
                    if (!hasUnits) {
                        Leaf leaf = new Leaf();
                        n.setNameAndWeight(leaf);
                        n.setDescription(leaf);
                        n.setMIU(leaf);
                        ((eu.scape_project.planning.model.tree.Node)node).addChild(leaf);
                    } else {
                        // Case 2: we have units, so n is the SCALE of myself
                        // - but this means that I AM a LEAF
                        // and that we can finish recursion
                        node = new Leaf();
                        setNameAndWeight(node);
                        setDescription(node);
                        Leaf leaf = (Leaf) node;
                        leaf.changeScale(n.createScale());
                        setMIU(leaf);
                        
                        return leaf; // == node
                    }
                } else {
                    ((eu.scape_project.planning.model.tree.Node)node).addChild(n.createNode(hasUnits,hasLeaves));
                }

            }
            return node;
        }
    }

    public PolicyNode createPolicyNode() {

        if (isLeaf()) {

            Policy p = new Policy();
            p.setValue(getTEXT());
            return p;

        } else {
            PolicyNode node = new PolicyNode();

            node.setName(getTEXT());

            for (Node n : children) {
                if (n.isLeaf()) {
                    Policy p = new Policy();
                    p.setValue(n.getTEXT());
                    //Policy p = (Policy)n.createPolicyNode();
                    p.setName(getTEXT());
                    return p;
                } else {
                    node.addChild(n.createPolicyNode());
                }
            }

            return node;
        }
    }
    
    /**
     * Method responsible for converting this node (and its successors) into corresponding CriteriaTreeNodes.
     * 
     * @param criteriaManager Class used to map CriteriaLeaves to correct criterion.
     * @return Node (and its successors) converted into corresponding a CriteriaTreeNodes.
     */
    public CriteriaTreeNode createCriteriaTreeNode(CriteriaManager criteriaManager) {
    	// Leaf
        if (isLeaf()) {
            CriteriaLeaf leaf = new CriteriaLeaf();

            // Hint: If the node is mapped its text has a specific format (my property name|outcome:format/image/width)
            // We have to check this
    		int stringLength = TEXT.length();
    		int mappingSeparatorIndex = TEXT.lastIndexOf('|');
    		
    		// node has no mapping specified 
    		if (mappingSeparatorIndex == -1 || mappingSeparatorIndex == (stringLength - 1)) {
    			leaf.setMapped(false);
    			leaf.setName(TEXT);
    		}
    		// node has mapping specified
    		else {
    			String name = TEXT.substring(0, mappingSeparatorIndex);
    			String criterionUri = TEXT.substring(mappingSeparatorIndex + 1);
    			leaf.setName(name);
    			// set criterion only if we can identify it
    			Measure crit = criteriaManager.getMeasure(criterionUri);
    			if (crit != null) {
    				leaf.setMapped(true);
    				leaf.setCriterion(crit);
    			}
    			else {
	    			leaf.setMapped(false);
    			}
    		}
    		
    		return leaf;
    	// Node
        } else {
            CriteriaNode node = new CriteriaNode();

            node.setName(TEXT);

            for (Node n : children) {
            	node.addChild(n.createCriteriaTreeNode(criteriaManager));
            }

            return node;
        }
    }

    /**
     * sets the name and weight of a TreeNode from MY members
     * @param node
     */
    private void setNameAndWeight(TreeNode node) {
        int k = TEXT.indexOf("%");
        double weight = 0.0;
        if (k != -1) {
            try {
                // we have weighting in the tree:
                // remove and set weighting, continue.
                if ( k == TEXT.length()-1) {
                    int l = TEXT.lastIndexOf(' ');
                    weight = Double.parseDouble(TEXT.substring(l+1,k).trim()) / 100;
                    setTEXT(TEXT.substring(0,l));
                } else {
                    weight = Double.parseDouble(TEXT.substring(0,k)) / 100;
                    setTEXT(TEXT.substring(k+1));
                }
            } catch (Exception e) {
                log.error("could not import weighting: "+e.getMessage(),e);
            }

        }
        node.setWeight(weight);
        node.setName(TEXT);
    }

    /**
     * sets the description of a provided Treenode from MY {@link #DESCRIPTION}
     * and takes care to remove potentiall included measuredProperty=xxx from this description
     * @param node
     */
    public void setDescription(TreeNode node) {
        if (DESCRIPTION == null) {
            return;
        }
        String key = "measureId=";
        int idx = DESCRIPTION.indexOf(key);
        if (idx > -1) {
            int newLineIndex = DESCRIPTION.indexOf('\n');
            if (newLineIndex > -1) {
                node.setDescription(DESCRIPTION.substring(newLineIndex+1));
            }
        } else {
            node.setDescription(DESCRIPTION);
        }
    }
    
    /**
     * sets the measured property on a provided Leaf (!!) from my {@link #DESCRIPTION}
     * @param leaf
     */
    public void setMIU(Leaf leaf) {
        String descr = DESCRIPTION;
        if (descr != null) {
            String key = "measureId=";
            int idx = descr.indexOf(key);
            if (idx > -1) {
                int endIdx = descr.indexOf("\n", idx+key.length());
                if (endIdx == -1) {
                    endIdx = descr.length();
                } else {
                    endIdx = endIdx-1;
                }
                String mInfo = descr.substring(idx+key.length(), endIdx);
                try {
                    // TODO: Do we really need and use this functionality?
                    //leaf.getCriterion().fromUri(mInfo);
                } catch (IllegalArgumentException e) {
                    log.debug("Invalid measurement info for leaf: " +  TEXT + "=" + mInfo);
                }
                int newLineIndex = DESCRIPTION.indexOf('\n');
                if (newLineIndex > -1) {
                    descr = DESCRIPTION.substring(newLineIndex+1);
                } else {
                    descr = "";
                }
            }
        }
           
    }

    public void addChild(Node n) {
        children.add(n);
        n.setParent(this);
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public String getCREATED() {
        return CREATED;
    }

    public void setCREATED(String created) { 
        CREATED = created;
    }

    public String getMODIFIED() {
        return MODIFIED;
    }

    public void setMODIFIED(String modified) {
        MODIFIED = modified;
    }

    public String getTEXT() {
        return TEXT;
    }

    public void setTEXT(String text) {
        TEXT = text;
    }
}
