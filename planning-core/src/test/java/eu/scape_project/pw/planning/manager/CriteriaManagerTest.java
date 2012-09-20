package eu.scape_project.pw.planning.manager;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.scape_project.planning.manager.CriteriaManager;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.utils.LoggerFactory;

@RunWith(Arquillian.class)
public class CriteriaManagerTest {

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive wa = ShrinkWrap
				.create(WebArchive.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.addClasses(CriteriaManager.class, LoggerFactory.class);

		System.out.println(wa.toString(true));
		return wa;
	}
	
	@Inject
	CriteriaManager cm;
	
	@Test
	public void testMeasures() {
		Measure m = cm.getMeasure("http://scape-project.eu/pw/vocab/measures/31");
		
		Assert.assertTrue(m != null);
	}
	
	

}
