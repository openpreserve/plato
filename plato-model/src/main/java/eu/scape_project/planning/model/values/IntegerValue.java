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

package eu.scape_project.planning.model.values;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("I")
public class IntegerValue extends Value implements INumericValue {

    private static final long serialVersionUID = 1837519006765943657L;
    
    @Column(name = "int_value")
    private int value;

    public double value() {
        return value;
    }
    
    @Override
    public String toString() {
        return Integer.toString(value);
    }
    
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    @Override
    public void parse(String text) {
        setValue(Integer.parseInt(text));        
    }
    
}
