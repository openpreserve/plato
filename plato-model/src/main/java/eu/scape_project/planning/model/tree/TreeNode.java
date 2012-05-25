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
package eu.scape_project.planning.model.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;

import org.hibernate.annotations.IndexColumn;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.ChangeLog;
import eu.scape_project.planning.model.EvaluationStatus;
import eu.scape_project.planning.model.IChangesHandler;
import eu.scape_project.planning.model.ITouchable;
import eu.scape_project.planning.validation.ValidationError;

/**
 * Base class for our composite hierarchy of nodes and leaves - 
 * TreeNode corresponds to the <code>Component</code> in the 
 * Composite Design Pattern.
 * 
 * @author Christoph Becker
 *  @see Node
 *  @see Leaf
 */
@Entity
@Inheritance
@DiscriminatorColumn(name = "nodetype")
public abstract class TreeNode implements ITreeNode, Serializable, ITouchable, Cloneable {

    private static final long serialVersionUID = 7696297425270759326L;

    @Id
    @GeneratedValue
    protected int id;

    @ManyToOne
    @JoinColumn(name = "parent_fk", insertable = false, updatable = false)
    protected TreeNode parent;

    @Lob
    protected String name;

    @Lob
    protected String description;
    
    /**
     * the children that are contained in this node.
     */
    @Valid
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_fk")
    @IndexColumn(name="indexcol",base=1)
    protected List<TreeNode> children = new ArrayList<TreeNode>();
    
    /**
     * indicates whether the weight of this node may be changed by the automatic balancing of weights.
     * If lock is true, the weight is not changed automatically.
     */
    @Column(name="locked")
    private boolean lock;

    /**
     * FIXME: why should it be more comfortable? 
     * 
     * determines if this node is going to have one single value for all
     * SampleRecords, or if each SampleObject has its own result value. This
     * only applies for the class Leaf, but it's comfier to include it here than
     * write complex statements in the view layer.
     */
    private boolean single;    

    /**
     * This should never have a value with more than two fractional (decimal) digits!
     * Kevin suggests turning this into an integer (0 <= x <= 100(0))
     */
    protected double weight = 1.0;
    
