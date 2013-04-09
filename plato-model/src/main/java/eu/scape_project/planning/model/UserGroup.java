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
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 * A group of users that collaborate during planning.
 */
@Entity
public class UserGroup implements Serializable {
    private static final long serialVersionUID = -3659021986541051911L;

    @Id
    @GeneratedValue
    private int id;

    private String name;

    // @OneToMany(mappedBy = "userGroup", fetch = FetchType.LAZY)
    // private List<User> users = new ArrayList<User>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RDFPolicy> policies = new ArrayList<RDFPolicy>();

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private Repository repository = new Repository();

    /**
     * Returns the policy with the latest import date.
     * 
     * @return the latest policy
     */
    public RDFPolicy getLatestPolicy() {

        if (policies.size() == 0) {
            return null;
        }

        RDFPolicy latestPolicy = null;
        Date latestDate = null;

        for (RDFPolicy policy : policies) {
            if (latestDate == null) {
                latestPolicy = policy;
                latestDate = policy.getDateCreated();
            }
            if (policy.getDateCreated().after(latestDate)) {
                latestPolicy = policy;
                latestDate = policy.getDateCreated();
            }
        }

        return latestPolicy;
    }

    // ---------- getter/setter ----------

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

    // public List<User> getUsers() {
    // return users;
    // }
    //
    // public void setUsers(List<User> users) {
    // this.users = users;
    // }

    public List<RDFPolicy> getPolicies() {
        return policies;
    }

    public void setPolicies(List<RDFPolicy> policies) {
        this.policies = policies;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

}
