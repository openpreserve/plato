/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.scape_project.planning.model;

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
import javax.persistence.Transient;

@Entity
// FIXME this needs to be outjected by a component
// @Name("user")
// @Scope(ScopeType.SESSION)
public class User implements Serializable {
    private static final long serialVersionUID = 3842938596189922641L;

    @Id
    @GeneratedValue
    private long id;

    @Column(unique = true)
    private String username;

    @Transient
    private String firstName;

    @Transient
    private String lastName;

    @Transient
    private String email;

    @Transient
    private String fullName;

    // @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    // @ManyToMany(mappedBy = "users")
    // @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST,
    // CascadeType.REFRESH}, targetEntity = Role.class, fetch = FetchType.EAGER)
    // @JoinTable(name = "user_role", joinColumns = @JoinColumn(name =
    // "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))

    @Transient
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
     * public User clone() { User u = new User(); u.setEmail(email);
     * u.setFirstName(firstName); u.setLastName(lastName);
     * u.setOrganisation(organisation); u.setPassword(password); List<Role>
     * userroles = new ArrayList<Role>(); userroles.addAll(roles);
     * u.setRoles(userroles); u.setUsername(username); return u; }
     */
}
