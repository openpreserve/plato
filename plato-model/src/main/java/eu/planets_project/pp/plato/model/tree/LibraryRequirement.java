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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import eu.planets_project.pp.plato.model.measurement.Criterion;
import eu.planets_project.pp.plato.model.measurement.CriterionCategory;

@Entity
@DiscriminatorValue("LR")
public class LibraryRequirement extends Node {
    private static final long serialVersionUID = 2407351076268518335L;

    private CriterionCategory category = null;
    
    private boolean predefined = false;


    public LibraryRequirement() {
    }
    
    public CriterionCategory getCategory() {
        return category;
    }

    public void setCategory(CriterionCategory category) {
        this.category = category;
    }

    public boolean isPredefined() {
        return predefined;
    }

    public void setPredefined(boolean predefined) {
        this.predefined = predefined;
    }
    
    public Leaf addCriterion() {
        Leaf l = new Leaf();
        Criterion mInfo = l.getCriterion();
//        if ((mInfo.getScheme() == null) ||("".equals(mInfo.getScheme()))) {
//            if ((category == CriterionCategory.AJ)||
//                (category == CriterionCategory.AR)||
//                (category == CriterionCategory.AS)){
//                mInfo.setScheme("action");
//            } else {
//                mInfo.setScheme("object");
//            }
//        }
        addChild(l);
        return l;
    }
    public LibraryRequirement addRequirement(){
        LibraryRequirement r = new LibraryRequirement();
        r.setCategory(category);
        addChild(r);
        return r;
    }
}
