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

/**
 * Generic class holding a parameter defined by a name/value pair.
 *
 * @author Hannes Kulovits
 */
@Entity
public class Parameter implements Serializable {

    private static final long serialVersionUID = -6980010277762919238L;

    @Id
    @GeneratedValue
    private int id;
    
    /**
     * Name of the parameter
     */
    private String name;

    /**
     * Value of parameter with certain {@link #name}
     */
    private String value;
    
    public Parameter() {};
    
    public Parameter(String name, String value){
        this.name = name;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
