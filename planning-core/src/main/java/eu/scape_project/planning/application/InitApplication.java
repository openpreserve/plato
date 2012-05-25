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
package eu.scape_project.planning.application;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import eu.scape_project.planning.model.Organisation;
import eu.scape_project.planning.model.Role;
import eu.scape_project.planning.model.User;

/**
 * Class responsible for initializing the application and setting up required data  
 * 
 * @author Michael Kraxner
 *
 */
@Singleton
@Startup
public class InitApplication implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@PersistenceContext private EntityManager em;
	
	@PostConstruct
	public void init(){
		createDefaultAdmin();
	}
	
	/**
	 * Creates an admin user, if missing.
	 * This is only a temporary workaround until we have a user management.
	 * 
	 */
	private void createDefaultAdmin(){
		Object existingUser;
		try {
			existingUser = em.createQuery("SELECT u From User u WHERE u.username = 'admin'").getSingleResult();
		} catch (Exception e) {
			existingUser = null;
		}
		
		if (existingUser == null) {
			Organisation organisation = new Organisation();
			organisation.setName("UT Vienna");
			em.persist(organisation);
			
			User user = new User();
			user.setUsername("admin");
			user.setFirstName("admin");
			user.setLastName("admin");
			
			// set admin rights
			Role adminRole = new Role();
			adminRole.setName("admin");
			user.getRoles().add(adminRole);
			user.setOrganisation(organisation);
			
			em.persist(user);
		}
	}
}
