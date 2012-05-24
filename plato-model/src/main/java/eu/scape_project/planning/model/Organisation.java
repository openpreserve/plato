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
package eu.scape_project.planning.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.Valid;

import eu.scape_project.planning.model.tree.PolicyTree;

@Entity
public class Organisation implements Serializable {
    private static final long serialVersionUID = -3659021986541051911L;

    @Id
    @GeneratedValue
    private int id;

    private String name;

    @OneToMany(mappedBy="organisation")
    private List<User> user = new ArrayList<User>();
    
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private PolicyTree policyTree = new PolicyTree();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PolicyTree getPolicyTree() {
        return policyTree;
    }

    public void setPolicyTree(PolicyTree policyTree) {
        this.policyTree = policyTree;
    }

	public List<User> getUser() {
		return user;
	}

	public void setUser(List<User> user) {
		this.user = user;
	}
}
