package eu.scape_project.planning.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.scales.PositiveIntegerScale;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.values.PositiveIntegerValue;
import eu.scape_project.planning.model.values.Value;

public class ElementOrderPersistenceTest {

    private EntityManager em;

    @Before
    public void setUp() {
        EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("testing-platoDatabase");
        em = emFactory.createEntityManager();
    }

    @After
    public void tearDown() {
        em.close();
    }

    @Ignore
    @Test
    public void checkRelationSampleRecordsDefinitionToSampleObjects() {
        SampleRecordsDefinition samplesDef = new SampleRecordsDefinition();
        SampleObject s1 = new SampleObject("s1");
        SampleObject s2 = new SampleObject("s2");
        SampleObject s3 = new SampleObject("s3");

        samplesDef.addRecord(s1);
        samplesDef.addRecord(s2);
        samplesDef.addRecord(s3);

        em.getTransaction().begin();

        em.persist(samplesDef);

        em.getTransaction().commit();
        em.refresh(samplesDef);

        // check overall existing samples
        List samples = em.createQuery("select s from SampleObject s").getResultList();
        Assert.assertNotNull(samples);
        Assert.assertEquals(3, samples.size());

        // reload and check persisted samplerecordsdefinition
        SampleRecordsDefinition sDef2 = em.find(SampleRecordsDefinition.class, samplesDef.getId());
        // check proper order of samples
        Assert.assertEquals(s1.getShortName(), sDef2.getRecords().get(0).getShortName());
        Assert.assertEquals(s2.getShortName(), sDef2.getRecords().get(1).getShortName());
        Assert.assertEquals(s3.getShortName(), sDef2.getRecords().get(2).getShortName());

        // now remove the sample in between
        em.getTransaction().begin();
        sDef2.removeRecord(s2);
        // and add a new one
        SampleObject s4 = new SampleObject("s4");
        sDef2.addRecord(s4);
        em.persist(sDef2);

        // check overall existing samples
        samples = em.createQuery("select s from SampleObject s").getResultList();
        Assert.assertNotNull(samples);
        Assert.assertEquals("orphanRemoval should take care of removing sample s2.", 3, samples.size());

        // reload and check proper order of samples
        sDef2 = em.find(SampleRecordsDefinition.class, samplesDef.getId());
        Assert.assertEquals(s1.getShortName(), sDef2.getRecords().get(0).getShortName());
        Assert.assertEquals(s3.getShortName(), sDef2.getRecords().get(1).getShortName());
        Assert.assertEquals(s4.getShortName(), sDef2.getRecords().get(2).getShortName());

        em.getTransaction().commit();
        // test rollback
        em.getTransaction().begin();
        sDef2.removeRecord(s1);
        sDef2.addRecord(new SampleObject("s5"));
        em.persist(sDef2);
        em.getTransaction().rollback();

        // reload and check proper order of samples
        em.getTransaction().begin();
        sDef2 = em.find(SampleRecordsDefinition.class, samplesDef.getId());
        em.refresh(sDef2);
        Assert.assertEquals(s1.getShortName(), sDef2.getRecords().get(0).getShortName());
        Assert.assertEquals(s3.getShortName(), sDef2.getRecords().get(1).getShortName());
        Assert.assertEquals(s4.getShortName(), sDef2.getRecords().get(2).getShortName());

        // remove and add some records
        sDef2.removeRecord(s1);
        sDef2.addRecord(new SampleObject("a"));
        sDef2.removeRecord(s3);
        sDef2.addRecord(new SampleObject("b"));

        sDef2 = em.merge(sDef2);
        em.getTransaction().commit();

        sDef2 = em.find(SampleRecordsDefinition.class, samplesDef.getId());

        Assert.assertEquals(s4.getShortName(), sDef2.getRecords().get(0).getShortName());
        Assert.assertEquals("a", sDef2.getRecords().get(1).getShortName());
        Assert.assertEquals("b", sDef2.getRecords().get(2).getShortName());

        em.close();
        // check order with new connection, this time don't recreate schema
        em = newConnection();

        SampleRecordsDefinition sDef = (SampleRecordsDefinition) em
            .createQuery("select d from SampleRecordsDefinition d").getResultList().get(0);
        Assert.assertEquals("s4", sDef.getRecords().get(0).getShortName());
        Assert.assertEquals("a", sDef.getRecords().get(1).getShortName());
        Assert.assertEquals("b", sDef.getRecords().get(2).getShortName());

    }

