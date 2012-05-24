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

package eu.scape_project.planning.plato.wfview.beans;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.values.TargetValues;


public class ReportLeaf implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -7163202702871602805L;

    public ReportLeaf(){}
    
    private Leaf leaf;
   
    private Map<Alternative, TargetValues> transformedValues = new Hashtable<Alternative, TargetValues>();
    private Map<Alternative, Double> resultValues = new Hashtable<Alternative, Double>();
    
    public ReportLeaf(Leaf leaf, List<Alternative> alternatives) {
        this.leaf = leaf;
        for (Alternative a : alternatives) {
            if (!this.leaf.isSingle()) {
                this.transformedValues.put(a, this.leaf.transformValues(a));
            }
            this.resultValues.put(a, this.leaf.getResult(a));
        }
    }
    
    public Leaf getLeaf() {
        return this.leaf;
    }
    
    public Map<Alternative, TargetValues> getTransformedValues() {
        return this.transformedValues;
    }
    
    public Map<Alternative, Double> getResultValues() {
        return this.resultValues;
    }
    
}
