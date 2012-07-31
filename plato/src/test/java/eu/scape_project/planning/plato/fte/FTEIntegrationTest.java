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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.context.bound.Bound;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.MutableBoundRequest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.plato.wfview.fte.FTCreatePlanView;
import eu.scape_project.planning.plato.wfview.fte.FTDefineRequirementsView;

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
		// EnterpriseArchive planningSuite =
		// ShrinkWrap.create(EnterpriseArchive.class,"plannningsuite.ear").
		// addAsLibraries(lib).addAsModule(planningCore).addAsModule(plato).
		// addAsManifestResource("test-persistance.xml", "persistance.xml");
		//
		System.out.println("preparing");
		// BeansDescriptorImpl bd =
		// Descriptors.create(BeansDescriptorImpl.class);
		// String beansDescriptor =
		// bd.alternativeClass(MockConversation.class).toString();
		// System.out.println(beansDescriptor);
		// String beansDescriptor = bd.createAlternatives()
		// .clazz(MockConversation.class.getCanonicalName())
		// .up()
		// .exportAsString();
		platoArch.delete(platoArch.get("WEB-INF/web.xml").getPath());
		platoArch.delete(platoArch.get("WEB-INF/beans.xml").getPath());
		platoArch.addAsWebInfResource("test-beans.xml", "beans.xml")
				.addPackage("eu.scape_project.planning.plato.fte");
		System.out.println(platoArch.toString(true));
		Node node = archive.get("plato-0.0.1-SNAPSHOT.war");
		archive.delete(node.getPath());
		archive.addAsModule(Testable.archiveToTest(platoArch));

		// System.out.println(planningSuite.toString(true));
		// return planningSuite;

		// archive.as(ZipExporter.class).exportTo(new
		// File("/home/kresimir/arhiva.ear"));
		System.out.println(archive.toString(true));
		return archive;
	}

	// @Inject
	// private Conversation conversation;

	@Inject
	@Bound
	BoundConversationContext conversationContext;

	// @Inject
	// BeanManager bm;

	@Inject
	FTCreatePlanView fcv;
	
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
	
	@Test
	public void test() {

		// ConversationContext cc =
		// Container.instance().services().get(ContextLifecycle.class).getConversationContext();
		// ConversationContext cc = new MockConversationContext();
		// BeanManagerImpl bml = (BeanManagerImpl)bm;
		// bml.addContext(cc);
		System.out.println("Entering test");

		// Container.instance().services().get(ContextLifecycle.class).getConversationContext();
		// conversationContext.setBeanStore(new HashMapBeanStore());
		// conversationContext.setActive(true);

		if (fcv == null)
			System.out.println("NULL FCV");
		else
			System.out.println("NOT NULL FCV" + fcv);
		Assert.assertTrue(fcv != null);
		fcv.createPlan();
		Plan plan = fcv.getPlan();
		Assert.assertTrue(plan != null);
		Assert.assertTrue(plan.getPlanProperties().getAuthor().equals("Testing Plato"));
		Assert.assertTrue(true);
	}

	/**
	 * first integration test for FTE
	 * @author cb
	 */
	@Test
	public void testFTE1() {

		System.out.println("Entering test FTE");

		Assert.assertTrue(fcv != null);
		fcv.createPlan();
		Plan plan = fcv.getPlan();
		Assert.assertTrue(plan != null);
		
		fteDefine.init(plan);

		// is there at least one ft template?
		Assert.assertTrue(fteDefine.getFtTemplates().size()>0);
		
		// TODO this currently fails

		try	{
			//name is not set, this should fail:
			fteDefine.proceed();
			Assert.assertTrue(false);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		// need to set plan name here - otherwise *required* name is missing
		plan.getPlanProperties().setName("fte test name");
		
		// need to select a fast track template
		fteDefine.setSelectedFTTemplate(fteDefine.getFtTemplates().get(0));

		// need to add one sample file TODO
		SampleObject digitalObject = new SampleObject();
        digitalObject.setFullname("test.jpg");
//        TODO digitalObject.getData().setData();
        digitalObject.setContentType("image/jpg");
        fteDefine.getSamples().add(digitalObject);
        
		try	{
			fteDefine.proceed(); // <<< this should work
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false); // if there is an exception here, the test failed 
		}
		
		// voila!
		Assert.assertTrue(true);
	}
}
