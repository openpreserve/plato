package eu.scape_project.pw.planning.application;

import java.io.Serializable;
import java.security.Principal;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

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

    @PersistenceContext
    private EntityManager em;

    public SessionScopeProducer() {
        user = null;
    }

    @Produces
    @Named("user")
    public User getUser() {
        // TODO: Replace this by correct code after login-functionality exists.

        if (user == null) {
            user = getUserByServletRequest();
        }

        if (user == null) {
            user = getDummyUserFromDb();
        }

        return user;
    }

    /**
     * Reads the current logged in user from the ServletRequest and fetches the
     * corresponding plato specific data.
     * 
     * @return The current user
     */
    private User getUserByServletRequest() {
        // Get user principal
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        Principal principal = request.getUserPrincipal();

        if (principal == null) {
            return null;
        }

        // Get user from DB
        try {
            User user = em.createQuery("SELECT u From User u WHERE u.username = :username", User.class)
                .setParameter("username", principal.getName()).getSingleResult();

            return user;
        } catch (NoResultException e) {
            return null;
        }
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

    private User createUser(Principal principal) {
        User user = new User();        
        user.setUsername(principal.getName());
        

        return user;
    }
}