    @ManyToOne(cascade=CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();
    
    /**
     * emtpy default constructor
     */
    public TreeNode() {

    }

    /**
     * Instantiate a new TreeNode by its name and according weight.
     * @param name of the node.
     * @param weight of the node.
     */
    public TreeNode(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }
    

    public ChangeLog getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(ChangeLog changeLog) {
        this.changeLog = changeLog;
    }

    /**
     * implement in subclasses.
     */
    public void convertChild(TreeNode n) {
    }

    
//    /**
//     * transient needed because of hibernate
//     */
//    @Transient
//    private List<Leaf> allLeaves = null;
//
//    /**
//     * transient needed because of hibernate
//     */
//    @Transient
//    private int numberOfLeaves = -1;

//    /**
//     * transient needed because of hibernate
//     */
//    @Transient
//    private EvaluationStatus evaluationStatus = null;
//    
    /**
     * @return the status of evaluation of the leaves of this branch of the
     *         tree, which can be one of the values that are defined in
     *         {@link EvaluationStatus}
     * @see EvaluationStatus
     */
    public abstract EvaluationStatus getEvaluationStatus();

    /**
     * @return a List with all the leaves that are contained in this branch of
     *         the tree
     */
    public List<Leaf> getAllLeaves() {
        List<Leaf> list = new ArrayList<Leaf>();
        for (TreeNode n : children) {
            if (n instanceof Leaf) {
                Leaf leaf = (Leaf) n;
                list.add(leaf);
            } else {
                list.addAll(n.getAllLeaves());
            }
        }
        return list;
    }

    /**
     * Initializes the weights for all leaves of this TreeNode, i.e.
     * {@link #balanceNodes(List, double) balances the weights} equally.
     * This is done {@link #initWeights() recursively}.
     */
    public void initWeights() {
        if (children.isEmpty()) {
            return;
        }
        // Set weights..
        this.balanceNodes(children, 1.0);
        // ..recursively!
        for (TreeNode n : children) {
            n.initWeights();
        }
    }

     public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    /**
     * @return the number of leaves that are contained in this branch of the
     *         tree
     */
    public int getNumberOfLeaves() {
        return getAllLeaves().size();
    }


    /**
     * This returns the absolute influence this Node has on the overall weighted
     * root value. It is worthwhile displaying this in the Analysis stage -
     * and the weighting stage - to give users a feeling how much
     * influence each node has.
     */
    public double getTotalWeight() {
        return (getParent() == null) ? getWeight() : getWeight()
                * getParent().getTotalWeight();
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
    /**
     * sets the weight of this node, constraining it to [0,1].
     * Outliers are ignored, i.e. to 0 or 1, respectively.
     * @param weight the new weight to be set 
     */
    public void setWeight(double weight) {
        if (weight < 0) {
            this.weight = 0.0;
        } else if (weight > 1.0) {
            this.weight = 1.0;
        } else {
            this.weight = weight;
        }
    }

    public double getWeight() {
        return weight;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<TreeNode> getChildren() {
        return children;
    }
    

    /**
     * sets the children, and sets self as parent of each of the nodes in the provided list
     * @param children
     * @see TreeNode#setParent(TreeNode)
     */
    public void setChildren(List<TreeNode> children) {
        this.children = children;
        for (TreeNode n : children) {
            n.setParent(this);
        }
    }

    /**
     * @return if this is a {@link Leaf}, false otherwise.
     */
    public abstract boolean isLeaf();

    /**
     * returns the sibling that comes after self, or <code>null</code>, if either I am the last
     * one or I don't have a parent.
     * @return the next sibling, or null
     */
    public TreeNode getNextSibling() {
        if (parent != null) {
            return parent.getNextChild(this);
        } else {
            return null;
        }
    }

    /**
     * returns the TreeNode that comes next in the children's list after the one that is being passed
     * as parameter
     * @param n node for which the next sibling shall be returned 
     * @return the next sibling of the provided node, or null, if there is none.
     * @throws {@link IllegalArgumentException} if a foreign child is provided as a parameter
     */
    public TreeNode getNextChild(TreeNode n) {
        int index = children.indexOf(n);
        if (index == -1) {
            throw new IllegalArgumentException(
                    "This node is not my child. And I don't even think of adopting it.");
        }
        if (index == children.size()) {
            return null;
        } else {
            return children.get(index + 1);
        }
    }

    
    /**
     * inits the values of all children
     * @param list the alternatives for which values shall be created
     * @param records the number of sample objects
     * @param initLinkage indicates whether the linkage between scales and values shall be created.
     * @see Leaf#initValues(List, int, boolean)
     */
    public void initValues(List<Alternative> list, int records, boolean initLinkage) {
        for (TreeNode n : children) {
            n.initValues(list, records,initLinkage);
        }
    }
    
    public boolean isValueMapProperlyInitialized(List<Alternative> alternatives, int numberRecords) {
        for (TreeNode n : children) {
            if (n.isValueMapProperlyInitialized(alternatives, numberRecords) == false) {
                return false;
            } 
        }
        
        return true;
    }    

    public boolean isSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    /**
     * balances the importance weightings of self and my siblings by calling self's parent.
     * @see #balanceWeights(TreeNode)
     */
    public void balanceWeights() {
        if (parent != null) {
            parent.balanceWeights(this);
        }
    }

    /**
     * balances the weights of the children according to the newly changed
     * weight of one child, and sets the changed child to locked state.
     * If the sum of locked weights plus the changed one is over 1.0, the weight 
     * of the freshly changed node is cut down to the remaining difference of locked weights to
     *  1.0. The other unlocked weights are balanced evenly.
     * @param changed the node where the weight was just changed. 
     */
    public void balanceWeights(TreeNode changed) {
        // We need to things:
        // 1. The list of nodes where the weights are not locked
        List<TreeNode> unlocked = new ArrayList<TreeNode>();
        // 2. the sum of weights of the nodes where lock is true
        double lockedWeight = 0.0;

        // We calculate these two things:
        for (TreeNode n : children) {
            if (n != changed) {
                if (n.isLock()) {
                    lockedWeight += n.getWeight();
                } else {
                    unlocked.add(n);
                }
            }
        }

        if (lockedWeight + changed.getWeight() >= 1.0) {
            /* Either the new change results in sum >= 1
             * in which case the changed gets only as much as weighting
             * as is left in the pot, or there is enough left.
             * 
             * First case treated here.
             * If the locked weights are already too high, we dont give it everything:
             * Even the unlocked nodes need a minimum of 0.01, so that amount is kept aside.
             */
            changed.setWeight(1.0 - lockedWeight - 0.01*unlocked.size());
            balanceNodes(unlocked, 0.01*unlocked.size());
        } else {
            /* Or the sum is < 1.0, which means we have to spread the 
             * remaining (1.0 - sum) evenly across the nodes that are 
             * not locked.
             * Because the weights we set have to be fully transparent to the 
             * user (because he has to make sure that the sum is 1.0 in the end)
             * we must not set a weight to something with more than two 
             * fractional (decimal!) digits!
             */
            double weightToGive = 1.0 - (lockedWeight + changed.getWeight());
            balanceNodes(unlocked, weightToGive);
        }
        // this node has been changed
        changed.touch();
        changed.setLock(true);
    }
    
    /**
     * Spreads the given weight equally among the given TreeNodes, never 
     * using more than 2 fractional (decimal) digits
     * @author Kevin Stadler
     */
    private void balanceNodes(List<TreeNode> elements, double weightToGive) {
        int i = elements.size();
        /* "i" is the number of unlocked children of this node which 
         * have not been assigned a new weight yet */
        for (TreeNode n : elements) {
            // Round it to two decimal digits
            double weight = Math.round((100.0*weightToGive)/i)/100.0;
            n.setWeight(weight);
            weightToGive -= weight;
            i--;
            // this node has been changed
            n.touch();
        }
    }

    /**
     * checks if this is a child. If it has a parent, it must be a child, so...
     * @return <code>not parent is null :)</code>
     */
    public boolean isChild() {
        return parent != null;
    }

    /**
     * checks whether this node and its children are specified completely.
     * This is overridden in Node and Leaf!
     * @param errors TODO
     * @see Node#isCompletelySpecified(List<ValidationError>)
     * @see Leaf#isCompletelySpecified(List<ValidationError>) 
     * @return false
     */
    public boolean isCompletelySpecified(List<ValidationError> errors) {
        return false;
    }

    /**
     * checks whether this node and its children are evaluated completely.
     * This is overridden in Node and Leaf!
     * @param errors TODO
     * @see Node#isCompletelyEvaluated(List, List)
     * @see Leaf#isCompletelyEvaluated(List, List)
     * @return false
     */
    public boolean isCompletelyEvaluated(List<Alternative> alternatives,
            List<ValidationError> errors) {
        return false;
    }

    /**
     * checks whether this node and its children have complete transformation settings.
     * This is overridden in Node and Leaf!
     * @param errors TODO
     * @see Node#isCompletelyTransformed(List)
     * @see Leaf#isCompletelyTransformed(List)
     * @return false
     */
    public boolean isCompletelyTransformed(List<ValidationError> errors) {
        return false;
    }
    
    /**
     * Checks whether this node is correctly weighted.
     * This is overridden in Node and Leaf!
     * @param errors TODO
     * @see Node#isCorrectlyWeighted(List)
     * @see Leaf#isCorrectlyWeighted(List)
     * @return false
     */
    public boolean isCorrectlyWeighted(List<ValidationError> errors) {
        return false;
    }

    /**
     * Touches every thing in the hierarchy:
     * this treenode, all nodes and children down to the leaves, scales, transformers
     * @see Leaf#touchAll()
     */
    public void touchAll() {
        touch();
        for (TreeNode n: children) {
            n.touchAll();
        }
    }

    /**
     * @see #touchAll()
     */
    public void touchAll(String username) {
        touch(username);
        for (TreeNode n: children) {
            n.touchAll(username);
        }
    }

    public void touch() {
        changeLog.touch();        
    }
    
    public void touch(String username) {
        changeLog.touch(username);
    }

    public boolean isChanged() {
        return changeLog.isAltered();
    }
    
    public boolean isDirty(){
        return changeLog.isDirty();
    }
        
    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    public void handleChanges(IChangesHandler h) {
        // let the changeshandler handle this instance
        h.visit(this);
        // and call handleChanges of all child elements
        for (TreeNode node : children) {
            node.handleChanges(h);
        }
    }

    /**
     * returns a clone of self.
     * Implemented for storing and inserting fragments.
     * Subclasses obtain a shallow copy by invoking this method, then 
     * modifying the fields required to obtain a deep copy of this object.
     */
    public TreeNode clone() {
        try {
            TreeNode clone = (TreeNode) super.clone();
            
            clone.id = 0;

            clone.setParent(null);
            
            // created-timestamp is automatically set to now
            clone.setChangeLog(new ChangeLog(this.getChangeLog().getChangedBy()));
            
            if (this.getChildren() != null) {
                List<TreeNode> clonedChildren = new ArrayList<TreeNode>();
                for (TreeNode child : this.getChildren()) {
                    clonedChildren.add(child.clone());
                }
                clone.setChildren(clonedChildren);
            }
            
            return clone;
        } catch (CloneNotSupportedException e) {
            // Never thrown
            return null;
        }
    }

    public void convertToNode(Leaf l) {
        Node node = new Node();
        node.setName(l.getName());
        node.setWeight(l.getWeight());
        
        children.set(children.indexOf(l), node);
        node.setParent(this);
    }
    
    public void convertToLeaf(Node n) {
        Leaf leaf = new Leaf();
        leaf.setName(n.getName());
        leaf.setWeight(n.getWeight());
        
        children.set(children.indexOf(n),leaf);
        leaf.setParent(this);
    }
    
    public Set<TreeNode> getAllParents() {
        Set<TreeNode> parents = null;
        if (parent == null) {
            parents = new HashSet<TreeNode>();
        } else {
            parents = parent.getAllParents();
            parents.add(parent);
        }
        return parents;
    }
    
    
    /**
     * Normalize the weights of this node.
     * 
     * @see #normalizeWeights()
     * 
     * @param recoursive
     *            If true, this function will be applied to this node and all
     *            its children recoursivly. Otherwise only this node will be
     *            normalized.
     */
    public abstract void normalizeWeights(boolean recursive);
    
    /**
     * Make sure the weights are normalized. 
     * This means that all values are between 0 and 1
     * and the sum of all children of a tree node is equal 1.
     * 
     * This function normalizes the weights of this node and is then applied
     * to all children recoursivly.
     */
    public void normalizeWeights() {
        normalizeWeights(true);
    }
    
    public void walkTree(ITreeWalker treeWalker) {
    	treeWalker.walk(this);
    	for (TreeNode node : children) {
    		node.walkTree(treeWalker);
    	}
    }
}
