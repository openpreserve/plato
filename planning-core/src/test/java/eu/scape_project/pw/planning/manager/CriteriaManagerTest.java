package eu.scape_project.pw.planning.manager;

import junit.framework.Assert;

import org.junit.Test;

import eu.scape_project.planning.manager.CriteriaManager;
import eu.scape_project.planning.model.measurement.Measure;

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
	
//	@Inject
//	CriteriaManager cm;
	
	@Test
	public void testMeasures() {
		
		CriteriaManager cm = new CriteriaManager();
		cm.init();
		
		Measure m = cm.getMeasure("http://scape-project.eu/pw/vocab/measures/31");
		
		Assert.assertTrue(m != null);
	}
	
	

}
