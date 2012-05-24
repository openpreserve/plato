package eu.scape_project.pw.planning.application;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import eu.scape_project.planning.model.Organisation;
import eu.scape_project.planning.model.Role;
import eu.scape_project.planning.model.User;

/**
 * Factory class responsible for producing/injecting session-scoped objects.
 * 
 * @author Michael Kraxner, Markus Hamm
 */
@SessionScoped
@Stateful
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
            user = getUserFromSession();
        }

        if (user == null) {
            user = getDummyUserFromDb();
        }

        return user;
    }

    private User getUserFromSession() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        // Get userprincipal
        Principal principal = request.getUserPrincipal();

        if (principal == null) {
            return null;
        }

        // Read user from DB
        User user = getUserFromDB(principal.getName());
        // Create new user object
        if (user == null) {
            user = createUser(principal.getName());
        }

        // Get attributes
        HttpSession session = request.getSession();
        Map<String, List<Object>> attributes = (Map<String, List<Object>>) session
            .getAttribute("SESSION_ATTRIBUTE_MAP");

        if (attributes != null) {
            // Set transient data from attributes
            List<Object> firstNameList = (List<Object>) attributes.get("firstName");
            if (firstNameList != null) {
                if (firstNameList.size() > 0) {
                    String firstName = (String) firstNameList.get(0);
                    user.setFirstName(firstName);
                }
            }

            List<Object> lastNameList = (List<Object>) attributes.get("lastName");
            if (lastNameList != null) {
                if (lastNameList.size() > 0) {
                    String lastName = (String) lastNameList.get(0);
                    user.setLastName(lastName);
                }
            }

            List<Object> emailList = (List<Object>) attributes.get("email");
            if (emailList != null) {
                if (emailList.size() > 0) {
                    String email = (String) emailList.get(0);
                    user.setEmail(email);
                }
            }
        }

        ArrayList<Role> roles = new ArrayList<Role>();
        if (request.isUserInRole("authenticated")) {
            Role role = new Role();
            role.setName("authenticated");
            roles.add(role);

        }
        if (request.isUserInRole("admin")) {
            Role role = new Role();
            role.setName("admin");
            roles.add(role);

        }
        user.setRoles(roles);

        // try {
        // Subject caller = (Subject) PolicyContext
        // .getContext("javax.security.auth.Subject.container");
        //
        // Set<Principal> principals = caller.getPrincipals();
        // for (Principal p : principals) {
        // String result = p.getName();
        // }
        //
        // CallbackHandler cbh = (CallbackHandler) PolicyContext
        // .getContext("org.jboss.security.auth.spi.CallbackHandler");
        //
        // } catch (PolicyContextException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

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

    private User getUserFromDB(String username) {
        // Get user from DB
        try {
            User user = em.createQuery("SELECT u From User u WHERE u.username = :username", User.class)
                .setParameter("username", username).getSingleResult();

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

    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        Organisation organisation = new Organisation();
        organisation.setName(username);
        user.setOrganisation(organisation);
        em.persist(organisation);
        em.persist(user);
        return user;
    }
}