    /**
     * Checks relations of {@link Leaf}
     * 
     * - valueMap and contained values
     * - measure
     */
    @Test
    public void checkLeafRelations() {
        Leaf l = new Leaf();
        l.setName("test");
        l.setSingle(false);
        l.changeScale(new PositiveIntegerScale());
        // to properly initialize the value list, we need alternatives
        List<Alternative> alternatives = new ArrayList<Alternative>();
        alternatives.add(new Alternative("a", "alternative a"));
        alternatives.add(new Alternative("x", "alternative x"));
        alternatives.add(new Alternative("b", "alternative b"));
        l.initValues(alternatives, 4, true);

        // set values from 4 down to 1, so the order can be checked afterwards
        for (Alternative a : alternatives) {
            int i = 4;
            for (Value v : l.getValues(a.getName()).getList()) {
                ((PositiveIntegerValue) v).setValue(i);
                i--;
            }
        }

        em.getTransaction().begin();
        em.persist(l);
        em.getTransaction().commit();
        em.refresh(l);

        // reload the leaf
        Leaf lStored = em.find(Leaf.class, l.getId());
        Assert.assertNotNull(lStored);

        Assert.assertNotNull(lStored.getValues("b"));
        Assert.assertNotNull(lStored.getValues("x"));
        Assert.assertNotNull(lStored.getValues("a"));

        Assert.assertEquals(4, lStored.getValues("a").size());
        Assert.assertEquals(4, lStored.getValues("b").size());
        Assert.assertEquals(4, lStored.getValues("x").size());

        Assert.assertEquals(4, ((PositiveIntegerValue) lStored.getValues("a").getValue(0)).getValue());
        Assert.assertEquals(3, ((PositiveIntegerValue) lStored.getValues("a").getValue(1)).getValue());
        Assert.assertEquals(2, ((PositiveIntegerValue) lStored.getValues("a").getValue(2)).getValue());
        Assert.assertEquals(1, ((PositiveIntegerValue) lStored.getValues("a").getValue(3)).getValue());

        Assert.assertEquals(3, ((PositiveIntegerValue) lStored.getValues("b").getValue(1)).getValue());
        Assert.assertEquals(1, ((PositiveIntegerValue) lStored.getValues("x").getValue(3)).getValue());

        em.getTransaction().begin();
        lStored.removeValues(alternatives, 1);
        lStored.removeValues(alternatives, 1);
        em.persist(lStored);
        em.getTransaction().commit();

        lStored = em.find(Leaf.class, l.getId());
        for (Alternative a : alternatives) {
            Values values = lStored.getValues(a.getName());
            Assert.assertEquals(4, ((PositiveIntegerValue) values.getValue(0)).getValue());
            Assert.assertEquals(1, ((PositiveIntegerValue) values.getValue(1)).getValue());
        }
        lStored.initValues(alternatives, 5, true);
        for (Alternative a : alternatives) {
            for (int idx = 2; idx <= 4; idx++) {
                Value v = lStored.getValues(a.getName()).getList().get(idx);
                ((PositiveIntegerValue) v).setValue(idx+1);
            }
        }
        em.getTransaction().begin();
        em.persist(lStored);
        em.getTransaction().commit();

        em.close();
        // check order with new connection, this time don't recreate schema
        em = newConnection();
        Assert.assertNotNull(em);
        em.getTransaction().begin();
        
        lStored = em.find(Leaf.class, l.getId());
        for (Alternative a : alternatives) {
            Values values = lStored.getValues(a.getName());
            Assert.assertEquals(4, ((PositiveIntegerValue) values.getValue(0)).getValue());
            Assert.assertEquals(1, ((PositiveIntegerValue) values.getValue(1)).getValue());
            Assert.assertEquals(3, ((PositiveIntegerValue) values.getValue(2)).getValue());
            Assert.assertEquals(4, ((PositiveIntegerValue) values.getValue(3)).getValue());
            Assert.assertEquals(5, ((PositiveIntegerValue) values.getValue(4)).getValue());
        }  
        
        Assert.assertEquals("Removed values (orphans) should be automatically deleted", 15, 
            ((Long)em.createQuery("select count(*) from Value").getSingleResult()).longValue());
        
        em.remove(lStored);

        Assert.assertEquals("Values should be automatically deleted together with leaf and its valueMap.", 0, 
            ((Long)em.createQuery("select count(*) from Value").getSingleResult()).longValue());

        em.getTransaction().rollback();
        
        em.getTransaction().begin();
        lStored = em.merge(lStored);
        em.refresh(lStored);
        Measure m = new Measure();
        lStored.setMeasure(m);
        em.persist(lStored);
        Assert.assertEquals("Measure should be persisted together with leaf", 1, 
            ((Long)em.createQuery("select count(*) from Measure").getSingleResult()).longValue());
        

        em.getTransaction().commit();
        em.getTransaction().begin();
        
        em.remove(lStored);
        Assert.assertEquals("Measure should be deleted together with leaf", 0, 
            ((Long)em.createQuery("select count(*) from Measure").getSingleResult()).longValue());

        em.getTransaction().rollback();
        em.getTransaction().begin();
        lStored = em.merge(lStored);

        Assert.assertEquals("Measure should be persisted together with leaf", 1, 
            ((Long)em.createQuery("select count(*) from Measure").getSingleResult()).longValue());
        
        Measure m2 = new Measure();
        m2.setName("m2");
        lStored.setMeasure(m2);
        
        em.persist(lStored);
        Assert.assertEquals("Measure should be persisted together with leaf", 1, 
            ((Long)em.createQuery("select count(*) from Measure").getSingleResult()).longValue());
        
        
        
        
    }

    private EntityManager newConnection() {
        // check order with new connection, this time don't recreate schema
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.hbm2ddl.auto", "update");

        EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("testing-platoDatabase", properties);
        return emFactory.createEntityManager();
    }
}
