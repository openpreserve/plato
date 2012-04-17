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
import javax.persistence.Transient;


/**
 * This is the target value to which everything gets transformed
 * @author Christoph Becker
 */
@Entity
@DiscriminatorValue("T")
public class TargetValue extends Value implements Comparable<TargetValue>{

    @Transient
    protected String displayName = "Target value";
    
    private static final long serialVersionUID = 5014527595089555687L;

    public TargetValue() {
    }

    @Column(name = "target_value")
    private double value;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    public double value() {
        return value;
    }
    public int compareTo(TargetValue v) {
        return Double.valueOf(this.getValue()).compareTo(v.getValue());
    }
    
    @Override
    public void parse(String text) {
        setValue(Double.parseDouble(text));
    }
    
}
