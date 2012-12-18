package eu.scape_project.planning.model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ElementOrderPersistenceTest {
    
    
    private EntityManager em;

    @Before
    public void setUp() {
        EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("testing-platoDatabase");
        em = emFactory.createEntityManager();
    }
    
    
    @Test
    public void storeSampeRecordsDefinition(){
        SampleRecordsDefinition samplesDef = new SampleRecordsDefinition();
        SampleObject s1 = new SampleObject("s1");
        SampleObject s2 = new SampleObject("s2");
        SampleObject s3 = new SampleObject("s3");
        
        samplesDef.addRecord(s1);
        samplesDef.addRecord(s2);
        samplesDef.addRecord(s3);
        
        em.persist(samplesDef);
        
        List<SampleObject> samples = em.createQuery("select from SampleObject").getResultList();
        Assert.assertNotNull(samples);
        Assert.assertEquals(3, samples.size());

        Assert.assertSame(s1.getShortName(), samples.get(0).getShortName());
        Assert.assertSame(s2.getShortName(), samples.get(1).getShortName());
        Assert.assertSame(s3.getShortName(), samples.get(2).getShortName());
    }
    
    

}
