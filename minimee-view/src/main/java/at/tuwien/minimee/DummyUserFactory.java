package at.tuwien.minimee;

import java.io.Serializable;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import eu.planets_project.pp.plato.model.User;

/**
 * creates one(!) user for the session
 * this will be replaced by a login bean 
 * 
 * @author kraxner
 *
 */
@SessionScoped
public class DummyUserFactory implements Serializable {
	private static final long serialVersionUID = -830549797293803656L;
	
	private User user;
	
	@Produces
	@Named // we will need it in the view 
	public User getUser() {
		if (user == null) {
			user = new User();
			user.setUsername("admin");
		}
		return user;
	}
	
	
}
