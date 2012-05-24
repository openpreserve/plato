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
package eu.scape_project.planning.model.measurement;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import eu.scape_project.planning.model.scales.FreeStringScale;
import eu.scape_project.planning.model.scales.PositiveFloatScale;
import eu.scape_project.planning.model.values.FreeStringValue;
import eu.scape_project.planning.model.values.PositiveFloatValue;
import eu.scape_project.planning.model.values.Value;

@Entity
public class Measurement implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1189511961248081431L;

    @Id
    @GeneratedValue
    private int id;
    
    @ManyToOne(cascade={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private MeasurableProperty property;
    
    @ManyToOne(cascade=CascadeType.ALL)
    private Value value;
    
    public Measurement() {
        
    }
    
    public Measurement(String propertyName, String value) {
        MeasurableProperty p = new MeasurableProperty();
        p.setName(propertyName);
        p.setScale(new FreeStringScale());
        FreeStringValue s = (FreeStringValue) p.getScale().createValue();
        s.setValue(value);
        this.setProperty(p);
        this.setValue(s);
    }
    
    public Measurement(String propertyName, double value) {
        MeasurableProperty p = new MeasurableProperty();
        p.setName(propertyName);
        p.setScale(new PositiveFloatScale());
        PositiveFloatValue v = (PositiveFloatValue) p.getScale().createValue();
        v.setValue(value);
        this.setProperty(p);
        this.setValue(v);
    }
    
    public MeasurableProperty getProperty() {
        return property;
    }
    public void setProperty(MeasurableProperty property) {
        this.property = property;
    }
    public Value getValue() {
        return value;
    }
    public void setValue(Value value) {
        this.value = value;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
