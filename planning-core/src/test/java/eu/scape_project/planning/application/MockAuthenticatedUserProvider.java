package eu.scape_project.planning.application;

import java.io.Serializable;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import eu.scape_project.planning.model.User;

@Mock
@SessionScoped
@Stateful
public class MockAuthenticatedUserProvider implements Serializable, IAuthenticatedUserProvider {
    private static final long serialVersionUID = 1L;

    private User user;

    @Produces
    @Named("user")
    public User getUser() {
        if (user == null) {
            user = new User();
            user.setFirstName("Testing");
            user.setLastName("Plato");
        }
        return user;
    }
}
