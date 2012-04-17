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
package eu.planets_project.pp.plato.model.tree;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.slf4j.LoggerFactory;

import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.EvaluationStatus;
import eu.planets_project.pp.plato.validation.ValidationError;

/**
 * A node in the objective tree contains children and does not contain any
 * measurement unit and values. Part of our implementation of the Composite
 * Design Pattern, cf. TreeNode and Leaf - Node corresponds to the
 * <code>Composite</code>
 * This will be named REQUIREMENT with the next major release.
 * CRITERIA are then the measurable requirements, i.e. the now-called Leaves.
 * @see Leaf
 * @see TreeNode
 * @author Christoph Becker
 */
@Entity
@DiscriminatorValue("N")
public class Node extends TreeNode {

    private static final long serialVersionUID = -4323424291922910124L;

    /**
     * adds the provided node as a child to this.
     * @param n the node to be added as a child
     */
    public void addChild(TreeNode n) {
        children.add(n);
        n.setParent(this);
    }

    /**
     * removes the provided node from the list of children.
     * This does <b>not</b> check if the node actually <em>is</em> a child,
     * so if it is not, it loses its parent reference. 
     * @param n
     */
    public void removeChild(TreeNode n) {
        children.remove(n);
        n.setParent(null);
    }

    /**
     * @param n
     */
    void convertChild(Node n) {
        Leaf l = new Leaf();
        l.setName(n.getName());
        children.set(children.indexOf(n), l);
        n.setParent(this);
    }

    /**
     * @param l
     */
    void convertChild(Leaf l) {
        Node n = new Node();
        n.setName(l.getName());
        children.set(children.indexOf(l), n);
        n.setParent(this);
    }

    /**
     * converts this Node to a Leaf 
     */
    public void convert() {
        TreeNode n = getParent();
        if (n != null) {
            n.convertChild(this);
        } else {
        	LoggerFactory.getLogger(this.getClass()).warn(
                    "convert called on root node - ignoring");
        }
    }


    /**
     * empty default constructor
     */
    public Node() {
    }

    @Override
    /**
     * @return false
     */
    public boolean isLeaf() {
        return false;
    }

    /**
     * unused atm, to be used in Plato 2.2+ 
     */
    public EvaluationStatus getEvaluationStatus() {
        int missing = 0;
        int complete = 0;

        for (TreeNode n : children) {
            if (n.getEvaluationStatus() == EvaluationStatus.NONE) {
                missing++;
            } else if (n.getEvaluationStatus() == EvaluationStatus.COMPLETE) {
                complete++;
            }
        }

        if (missing == children.size()) {
            return EvaluationStatus.NONE;
        } else if (complete == children.size()) {
            return EvaluationStatus.COMPLETE;
        } else {
            return EvaluationStatus.PARTLY;
        }

    }

    /**
     * Checks if there children of this node with duplicate names
     * @param errors A list of error messages
     * @return true if two children have the same name
     */
    private boolean hasDuplicates(List<ValidationError> errors) {
        boolean duplicates = false;
        Collection<String> childNames = new HashSet<String>();
        for (TreeNode node : this.children) {
            if (childNames.contains(node.getName())) {
                errors.add(new ValidationError("Node '" + this.getName()
                        + "' has several children with the name '" + node.getName() + "'", this));
                duplicates = true;
            } else {
                childNames.add(node.getName());
            }
        }
        return duplicates;
    }

    /**
     * Checks if this node is completely specified.
     * I.e., if there are no children, something is wrong - 
     * and if there are children with duplicate names, something is wrong too.
     * If not, we return true.
     * @param errors list to which to append validation errors
     * @see TreeNode#isCompletelySpecified(List<ValidationError>)
     * @see Leaf#isCompletelySpecified(List<ValidationError>)
     */
    @Override
    public boolean isCompletelySpecified(List<ValidationError> errors) {
        boolean noError = true;
        if (this.children.size() == 0) {
        	errors.add(new ValidationError("Node " + this.getName() + " has no Children.", this));
            noError = false;
        }
        if (this.hasDuplicates(errors)) {
            noError = false;
        }
        return noError;
    }

    @Override
    /**
     * unused - we go directly down to allLeaves(). 
     * TODO This should actually check and aggregate the evaluation status of the children
     * @return true (always!)
     */
    public boolean isCompletelyEvaluated(List<Alternative> alternatives,
            List<ValidationError> errors) {
        return true;
    }

    /**
     * unused - we go directly down to allLeaves(). 
     * TODO This should actually check and aggregate the transformation
     * status of the children
     * @return true (always!)
     */
    @Override
    public boolean isCompletelyTransformed(List<ValidationError> errors) {
        return true;
    }

    /**
     * checks the weighting of the children
     * @return true if the sum of weights of the children is equal to 100 percent.
     */
    @Override
    public boolean isCorrectlyWeighted(List<ValidationError> errors) {
        /*
         * Because IEEE Floating-Point-Arithmetic is inherently funny (did you
         * know that 0.09*10 != 0.9?) we have to add up the weights*100 rounded
         * to longs and check whether their sum is 100!
         */
        Integer sum = 0;
        for (TreeNode child : this.children) {
            sum += (int)Math.round(100.0 * child.getWeight());
        }
        if (sum.equals(100)) {
            return true;
        }
        errors.add(new ValidationError("The sum of the weights of node " + this.getName()
                + "'s children is not 1 (but " + sum / 100.0 + ").", this));
        return false;
    }
    
    /**
     * Returns a clone of self. Does not clone more than super
     * @see TreeNode#clone()
     */
    @Override
    public TreeNode clone() {
        Node clone = (Node) super.clone();
        return clone;
    }


    public void normalizeWeights(boolean recursive) {
        // first determine the sum of weights of all direct children
        double weightSum = 0;
        for (TreeNode child : children) {
            weightSum += child.getWeight();
        }

        // if the sum is 0 then spread the weight equally (= 1 / number of
        // children)
        if (weightSum == 0) {
            for (TreeNode child : children) {
                child.setWeight(1 / children.size());
            }
        } else {
            // otherwise normalize the weights - divide all weights
            // with the total sum -> this way the new sum will be = 1;
            // TODO: handle crazy floating point arithmetics in java
            // (precision is futile, you will be approximated!).
            for (TreeNode child : children) {
                child.setWeight(child.getWeight()/ weightSum);
            }
        }
        
        // apply this function recursively to all children (if selected)
        if (recursive) {
            for (TreeNode child : children) {
                child.normalizeWeights();
            }
        }
    }
}
