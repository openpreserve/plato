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
import javax.persistence.Transient;

import eu.scape_project.planning.model.util.FloatFormatter;

@Entity
@DiscriminatorValue("Z")
public class FloatRangeValue extends Value  implements INumericValue {
    private static final long serialVersionUID = -4393438962093545787L;

    @Transient
    private FloatFormatter formatter;

    @Column(name = "floatrange_value")
    private double value;


    public double value() {
        return value;
    }

    @Override
    public String toString() {
        if (formatter == null) {
            formatter = new FloatFormatter();
        }
        return formatter.formatFloatPrecisly(value);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String getFormattedValue() {
        if (formatter == null) {
            formatter = new FloatFormatter();
        }
        return formatter.formatFloat(value);
    }    
    
    @Override
    public void parse(String text) {
        setValue(Double.parseDouble(text));        
    }

}
