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
package eu.scape_project.pw.idp;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.picketlink.identity.federation.core.interfaces.AttributeManager;

import eu.scape_project.pw.idp.model.IdpUser;
import eu.scape_project.pw.idp.model.IdpUserState;

/**
 * Attribute manager for the user.
 */
@Stateless
public class UserAttributeManager implements AttributeManager {

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
}
