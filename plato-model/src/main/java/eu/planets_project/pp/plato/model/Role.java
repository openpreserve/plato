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

//@Entity
public class Role implements Serializable, Comparable<Role> {

    /**
     * 
     */
    private static final long serialVersionUID = 2613722499444439263L;

    // @Id
    // @GeneratedValue
    // private int id;

    private String name;

    // public int getId() {
    // return id;
    // }
    //
    // public void setId(int id) {
    // this.id = id;
    // }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compareTo(Role r) {
        if (r != null && r.getName().equals(name)) {
            return 0;
        }
        return 1;
    }

    public boolean equals(Object o) {
        return (o instanceof Role && ((Role) o).getName().equals(name));
    }
}
