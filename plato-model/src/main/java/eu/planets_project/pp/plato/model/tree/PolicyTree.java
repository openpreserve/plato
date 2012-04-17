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
package eu.planets_project.pp.plato.model.tree;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import eu.planets_project.pp.plato.model.PolicyNode;

@Entity
public class PolicyTree implements Serializable {

    private static final long serialVersionUID = -5549892793184360859L;

    @Id
    @GeneratedValue
    private int id;

    @Valid
    @ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    private PolicyNode root;

    public boolean isPolicyTreeDefined() {
        return root != null;
    }

    public PolicyTree() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PolicyNode getRoot() {
        return root;
    }

    public void setRoot(PolicyNode root) {
        this.root = root;
    }
}
