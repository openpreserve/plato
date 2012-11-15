package eu.scape_project.pw.planning.application;

import java.io.Serializable;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import eu.scape_project.planning.model.User;

@SessionScoped
@Stateful
public class MockSessionScopeProducer implements Serializable {
    private static final long serialVersionUID = 1L;

    private User user;

    @Produces
    @MockedUser
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
