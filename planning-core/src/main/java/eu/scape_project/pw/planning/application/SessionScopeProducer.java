package eu.scape_project.pw.planning.application;

import java.io.Serializable;
import java.util.ArrayList;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.xml.registry.infomodel.Organization;

import eu.planets_project.pp.plato.model.Organisation;
import eu.planets_project.pp.plato.model.User;

/**
 * Factory class responsible for producing/injecting session-scoped objects.
 * 
 * @author Michael Kraxner, Markus Hamm
 */
@SessionScoped
public class SessionScopeProducer implements Serializable {
	private static final long serialVersionUID = -830549797293803656L;
	
	private User user;
	
	@PersistenceContext private EntityManager em;
	
	public SessionScopeProducer() {
		user = null;
	}
	
	@Produces @Named("user")
	public User getUser() {
		// TODO: Replace this by correct code after login-functionality exists.

		if (user == null) {
			user = getDummyUserFromDb();
		}
		
		return user;
	}
	
	private User getDummyUserFromDb() {
		Object dbResult;
		try {
			dbResult = em.createQuery("SELECT u From User u WHERE u.username = 'admin'").getSingleResult();
			return (User) dbResult;
		} catch (NoResultException e1) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}
