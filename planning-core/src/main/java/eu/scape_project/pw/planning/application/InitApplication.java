package eu.scape_project.pw.planning.application;

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
