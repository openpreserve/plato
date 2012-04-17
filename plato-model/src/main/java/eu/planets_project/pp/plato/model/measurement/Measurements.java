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
package eu.planets_project.pp.plato.model.measurement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import eu.planets_project.pp.plato.model.scales.FloatScale;
import eu.planets_project.pp.plato.model.values.FloatValue;
import eu.planets_project.pp.plato.model.values.INumericValue;

@Entity
public class Measurements implements Serializable {

    private static final long serialVersionUID = -6824569989115569984L;
    @Id
    @GeneratedValue
    private int id;    

    @OneToMany(cascade = CascadeType.ALL)
    private List<Measurement> list = new ArrayList<Measurement>();

    public void addMeasurement(Measurement measurement) {
        list.add(measurement);
    }
    
    public Measurement getAverage() {
        if (list.size() == 0) {
            return null;
        }
        Measurement m = list.get(0);
        if (! m.getProperty().isNumeric()) {
            throw new IllegalArgumentException("cannot calculate average of nun-numeric value: "+m.getProperty().getName());
        }
        
        Measurement measurement = new Measurement();
        MeasurableProperty property = new MeasurableProperty();
        String propertyName = m.getProperty().getName();
        property.setName(propertyName+":accumulated:average");
        FloatScale scale = new FloatScale();
        property.setScale(scale);
        measurement.setProperty(property);
        
        scale.setUnit(""+list.size());
    
        Double d = 0.0;
        for (Measurement entry : list) {
            INumericValue value = (INumericValue) entry.getValue();
            d+= value.value();
        }
        FloatValue average = (FloatValue) new FloatScale().createValue();
        average.setValue(d/list.size());
        measurement.setValue(average);
        return measurement;
    }

    public int getSize() {
        return list.size();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Measurement> getList() {
        return list;
    }

    public void setList(List<Measurement> list) {
        this.list = list;
    }
}
