package eu.scape_project.pw.idp;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.picketlink.identity.federation.core.interfaces.AttributeManager;
import org.slf4j.Logger;

import eu.scape_project.pw.idp.model.IdpRole;
import eu.scape_project.pw.idp.model.IdpUser;
import eu.scape_project.pw.idp.model.IdpUserState;

/**
 * Attribute manager for the user.
 */
@Stateless
public class UserAttributeManager implements AttributeManager {
    @Inject
    private Logger log;

    /**
     * @see AttributeManager#getAttributes(Principal, List)
     */
    public Map<String, Object> getAttributes(Principal userPrincipal, List<String> attributeKeys) {

        if (userPrincipal == null) {
            return new HashMap<String, Object>();
        }

        HashMap<String, Object> attributes = new HashMap<String, Object>();

        try {
            InitialContext initCtx = new InitialContext();

            // Perform JNDI lookup to obtain container-managed entity manager
            EntityManagerFactory emf = (EntityManagerFactory) initCtx
                .lookup("java:jboss/attributeEntityManagerFactory");
            EntityManager em = emf.createEntityManager();

            TypedQuery<IdpUser> q = em.createQuery(
                "SELECT u FROM IdpUser u WHERE username = :username and status = :status", IdpUser.class);
            q.setParameter("username", userPrincipal.getName()).setParameter("status", IdpUserState.ACTIVE);
            IdpUser user = q.getSingleResult();

            for (String attributeKey : attributeKeys) {
                Object object = null;

                if (attributeKey.equals("username")) {
                    object = user.getUsername();
                } else if (attributeKey.equals("firstName")) {
                    object = user.getFirstName();
                } else if (attributeKey.equals("lastName")) {
                    object = user.getLastName();
                } else if (attributeKey.equals("email")) {
                    object = user.getEmail();
                }
                // else if (attributeKey.equals("userRoles")) {
                // List<IdpRole> roles = user.getRoles();
                // ArrayList<String> roleNames = new
                // ArrayList<String>(roles.size());
                // for (IdpRole role : roles) {
                // roleNames.add(role.getRoleName());
                // }
                // attributes.put(attributeKey, roleNames);
                // }

                if (object != null) {
                    attributes.put(attributeKey, object);
                }
            }

        } catch (NamingException e1) {
            return new HashMap<String, Object>();
        } catch (NoResultException e) {
            return new HashMap<String, Object>();
        }

        return attributes;
    }

    @Override
    public String toString() {
        return UserAttributeManager.class.getName();
    }
}
