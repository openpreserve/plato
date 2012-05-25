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
package eu.scape_project.planning.model.kbrowser;

import java.io.Serializable;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Inheritance
@DiscriminatorColumn(name = "nodetype")
public abstract class CriteriaTreeNode implements Serializable {
    private static final long serialVersionUID = -5616348201551634176L;

    @Id
    @GeneratedValue
    protected int id;

    protected String name;
        
    /**
     * Field indicating if this node is a leaf or not.
     * The associated get-method is abstract, to be mandatory (and so specific) in each subclass.
     */
    private Boolean leaf;
        
    @ManyToOne
    @JoinColumn(name = "parent_fk", insertable = false, updatable = false)
    private CriteriaNode parent;
               
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setParent(CriteriaNode parent) {
        this.parent = parent;
    }

    public CriteriaNode getParent() {
        return parent;
    }

    public abstract Boolean getLeaf();
}
