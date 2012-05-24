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
package eu.scape_project.planning.model.tree;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.Valid;

import eu.scape_project.planning.model.measurement.CriterionCategory;

@Entity
public class LibraryTree implements Serializable {

    private static final long serialVersionUID = -8945252751698566747L;

    @Id
    @GeneratedValue
    private int id;
    
    private String name;
    
    @Valid
    @OneToOne(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    private LibraryRequirement root;
    
    public LibraryTree() {
    }
    
    public void addMainRequirements() {
        name = "RequirementsLibrary";
        
        root = new LibraryRequirement();
        root.setName("Requirements");
        root.setPredefined(true);
        // add predefined nodes
        LibraryRequirement action = root.addRequirement();
        action.setName("Action");
        action.setPredefined(true);

        LibraryRequirement r = action.addRequirement();
        r.setCategory(CriterionCategory.ACTION);
        r.setName(CriterionCategory.ACTION.toString());
        r.setPredefined(true);
        
        LibraryRequirement obj = root.addRequirement();
        obj.setName("Object");
        obj.setPredefined(true);

        r = obj.addRequirement();
        r.setCategory(CriterionCategory.OUTCOME_EFFECT);
        r.setName(CriterionCategory.OUTCOME_EFFECT.toString());
        r.setPredefined(true);

        r = obj.addRequirement();
        r.setCategory(CriterionCategory.OUTCOME_FORMAT);
        r.setName(CriterionCategory.OUTCOME_FORMAT.toString());
        r.setPredefined(true);
        
        r = obj.addRequirement();
        r.setCategory(CriterionCategory.OUTCOME_OBJECT);
        r.setName(CriterionCategory.OUTCOME_OBJECT.toString());
        r.setPredefined(true);        
    }

    public int getId() {
        return id;        

    }

    public void setId(int id) {
        this.id = id;
    }

    public LibraryRequirement getRoot() {
        return root;
    }

    public void setRoot(LibraryRequirement root) {
        this.root = root;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
