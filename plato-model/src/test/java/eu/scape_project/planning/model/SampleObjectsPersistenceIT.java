package eu.scape_project.planning.model;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class SampleObjectsPersistenceIT extends PersistenceTest{


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
        @SuppressWarnings("unchecked")
        List<SampleObject> samples = (List<SampleObject>)em.createQuery("select s from SampleObject s").getResultList();
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
        samples = (List<SampleObject>)em.createQuery("select s from SampleObject s").getResultList();
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

}
