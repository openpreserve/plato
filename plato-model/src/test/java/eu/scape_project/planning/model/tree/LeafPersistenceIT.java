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
package eu.scape_project.planning.model.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

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
        
        assertEquals(1,  ((Long)em.createQuery("select count(*) from Scale").getSingleResult()).longValue());

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
        assertNotNull(lStored);

        assertNotNull(lStored.getValues("b"));
        assertNotNull(lStored.getValues("x"));
        assertNotNull(lStored.getValues("a"));

        assertEquals(4, lStored.getValues("a").size());
        assertEquals(4, lStored.getValues("b").size());
        assertEquals(4, lStored.getValues("x").size());

        assertEquals(4, ((PositiveIntegerValue) lStored.getValues("a").getValue(0)).getValue());
        assertEquals(3, ((PositiveIntegerValue) lStored.getValues("a").getValue(1)).getValue());
        assertEquals(2, ((PositiveIntegerValue) lStored.getValues("a").getValue(2)).getValue());
        assertEquals(1, ((PositiveIntegerValue) lStored.getValues("a").getValue(3)).getValue());

        assertEquals(3, ((PositiveIntegerValue) lStored.getValues("b").getValue(1)).getValue());
        assertEquals(1, ((PositiveIntegerValue) lStored.getValues("x").getValue(3)).getValue());

        em.getTransaction().begin();
        lStored.removeValues(alternatives, 1);
        lStored.removeValues(alternatives, 1);
        em.persist(lStored);
        em.getTransaction().commit();

        lStored = em.find(Leaf.class, l.getId());
        for (Alternative a : alternatives) {
            Values values = lStored.getValues(a.getName());
            assertEquals(4, ((PositiveIntegerValue) values.getValue(0)).getValue());
            assertEquals(1, ((PositiveIntegerValue) values.getValue(1)).getValue());
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
        assertNotNull(em);
        em.getTransaction().begin();
        
        lStored = em.find(Leaf.class, l.getId());
        for (Alternative a : alternatives) {
            Values values = lStored.getValues(a.getName());
            assertEquals(4, ((PositiveIntegerValue) values.getValue(0)).getValue());
            assertEquals(1, ((PositiveIntegerValue) values.getValue(1)).getValue());
            assertEquals(3, ((PositiveIntegerValue) values.getValue(2)).getValue());
            assertEquals(4, ((PositiveIntegerValue) values.getValue(3)).getValue());
            assertEquals(5, ((PositiveIntegerValue) values.getValue(4)).getValue());
        }  
        
        assertEquals("Removed values (orphans) should be automatically deleted", 15, 
            ((Long)em.createQuery("select count(*) from Value").getSingleResult()).longValue());
        
        em.remove(lStored);

        assertEquals("Values should be automatically deleted together with leaf and its valueMap.", 0, 
            ((Long)em.createQuery("select count(*) from Value").getSingleResult()).longValue());

        em.getTransaction().rollback();
        
        em.getTransaction().begin();
        lStored = em.merge(lStored);
        em.refresh(lStored);
        Measure m = new Measure();
        lStored.setMeasure(m);
        em.persist(lStored);
        assertEquals("Measure should be persisted together with leaf", 1, 
            ((Long)em.createQuery("select count(*) from Measure").getSingleResult()).longValue());
        

        em.getTransaction().commit();
        em.getTransaction().begin();
        
        em.remove(lStored);
        assertEquals("Measure should be deleted together with leaf", 0, 
            ((Long)em.createQuery("select count(*) from Measure").getSingleResult()).longValue());

        em.getTransaction().rollback();
        em.getTransaction().begin();
        lStored = em.merge(lStored);

        assertEquals("Measure should be persisted together with leaf", 1, 
            ((Long)em.createQuery("select count(*) from Measure").getSingleResult()).longValue());
        
        Measure m2 = new Measure();
        m2.setName("m2");
        lStored.setMeasure(m2);
        
        em.persist(lStored);
        assertEquals("Measure should be persisted together with leaf", 2, 
            ((Long)em.createQuery("select count(*) from Measure").getSingleResult()).longValue());
        em.getTransaction().commit();        
    }


}
