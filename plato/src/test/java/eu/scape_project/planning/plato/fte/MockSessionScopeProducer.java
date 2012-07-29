package eu.scape_project.planning.plato.fte;

import java.io.Serializable;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import eu.scape_project.planning.model.User;

@Alternative
@SessionScoped
@Stateful
public class MockSessionScopeProducer implements Serializable {

	private User user;
	
	
	 @Produces
	    @Named("user")
	    public User getUser() {
		 	if (user==null) {
		 		user = new User();
		 		user.setFirstName("Testing");
		 		user.setLastName("Plato");
		 	}
		 	return user;
	    }
}
