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

package eu.planets_project.pp.plato.model.scales;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import eu.planets_project.pp.plato.model.values.BooleanValue;


/**
 * Boolean value to be used in the leaves of the objective tree
 * @author Christoph Becker
 *
 */
@Entity
@DiscriminatorValue("B")
public class BooleanScale extends OrdinalScale {

    private static final long serialVersionUID = -65662892936008713L;

    public String getDisplayName() {
        return "Boolean";
    }
    
    public BooleanScale() {
        super.setRestriction("Yes/No");
        // this is a Boolean-value, the restrictions above must not be changed
        immutableRestriction = true;
    }

    /*
     * this restriction cannot be changed
     */
    @Override
    public void setRestriction(String restriction) {
    }
    
    /**
     * 
     */
    public BooleanValue createValue() {
        BooleanValue bv = new BooleanValue();
        bv.setScale(this);
        return bv;
    }
}
