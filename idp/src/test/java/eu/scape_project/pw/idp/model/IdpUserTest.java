package eu.scape_project.pw.idp.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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
        user.setPassword("mypass");
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
        assertEquals(user.getPassword(), fetchedUser.getPassword());
        assertEquals(user.getFirstName(), fetchedUser.getFirstName());
        assertEquals(user.getLastName(), fetchedUser.getLastName());
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
        user.setPassword("mypass");
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
        user.setPassword("newPassword");
        em.persist(user);
        em.getTransaction().commit();

        // see if update was successful
        IdpUser fetchedUser = (IdpUser) em.createQuery("SELECT u FROM IdpUser u WHERE u.username = :username")
            .setParameter("username", "testUser").getSingleResult();

        assertNotNull(fetchedUser);
        assertEquals(user.getUsername(), fetchedUser.getUsername());
        assertEquals(user.getPassword(), fetchedUser.getPassword());
        assertEquals(user.getFirstName(), fetchedUser.getFirstName());
        assertEquals(user.getLastName(), fetchedUser.getLastName());
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
