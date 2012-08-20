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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.Valid;

import eu.scape_project.planning.model.tree.PolicyTree;

@Entity
public class UserGroup implements Serializable {
	private static final long serialVersionUID = -3659021986541051911L;

	@Id
	@GeneratedValue
	private int id;

	private String name;

	@OneToMany(mappedBy = "userGroup", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<User> users = new ArrayList<User>();

	// TODO: Remove
	@Valid
	@OneToOne(cascade = CascadeType.ALL)
	private PolicyTree policyTree = new PolicyTree();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<RDFPolicy> policies = new HashSet<RDFPolicy>();

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

	public PolicyTree getPolicyTree() {
		return policyTree;
	}

	public void setPolicyTree(PolicyTree policyTree) {
		this.policyTree = policyTree;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public Set<RDFPolicy> getPolicies() {
		return policies;
	}

	public void setPolicies(Set<RDFPolicy> policies) {
		this.policies = policies;
	}

}
