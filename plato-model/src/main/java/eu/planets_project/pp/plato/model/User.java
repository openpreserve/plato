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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity
//FIXME this needs to be outjected by a component
//@Name("user")
//@Scope(ScopeType.SESSION)
public class User implements Serializable {
    private static final long serialVersionUID = 3842938596189922641L;

    @Id
    @GeneratedValue
    private long id;
    
    @Column(unique=true)
    private String username;
    
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private String password;
    
    @Transient
    private String fullName;
    
    // @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    // @ManyToMany(mappedBy = "users")
    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST,
                        CascadeType.REFRESH },targetEntity=Role.class, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles = new ArrayList<Role>();
    
    @ManyToOne
    private Organisation organisation;
    
    public String getFullName() {
        return (firstName + " " + lastName);
    }
    
    public boolean hasRole(String role) {
        for (Role r : roles) {
            if (r.getName().equals(role)) {
                return true;
            }
        }
        return false;
    }
    
    
    public boolean isAdmin() {
        for (Role r : roles) {
            if ("admin".equals(r.getName())) {
                return true;
            }
        }
        return false;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }  
    
    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
    
    /*
    public User clone() {
        User u = new User();
        u.setEmail(email);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setOrganisation(organisation);
        u.setPassword(password);
        List<Role> userroles = new ArrayList<Role>();
        userroles.addAll(roles);
        u.setRoles(userroles);
        u.setUsername(username);
        return u;
    }
    */
}
