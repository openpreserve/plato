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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class IdpUserTest {

    private static EntityManagerFactory emFactory;
    private static EntityManager em;

    @BeforeClass
    public static void oneTimeSetUp() {
        emFactory = Persistence.createEntityManagerFactory("platodbTest");
        em = emFactory.createEntityManager();
    }

    @AfterClass
    public static void oneTimeTearDown() {
        em.close();
        emFactory.close();
    }

    // Create
    @Test
    public void persistAndRetrieveUserWithRelations() throws Exception {
        // ----- set-up -----
        deleteUserIfExistent("testUser");
        deleteRoleIfExistent("admin");
        deleteRoleIfExistent("manager");

        // add test-user and relating objects
        IdpRole adminRole = new IdpRole();
        adminRole.setRoleName("admin");

        IdpRole managerRole = new IdpRole();
        managerRole.setRoleName("manager");

        IdpUser user = new IdpUser();
        user.setUsername("testUser");
        user.setPlainPassword("mypass");
        user.setFirstName("Max");
        user.setLastName("Mustermann");
        user.setEmail("max@mustermann.at");
        user.getRoles().add(adminRole);
        user.getRoles().add(managerRole);
        user.setStatus(IdpUserState.CREATED);
        user.setActionToken("uid-123-uid-456");

        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();

        // ---- test -----

        IdpUser fetchedUser = (IdpUser) em.createQuery("SELECT u FROM IdpUser u WHERE u.username = :username")
            .setParameter("username", "testUser").getSingleResult();

        assertNotNull(fetchedUser);
        assertEquals(user.getUsername(), fetchedUser.getUsername());
        assertEquals(user.getPlainPassword(), fetchedUser.getPlainPassword());
        assertEquals(user.getPassword(), fetchedUser.getPassword());
        assertEquals(user.getFirstName(), fetchedUser.getFirstName());
        assertEquals(user.getLastName(), fetchedUser.getLastName());
        assertEquals(user.getFullName(), fetchedUser.getFullName());
        assertEquals(user.getEmail(), fetchedUser.getEmail());
        assertEquals(user.getStatus(), fetchedUser.getStatus());
        assertEquals(user.getActionToken(), fetchedUser.getActionToken());
        assertTrue(user.getRoles().contains(adminRole));
        assertTrue(user.getRoles().contains(managerRole));
    }

    // Update
    @Test
    public void updateAndRetrieveUserWithRelations() throws Exception {
        // ----- set-up -----
        deleteUserIfExistent("testUser");
        deleteRoleIfExistent("admin");
        deleteRoleIfExistent("manager");

        // add test-user and relating objects
        IdpRole adminRole = new IdpRole();
        adminRole.setRoleName("admin");

        IdpRole managerRole = new IdpRole();
        managerRole.setRoleName("manager");

        IdpUser user = new IdpUser();
        user.setUsername("testUser");
        user.setPlainPassword("mypass");
        user.setFirstName("Max");
        user.setLastName("Mustermann");
        user.setEmail("max@mustermann.at");
        user.getRoles().add(adminRole);
        user.getRoles().add(managerRole);
        user.setStatus(IdpUserState.CREATED);
        user.setActionToken("uid-123-uid-456");

        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();

        // ---- test -----

        // update user
        em.getTransaction().begin();
        user.getRoles().remove(adminRole);
        user.setFirstName("Markus");
        user.setPlainPassword("newPassword");
        em.persist(user);
        em.getTransaction().commit();
        
        // see if update was successful
        IdpUser fetchedUser = (IdpUser) em.createQuery("SELECT u FROM IdpUser u WHERE u.username = :username")
            .setParameter("username", "testUser").getSingleResult();

        assertNotNull(fetchedUser);
        assertEquals(user.getUsername(), fetchedUser.getUsername());
        assertEquals(user.getPlainPassword(), fetchedUser.getPlainPassword());
        assertEquals(user.getPassword(), fetchedUser.getPassword());
        assertEquals(user.getFirstName(), fetchedUser.getFirstName());
        assertEquals(user.getLastName(), fetchedUser.getLastName());
        assertEquals(user.getFullName(), fetchedUser.getFullName());
        assertEquals(user.getEmail(), fetchedUser.getEmail());
        assertEquals(user.getStatus(), fetchedUser.getStatus());
        assertEquals(user.getActionToken(), fetchedUser.getActionToken());
        assertTrue(user.getRoles().contains(managerRole));
        assertFalse(user.getRoles().contains(adminRole));
    }

    // Delete
    @Test
    public void deleteUserWithAssignedRoles_shouldNotDeleteRolesAsWell() throws Exception {
        // ----- set-up -----
        deleteUserIfExistent("testUser");
        deleteRoleIfExistent("testAdminRole");
        deleteRoleIfExistent("testManagerRole");

        // add test-user and relating objects
        IdpRole testAdminRole = new IdpRole();
        testAdminRole.setRoleName("testAdminRole");

        IdpRole testManagerRole = new IdpRole();
        testManagerRole.setRoleName("testManagerRole");

        IdpUser user = new IdpUser();
        user.setUsername("testUser");
        user.getRoles().add(testAdminRole);
        user.getRoles().add(testManagerRole);

        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();

        // ---- test -----

        deleteUserIfExistent("testUser");

        IdpRole fetchedAdminRole = em.find(IdpRole.class, testAdminRole.getId());
        IdpRole fetchedManagerRole = em.find(IdpRole.class, testManagerRole.getId());

        assertNotNull(fetchedAdminRole);
        assertNotNull(fetchedManagerRole);
    }
    
    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
      // ----- set-up -----
      deleteUserIfExistent("testUser");
      deleteRoleIfExistent("admin");
      deleteRoleIfExistent("manager");

      // add test-user and relating objects
      IdpRole adminRole = new IdpRole();
      adminRole.setRoleName("admin");

      IdpUser user = new IdpUser();
      user.setUsername("testUser");
      user.setPlainPassword("mypass");
      user.setFirstName("Max");
      user.setLastName("Mustermann");
      user.setEmail("max@mustermann.at");
      user.getRoles().add(adminRole);
      user.setStatus(IdpUserState.CREATED);
      user.setActionToken("uid-123-uid-456");
      
      IdpUser user2 = new IdpUser();
      user.setUsername("testUser2");
      user.setPlainPassword("mypass2");
      user.setFirstName("John");
      user.setLastName("Doe");
      user.setEmail("john@doe.at");
      user.getRoles().add(adminRole);
      user.setStatus(IdpUserState.CREATED);
      user.setActionToken("uid-123-uid-456");
      
      em.getTransaction().begin();
      em.persist(user);
      em.persist(user2);
      em.getTransaction().commit();
      
      IdpUser retrievedUser1 = em.find(IdpUser.class, user.getId());
      IdpUser retrievedUser2 = em.find(IdpUser.class, user2.getId());
      IdpUser retrievedUser2again = em.find(IdpUser.class, user2.getId());
      Assert.assertNotNull(retrievedUser1);
      Assert.assertNotNull(retrievedUser2);
      Assert.assertNotNull(retrievedUser2again);
      Assert.assertEquals(retrievedUser2, retrievedUser2again);
      Assert.assertEquals(user.getEmail(), retrievedUser1.getEmail());
      Assert.assertEquals(user2.getEmail(), retrievedUser2.getEmail());

      Set<IdpUser> users = new HashSet<IdpUser>();
      users.add(retrievedUser1);
      users.add(retrievedUser2);
      users.add(retrievedUser2again);
      
      // set should contain 2 users.
      Assert.assertEquals(2, users.size());
      
      //now close the entity manager, to simulate a crash
      em.close();
      em = emFactory.createEntityManager();
     
      IdpUser newUser = em.find(IdpUser.class, retrievedUser2.getId());

      //adding 2 users (3 user objects) to set
      users = new HashSet<IdpUser>();
      users.add(retrievedUser1);
      users.add(retrievedUser2);
      users.add(newUser);

      // set should contain 2 users.
      Assert.assertEquals(2, users.size());
      
    }

    private void deleteUserIfExistent(String username) {
        // delete probably conflicting test-user
        em.getTransaction().begin();

        List<IdpUser> userToDelete = (List<IdpUser>) em
            .createQuery("SELECT u FROM IdpUser u WHERE u.username = :username").setParameter("username", username)
            .getResultList();

        for (IdpUser delUser : userToDelete) {
            em.remove(delUser);
        }

        em.getTransaction().commit();
    }

    private void deleteRoleIfExistent(String roleName) {
        // delete probably conflicting test-user
        em.getTransaction().begin();

        List<IdpRole> rolesToDelete = (List<IdpRole>) em
            .createQuery("SELECT r FROM IdpRole r WHERE r.roleName = :roleName").setParameter("roleName", roleName)
            .getResultList();

        for (IdpRole delRole : rolesToDelete) {
            em.remove(delRole);
        }

        em.getTransaction().commit();
    }
}
