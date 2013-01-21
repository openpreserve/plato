package eu.scape_project.planning.model.tree;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.PersistenceTest;
import eu.scape_project.planning.model.Values;
import eu.scape_project.planning.model.measurement.Attribute;
import eu.scape_project.planning.model.measurement.CriterionCategory;
import eu.scape_project.planning.model.measurement.EvaluationScope;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.scales.FloatScale;
import eu.scape_project.planning.model.scales.PositiveIntegerScale;
import eu.scape_project.planning.model.values.PositiveIntegerValue;
import eu.scape_project.planning.model.values.Value;

public class LeafPersistenceIT extends PersistenceTest{
    
    @Test
    public void testRemovingScale(){
        Leaf l = new Leaf();
        l.setScale(new FloatScale());
        l.setName("a float scale");

        em.getTransaction().begin();
        em.persist(l);
        em.getTransaction().commit();
        em.refresh(l);
        
        Assert.assertEquals(1,  ((Long)em.createQuery("select count(*) from Scale").getSingleResult()).longValue());

        Measure meas = new Measure();
        Attribute attr = new Attribute();
        CriterionCategory cat = new CriterionCategory("test://category/1", "testcategory", EvaluationScope.ALTERNATIVE_ACTION);
        attr.setCategory(cat);
        attr.setName("test attr");
        attr.setUri("test://attribute/1");
        meas.setAttribute(attr);
        meas.setName("test measure");
        meas.setUri("test://measure/1");
        
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
        Assert.assertEquals("Measure should be persisted together with leaf", 2, 
            ((Long)em.createQuery("select count(*) from Measure").getSingleResult()).longValue());
    }


}
