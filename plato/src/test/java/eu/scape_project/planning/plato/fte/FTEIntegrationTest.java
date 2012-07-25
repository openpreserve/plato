package eu.scape_project.planning.plato.fte;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.plato.wfview.fte.FTCreatePlanView;

@RunWith(Arquillian.class)
public class FTEIntegrationTest {

	@Deployment(name = "planningsuite", order = 1)
	public static EnterpriseArchive createDeployment1() {

		File archiveFile = new File(
				"../planningsuite-ear/target/planningsuite-ear.ear");
		EnterpriseArchive archive = ShrinkWrap.createFromZipFile(
				EnterpriseArchive.class, archiveFile);

		System.out.println(archive.toString(true));
		return archive;
	}

//	@Deployment(name = "idp", order = 2, testable = false)
//	public static WebArchive createDeployment2() {
//
//		File archiveFile = new File("../idp/target/idp.war");
//		WebArchive archive = ShrinkWrap.createFromZipFile(WebArchive.class,
//				archiveFile);
//
//		System.out.println(archive.toString(true));
//		return archive;
//	}

	@Inject
	FTCreatePlanView fcv;

	//@OperateOnDeployment("idp")
	@Test
	public void test() {
		System.out.println("Entering test");
		Assert.assertTrue(fcv!=null);
		fcv.createPlan();
		Plan plan = fcv.getPlan();
		Assert.assertTrue(plan!=null);
		Assert.assertTrue(true);
	}
}
