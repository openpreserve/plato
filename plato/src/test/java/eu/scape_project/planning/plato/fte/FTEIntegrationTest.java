package eu.scape_project.planning.plato.fte;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.plato.wfview.fte.FTCreatePlanView;

@RunWith(Arquillian.class)
public class FTEIntegrationTest {


	
	@Deployment
	public static WebArchive createDeployment() {
		MavenDependencyResolver resolver = DependencyResolvers.use(
			    MavenDependencyResolver.class);
		
		JavaArchive platoModel = ShrinkWrap.create(JavaArchive.class, "plato-model.jar");
		platoModel.addPackages(true, "eu.scape_project.planning.model")
					.addPackage("eu.scape_project.planning.exception")
					.addAsManifestResource(EmptyAsset.INSTANCE, "MANIFEST.MF");
		
		//System.out.println(platoModel.toString(true));
		WebArchive wa = ShrinkWrap
				.create(WebArchive.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addClass(FTCreatePlanView.class);
		//System.out.println(wa.toString(true));
		
		EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "test.ear");
		ear.addAsModule(wa);
		ear.addAsLibraries(platoModel);
		ear.addAsLibraries(
                    DependencyResolvers.
                    use(MavenDependencyResolver.class).
                    	loadEffectivePom("pom.xml").resolveAsFiles());
                        
		System.out.println(ear.toString(true));
		return wa;
	}



	@Inject
	FTCreatePlanView fcv;

	@Test
	public void test(FTCreatePlanView fcv) {
		System.out.println("Entering test");
		Assert.assertTrue(fcv!=null);
		fcv.createPlan();
		Plan plan = fcv.getPlan();
		Assert.assertTrue(plan!=null);
		Assert.assertTrue(true);
	}
}
