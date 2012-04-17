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

package eu.planets_project.pp.plato.model.values;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("P")
public class PositiveIntegerValue extends Value  implements INumericValue {

    private static final long serialVersionUID = -8824495369506076325L;

    @Column(name = "integer_value")
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        // also save invalid values, they are checked later with isEvaluated()
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    public double value() {
        return value;
    }
    
    @Override
    public void parse(String text) {
        setValue(Integer.parseInt(text));        
    }
    
}
