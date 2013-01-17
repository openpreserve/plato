package eu.scape_project.planning.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;

public abstract class PersistenceTest {
    protected EntityManager em;

    @Before
    public void setUp() {
        EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("testing-platoDatabase");
        em = emFactory.createEntityManager();
    }

    @After
    public void tearDown() {
        em.close();
    }
    
    protected EntityManager newConnection() {
        // check order with new connection, this time don't recreate schema
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.hbm2ddl.auto", "update");

        EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("testing-platoDatabase", properties);
        return emFactory.createEntityManager();
    }

}
