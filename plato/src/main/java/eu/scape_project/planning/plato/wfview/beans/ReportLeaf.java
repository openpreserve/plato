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
