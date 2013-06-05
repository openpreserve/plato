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
package eu.scape_project.pw.idp.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IdpUserTest {

    private static EntityManagerFactory emFactory;
    private static EntityManager em;

    @Before
    public void setUp() {
        emFactory = Persistence.createEntityManagerFactory("idpdbtest");
        em = emFactory.createEntityManager();
    }

    @After
    public void tearDown() {
        em.close();
        emFactory.close();
    }

    // Create
    @Test
    public void persistAndRetrieveUserWithRelations() throws Exception {
        // ----- set-up -----

        // add test-user and relating objects
        IdpRole role0 = new IdpRole();
        role0.setRoleName("role0");
        IdpRole role1 = new IdpRole();
        role1.setRoleName("role1");
        IdpUser user = createUser("testUser", role0, role1);

        user.getRoles().add(role0);
        user.getRoles().add(role1);

        em.getTransaction().begin();
        em.persist(role0);
        em.persist(role1);
        em.persist(user);
        em.getTransaction().commit();

        em.clear();

        // ---- test -----
        IdpUser fetchedUser = (IdpUser) em.createQuery("SELECT u FROM IdpUser u WHERE u.username = :username")
            .setParameter("username", "testUser").getSingleResult();

        assertNotNull(fetchedUser);
        assertEquals(user.getUsername(), fetchedUser.getUsername());
        assertEquals(null, fetchedUser.getPlainPassword());
        assertEquals("a029d0df84eb5549c641e04a9ef389e5", fetchedUser.getPassword());
        assertEquals(user.getFirstName(), fetchedUser.getFirstName());
        assertEquals(user.getLastName(), fetchedUser.getLastName());
        assertEquals(user.getFullName(), fetchedUser.getFullName());
        assertEquals(user.getEmail(), fetchedUser.getEmail());
        assertEquals(user.getStatus(), fetchedUser.getStatus());
        assertEquals(user.getActionToken(), fetchedUser.getActionToken());
        assertTrue(user.getRoles().contains(role0));
        assertTrue(user.getRoles().contains(role1));
    }

    // Update
    @Test
    public void updateAndRetrieveUserWithRelations() throws Exception {
        // ----- set-up -----

        // add test-user and relating objects
        IdpRole role0 = new IdpRole();
        role0.setRoleName("role0");
        IdpRole role1 = new IdpRole();
        role1.setRoleName("role1");
        IdpUser user = createUser("testUser", role0, role1);

        em.getTransaction().begin();
        em.persist(role0);
        em.persist(role1);
        em.persist(user);
        em.getTransaction().commit();
        em.clear();
        // ---- test -----

        // update user
        em.getTransaction().begin();
        user.getRoles().remove(role0);
        user.setFirstName("Markus");
        user.setPlainPassword("newPassword");
        em.persist(em.merge(user));
        em.getTransaction().commit();
        em.clear();

        // see if update was successful
        IdpUser fetchedUser = (IdpUser) em.createQuery("SELECT u FROM IdpUser u WHERE u.username = :username")
            .setParameter("username", "testUser").getSingleResult();

        assertNotNull(fetchedUser);
        assertEquals(user.getUsername(), fetchedUser.getUsername());
        assertEquals(null, fetchedUser.getPlainPassword());
        assertEquals("a029d0df84eb5549c641e04a9ef389e5", fetchedUser.getPassword());
        assertEquals(user.getFirstName(), fetchedUser.getFirstName());
        assertEquals(user.getLastName(), fetchedUser.getLastName());
        assertEquals(user.getFullName(), fetchedUser.getFullName());
        assertEquals(user.getEmail(), fetchedUser.getEmail());
        assertEquals(user.getStatus(), fetchedUser.getStatus());
        assertEquals(user.getActionToken(), fetchedUser.getActionToken());
        assertTrue(user.getRoles().contains(role1));
        assertFalse(user.getRoles().contains(role0));
    }

    // Delete
    @Test
    public void deleteUserWithAssignedRoles_shouldNotDeleteRolesAsWell() throws Exception {
        // ----- set-up -----

        // add test-user and relating objects
        IdpRole role0 = new IdpRole();
        role0.setRoleName("role0");
        IdpRole role1 = new IdpRole();
        role1.setRoleName("role1");
        IdpUser user = createUser("testUser", role0, role1);

        em.getTransaction().begin();
        em.persist(role0);
        em.persist(role1);
        em.persist(user);
        em.getTransaction().commit();
        em.clear();
        // ---- test -----

        deleteUserIfExistent("testUser");

        IdpRole fetchedAdminRole = em.find(IdpRole.class, role0.getId());
        IdpRole fetchedManagerRole = em.find(IdpRole.class, role1.getId());

        assertNotNull(fetchedAdminRole);
        assertNotNull(fetchedManagerRole);
    }

    private IdpUser createUser(String username, IdpRole... roles) {

        IdpUser user = new IdpUser();
        user.setUsername(username);
        user.setPlainPassword("mypass");
        user.setFirstName("Max");
        user.setLastName("Mustermann");
        user.setEmail(username + "@mustermann.at");
        user.setStatus(IdpUserState.CREATED);
        user.setActionToken("uid-123-uid-456");

        for (IdpRole role : roles) {
            user.getRoles().add(role);
        }

        return user;
    }

    private void deleteUserIfExistent(String username) {
        // delete probably conflicting test-user
        em.getTransaction().begin();

        List<IdpUser> userToDelete = em
            .createQuery("SELECT u FROM IdpUser u WHERE u.username = :username", IdpUser.class)
            .setParameter("username", username).getResultList();

        for (IdpUser delUser : userToDelete) {
            em.remove(delUser);
        }

        em.getTransaction().commit();
    }

    private void deleteRoleIfExistent(String roleName) {
        // delete probably conflicting test-user
        em.getTransaction().begin();

        List<IdpRole> rolesToDelete = em
            .createQuery("SELECT r FROM IdpRole r WHERE r.roleName = :roleName", IdpRole.class)
            .setParameter("roleName", roleName).getResultList();

        for (IdpRole delRole : rolesToDelete) {
            em.remove(delRole);
        }

        em.getTransaction().commit();
    }
}
