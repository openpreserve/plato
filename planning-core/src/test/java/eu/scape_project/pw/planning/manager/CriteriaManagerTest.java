package eu.scape_project.pw.planning.manager;

import java.util.Collection;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.scape_project.planning.manager.CriteriaManager;
import eu.scape_project.planning.model.measurement.Attribute;
import eu.scape_project.planning.model.measurement.CriterionCategory;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.scales.OrdinalScale;
import eu.scape_project.planning.model.scales.PositiveFloatScale;
import eu.scape_project.planning.model.scales.PositiveIntegerScale;
import eu.scape_project.planning.model.scales.RestrictedScale;
import eu.scape_project.planning.model.scales.Scale;

//@RunWith(Arquillian.class)
public class CriteriaManagerTest {

//	@Deployment
//	public static WebArchive createDeployment() {
//		WebArchive wa = ShrinkWrap 
//				.create(WebArchive.class)
//				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
//				.addClasses(QueryFactory.class, ITouchable.class, Model.class, Query.class, PositiveIntegerScale.class, RestrictedScale.class, BooleanScale.class, FreeStringScale.class,
//						FloatScale.class, PositiveFloatScale.class, OrdinalScale.class, LoggerFactory.class, Scale.class, CriteriaManager.class);
//
//		System.out.println(wa.toString(true));
//		return wa;
//	}
	
        private static CriteriaManager criteriaManager;
    
        @BeforeClass
        public static void setUp(){
            criteriaManager = new CriteriaManager();
            criteriaManager.init();
        }
        
	
	@Test
	public void testRetriveSingleMeasureBasic() {
		Measure m = criteriaManager.getMeasure("http://scape-project.eu/pw/vocab/measures/31");
		Assert.assertNotNull(m);
	}

	@Test
        public void testMeasureWithPositiveNumberScale() {
            Measure m = criteriaManager.getMeasure("http://scape-project.eu/pw/vocab/measures/3");
            Assert.assertNotNull(m);

            Scale s = m.getScale();
            Assert.assertNotNull("Measure '" + m.getName() + "' has no scale!", s);
            
            Assert.assertTrue(s instanceof PositiveFloatScale);
        }

        @Test
        public void testMeasureWithPositiveIntegerScale() {
            Measure m = criteriaManager.getMeasure("http://scape-project.eu/pw/vocab/measures/96");
            Assert.assertNotNull(m);

            Scale s = m.getScale();
            Assert.assertNotNull("Measure '" + m.getName() + "' has no scale!", s);
            
            Assert.assertTrue(s instanceof PositiveIntegerScale);
        }
	
        @Test
        public void testMeasureWithOrdinalScale() {
            Measure m = criteriaManager.getMeasure("http://scape-project.eu/pw/vocab/measures/38");
            Assert.assertNotNull(m);

            Scale s = m.getScale();
            Assert.assertNotNull("Measure '" + m.getName() + "' has no scale!", s);
            
            Assert.assertTrue(s instanceof OrdinalScale);
            Assert.assertTrue("Measure '" + m.getName() + "' scale " + s.getDisplayName() + " has no restriction defined.", StringUtils.isNotEmpty(((OrdinalScale)s).getRestriction()));
        }
        
	@Test
	public void testRetrievedMeasureIsComplete() {
	    Measure m = criteriaManager.getMeasure("http://scape-project.eu/pw/vocab/measures/30");
	    Assert.assertNotNull(m);
            Assert.assertTrue(StringUtils.isNotEmpty(m.getUri()));
            Assert.assertTrue(StringUtils.isNotEmpty(m.getName()));
	    
	    Attribute a = m.getAttribute();
	    Assert.assertNotNull(a);
            Assert.assertTrue(StringUtils.isNotEmpty(a.getUri()));
	    Assert.assertTrue(StringUtils.isNotEmpty(a.getName()));

	    CriterionCategory category = a.getCategory();
	    Assert.assertNotNull("Measure '" + m.getName() + "' has no category!", category);
	    Assert.assertTrue("Measure '" + m.getName() + "' category has no uri!", StringUtils.isNotEmpty(category.getUri()));
	    Assert.assertNotNull("Measure '" + m.getName() + "' category " + category.getUri() + " has no scope", category.getScope());
	    
            Scale s = m.getScale();
            Assert.assertNotNull("Measure '" + m.getName() + "' has no scale!", s);
            Assert.assertNotNull("Measure '" + m.getName() + "' scale " + s.getDisplayName() + " has no type", s.getType());
	}

	@Test
	public void testDataCategoriesPresent() {
	    Assert.assertFalse(criteriaManager.getAllCriterionCategories().isEmpty());
	}
	

	/**
	 * // FIXME: fix data and reactivate 
	 * - Measure 'TCO of action' restricted scale Positive Number with undefined restriction.
	 * - Measure 'licencing schema' has no scale
	 * - ... ?
	 */
	//@Test   
	public void testDataAllMeasuresComplete(){
	    Collection<Measure> measures = criteriaManager.getAllMeasures();
            
	    for (Measure m : measures) {
                Assert.assertNotNull(m);
                Assert.assertTrue(StringUtils.isNotEmpty(m.getUri()));
                Assert.assertTrue(StringUtils.isNotEmpty(m.getName()));
                
                Attribute a = m.getAttribute();
                Assert.assertNotNull(a);
                Assert.assertTrue(StringUtils.isNotEmpty(a.getUri()));
                Assert.assertTrue(StringUtils.isNotEmpty(a.getName()));
    
                CriterionCategory category = a.getCategory();
                Assert.assertNotNull("Measure '" + m.getName() + " has no category defined", category);
                
                Scale s = m.getScale();
                Assert.assertNotNull("Measure '" + m.getName() + "' has no scale!", s);
                Assert.assertNotNull("Measure '" + m.getName() + "' scale " + s.getDisplayName() + " has no type", s.getType());
                
                if (s instanceof RestrictedScale) {
                    Assert.assertTrue("Measure '" + m.getName() + "' restricted scale " + s.getDisplayName() + " with undefined restriction.", StringUtils.isNotEmpty(((RestrictedScale) s).getRestriction()));
                }
	    }
	}
	
	

}
