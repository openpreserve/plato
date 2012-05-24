/*******************************************************************************
 * Copyright 2012 Vienna University of Technology
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
package eu.scape_project.planning.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.IndexColumn;

import eu.scape_project.planning.model.tree.ITreeNode;
import eu.scape_project.planning.model.tree.ITreeWalker;

@Entity
@Inheritance
@DiscriminatorColumn(name = "policy")
public class PolicyNode implements Serializable, Cloneable, ITreeNode {

    private static final long serialVersionUID = -4900363865139288082L;

    @Id
    @GeneratedValue
    protected int id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_fk", insertable = false, updatable = false)
    protected PolicyNode parent;
   
    @Valid
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)    
    @JoinColumn(name = "parent_fk")
    @IndexColumn(name="indexcol",base=1)
    protected List<PolicyNode> children = new ArrayList<PolicyNode>();

    /**
     * Indicates it this node is a leaf-node and therefore a concrete policy.
     * @return True if this node is a leaf-node and therefore a concrete policy.
     */
    public boolean isPolicy() {
    	return false;
    }
    
    public boolean isLeaf() {
    	return isPolicy();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addChild(PolicyNode p) {
        children.add(p);
        p.setParent(this);
    }
    
    public List<PolicyNode> getChildren() {
        return children;
    }

    public void setChildren(List<PolicyNode> children) {
        this.children = children;
        for (PolicyNode p : children) {
            p.setParent(this);
        }
    }

    public PolicyNode getParent() {
        return parent;
    }

    public void setParent(PolicyNode parent) {
        this.parent = parent;
    }
    
    public PolicyNode clone() {
        
        try {
            PolicyNode clone = (PolicyNode) super.clone();
            
            clone.id = 0;

            clone.setParent(null);
            
            if (this.getChildren() != null) {
                List<PolicyNode> clonedChildren = new ArrayList<PolicyNode>();
                for (PolicyNode child : this.getChildren()) {
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

	@Override
	public void walkTree(ITreeWalker treeWalker) {
    	treeWalker.walk(this);
    	for (PolicyNode node : children) {
    		node.walkTree(treeWalker);
    	}
	}
    
}
