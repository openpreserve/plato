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
package eu.scape_project.pw.idp.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 * User object used in the identity provider.
 */
@Entity
public class IdpRole {
    @Id
    @GeneratedValue
    private int id;
    
    @Column(unique = true)
    private String roleName;

    /**
     * User having this role assigned.
     */
    @ManyToMany(mappedBy = "roles", cascade = CascadeType.REFRESH)
    private List<IdpUser> user = new ArrayList<IdpUser>();

    // ---------- getter/setter ----------
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(final String roleName) {
        this.roleName = roleName;
    }

    public List<IdpUser> getUser() {
        return user;
    }

    public void setUser(List<IdpUser> user) {
        this.user = user;
    }
}
