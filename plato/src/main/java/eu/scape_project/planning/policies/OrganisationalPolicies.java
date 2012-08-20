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
package eu.scape_project.planning.policies;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import eu.scape_project.planning.model.RDFPolicy;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.model.UserGroup;

@Stateful
@SessionScoped
public class OrganisationalPolicies implements Serializable {
	private static final long serialVersionUID = 1811189638942547758L;

	@Inject
	private Logger log;

	@Inject
	private EntityManager em;

	@Inject
	private User user;

	/**
	 * Imports a new policy to the users group.
	 * 
	 * @param input
	 *            the policy
	 * @throws IOException
	 *             if the polify could not be read
	 */
	public void importPolicy(InputStream input) throws IOException {
		String content = IOUtils.toString(input, "UTF-8");

		RDFPolicy policy = new RDFPolicy();
		policy.setPolicy(content);

		user.getUserGroup().getPolicies().add(policy);

		log.info("Imported new policy for user " + user.getUsername());
	}

	/**
	 * Clears the polify of the users group.
	 */
	public void clearPolicies() {
		user.getUserGroup().getPolicies().clear();
		log.info("Cleared policies of user " + user.getUsername());
	}

	/**
	 * Method responsible for saving the made changes.
	 */
	public void save() {
		UserGroup group = user.getUserGroup();
		user.setUserGroup(em.merge(group));

		log.info("Policies saved for user " + user.getUsername());
	}

	/**
	 * Method responsible for discarding the made changes.
	 */
	public void discard() {
		UserGroup oldUserGroup = em.find(UserGroup.class, user.getUserGroup()
				.getId());
		user.setUserGroup(oldUserGroup);

		log.info("Policies discarded for user " + user.getUsername());
	}
}
