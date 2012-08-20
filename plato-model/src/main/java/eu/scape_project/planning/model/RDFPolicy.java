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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;

import org.hibernate.validator.constraints.Length;

@Entity
public class RDFPolicy implements Serializable {

	private static final long serialVersionUID = 61731856550176987L;

	@Id
	@GeneratedValue
	private int id;

	@Length(max = 2000000)
	@Column(length = 2000000)
	private String policy;

	private Date dateCreated;

	/**
	 * Fills the date created before persisting the object.
	 */
	@SuppressWarnings("unused")
	@PrePersist
	private void fillDateCreated() {
		dateCreated = new Date();
	}

	// ---------- getter/setter ----------
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	public Date getDateCreated() {
		return dateCreated;
	}
}
