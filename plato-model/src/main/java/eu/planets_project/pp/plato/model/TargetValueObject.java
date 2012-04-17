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

package eu.planets_project.pp.plato.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.validator.constraints.Range;

/**
 * Represents the target value all measured values from experiments must be mapped to.
 * Target value must be between 0.0 and 5.0.
 *
 * JPA note: need this for JPA: OrdinalTransformer.mapping
 *
 * @author Christoph Becker
 *
 */
@Entity
public class TargetValueObject implements Serializable {

    private static final long serialVersionUID = 4355479396527254031L;

    @Id
    @GeneratedValue
    private int id;

    @Range(min=0,max=5)
    private double value = 0.0;

    public TargetValueObject() {
        
    }
    
    public TargetValueObject(double value) {
        setValue(value);
    }
    
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public TargetValueObject clone() {
        TargetValueObject t = new TargetValueObject();
        t.setId(0);
        t.setValue(value);
        return t;
    }
}
