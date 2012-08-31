package eu.scape_project.planning.plato.fte;

import java.io.File;
import java.util.HashMap;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.Testable;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.context.bound.Bound;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.MutableBoundRequest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.plato.wfview.fte.FTCreatePlanView;
import eu.scape_project.planning.plato.wfview.fte.FTDefineRequirementsView;
import eu.scape_project.planning.utils.FacesMessages;

@RunWith(Arquillian.class)
public class FTEIntegrationTest {

	// @Deployment
	// public static WebArchive createDeployment() {
	// MavenDependencyResolver resolver = DependencyResolvers.use(
	// MavenDependencyResolver.class);
	//
	// JavaArchive platoModel = ShrinkWrap.create(JavaArchive.class,
	// "plato-model.jar");
	// platoModel.addPackages(true, "eu.scape_project.planning.model")
	// .addPackage("eu.scape_project.planning.exception")
	// .addAsManifestResource(EmptyAsset.INSTANCE, "MANIFEST.MF");
	//
	// //System.out.println(platoModel.toString(true));
	// WebArchive wa = ShrinkWrap
	// .create(WebArchive.class)
	// .addAsWebInfResource(EmptyAsset.INSTANCE,
	// "beans.xml").addClass(FTCreatePlanView.class);
	// //System.out.println(wa.toString(true));
	//
	// EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class,
	// "test.ear");
	// ear.addAsModule(wa);
	// ear.addAsLibraries(platoModel);
	// ear.addAsLibraries(
	// DependencyResolvers.
	// use(MavenDependencyResolver.class).
	// loadEffectivePom("pom.xml").resolveAsFiles());
	//
	// System.out.println(ear.toString(true));
	// return wa;
	// }

	@Deployment
	public static EnterpriseArchive createDeployment1() {

		File planningCore = new File(
				"../planningsuite-ear/target/planningsuite-ear/planning-core-0.0.1-SNAPSHOT.jar");
		File plato = new File(
				"../planningsuite-ear/target/planningsuite-ear/plato-0.0.1-SNAPSHOT.war");

		File lib = new File("../planningsuite-ear/target/planningsuite-ear/lib");
		File metaInf = new File(
				"../planningsuite-ear/target/planningsuite-ear/META-INF");
		File archiveFile = new File(
				"../planningsuite-ear/target/planningsuite-ear.ear");
		EnterpriseArchive archive = ShrinkWrap.createFromZipFile(
				EnterpriseArchive.class, archiveFile);
		WebArchive platoArch = ShrinkWrap.createFromZipFile(WebArchive.class,
				plato);
		JavaArchive planningCoreArch = ShrinkWrap.createFromZipFile(JavaArchive.class,
				planningCore);
				
		
		System.out.println("preparing");
	
		//planningCoreArch.delete(planningCoreArch.get("/eu/scape_project/planning/application/SessionScopeProducer.class").getPath());
		planningCoreArch.delete(planningCoreArch.get("META-INF/beans.xml").getPath());
		planningCoreArch.addClass(MockSessionScopeProducer.class);
		planningCoreArch.addAsManifestResource("test-beans-planningcore.xml", "beans.xml");
		System.out.println(planningCoreArch.toString(true));
		
		
		platoArch.delete(platoArch.get("WEB-INF/web.xml").getPath());
		platoArch.delete(platoArch.get("WEB-INF/beans.xml").getPath());
		platoArch.addAsWebInfResource("test-beans.xml", "beans.xml")
				.addPackage("eu.scape_project.planning.plato.fte");
		System.out.println(platoArch.toString(true));
		Node node = archive.get("plato-0.0.1-SNAPSHOT.war");
		archive.delete(node.getPath());
		node = archive.get("planning-core-0.0.1-SNAPSHOT.jar");
		archive.delete(node.getPath());
		node = archive.get("META-INF/persistence.xml");
		archive.delete(node.getPath());
		archive.addAsResource("test-persistence.xml", "persistence.xml");
		
		archive.addAsModule(planningCoreArch);
		archive.addAsModule(Testable.archiveToTest(platoArch));

		
		System.out.println(archive.toString(true));
		return archive;
	}

	@Inject
	@Bound
	BoundConversationContext conversationContext;


	@Inject
	FTDefineRequirementsView fteDefine;

	@Before
	public void initTests() {
		conversationContext.associate(new MutableBoundRequest(
				new HashMap<String, Object>(), new HashMap<String, Object>()));
		conversationContext.activate();
	}

	@After
	public void tearDown() {
		conversationContext.deactivate();
	}

	
	/**********************************************************
	 * Test for the plan creation							  *
	 *  - checks weather plan has a correct author 
	 *    (author is produced in MockSessionScopeProducer
	 *  - tries to save a plan before defining all properties
	 *  - specifies all the properties saves the plan and check weather it 
	 *    really is in a database    
	 */

	@Inject
	FTCreatePlanView fcv;
	
	@Inject
	FacesMessages facesMessages;
	
	
	@Test
	public void testCreatePlan() {
		
		

		Assert.assertNotNull(fcv);
		
		fcv.createPlan();
		Plan plan = fcv.getPlan();
		
		Assert.assertNotNull(plan);
		
		Assert.assertEquals("Test User", plan.getPlanProperties()
				.getAuthor());
		
		
		fcv.savePlan();
		
	}

	
	//
	// /**
	// * first integration test for FTE
	// * @author cb
	// */
	// @Test
	// public void testFTE1() {
	//
	// System.out.println("Entering test FTE");
	//
	// Assert.assertNotNull(fcv);
	// fcv.createPlan();
	// Plan plan = fcv.getPlan();
	// Assert.assertNotNull(plan);
	//
	// fteDefine.init(plan);
	//
	// // is there at least one ft template?
	// Assert.assertTrue(fteDefine.getFtTemplates().size()>0);
	//
	// // TODO this currently fails
	//
	// try {
	// //name is not set, this should fail:
	// fteDefine.proceed();
	// Assert.fail("the plan is missing information like name, proceed should not be possible");
	// } catch (Exception e) {
	// }
	// // need to set plan name here - otherwise *required* name is missing
	// plan.getPlanProperties().setName("fte test name");
	//
	// // need to select a fast track template
	// fteDefine.setSelectedFTTemplate(fteDefine.getFtTemplates().get(0));
	//
	// // FIXME: Samples should be defined with view/bean of step define
	// samples, or at least via
	// plan.getSampleRecordsDefinition().addRecord(record)
	//
	// // TODO need to add one sample file
	// SampleObject digitalObject = new SampleObject();
	// digitalObject.setFullname("test.jpg");
	// // TODO digitalObject.getData().setData();
	// digitalObject.setContentType("image/jpg");
	// fteDefine.getSamples().add(digitalObject);
	//
	// try {
	// Assert.assertEquals("success", fteDefine.proceed()); // <<< this should
	// work
	// } catch (Exception e) {
	// e.printStackTrace();
	// Assert.fail("proceed should be possible now"); // if there is an
	// exception here, the test failed
	// }
	//
	// // voila!
	// Assert.assertTrue(true);
	// }

}
