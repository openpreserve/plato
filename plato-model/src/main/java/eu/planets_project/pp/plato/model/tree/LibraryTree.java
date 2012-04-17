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

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.Valid;

import eu.planets_project.pp.plato.model.measurement.CriterionCategory;

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
