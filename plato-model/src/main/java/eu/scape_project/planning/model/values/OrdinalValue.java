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
@DiscriminatorValue("O")
public class OrdinalValue extends Value implements IOrdinalValue  {

    private static final long serialVersionUID = 8774873647428936457L;

    @Column(name = "ordinal_value")
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
    
    @Override
    public String getFormattedValue() {
        return value;
    }
    @Override
    public void parse(String text) {
        setValue(text);        
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof OrdinalValue) {
            OrdinalValue ov = (OrdinalValue) o;
            return (getValue() != null ? (getValue().equals(ov.getValue()))
                    : ov.getValue() == null);
        }
        return false;
    }
}
