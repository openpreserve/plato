package eu.scape_project.pw.planning.plato.wf;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;

import eu.scape_project.planning.plato.wf.DefineAlternatives;

//@RunWith(Arquillian.class)
public class DefineAlternativesActionTest {
	
	@Inject
	private DefineAlternatives action; 
	
	@Deployment
	public static JavaArchive createTestArchive() {
	   return ShrinkWrap.create(JavaArchive.class, "platotest.jar")
	      .addClasses(DefineAlternatives.class)
	      .addAsManifestResource(
	            new ByteArrayAsset("<beans/>".getBytes()), 
	            ArchivePaths.create("beans.xml"));
	}
	
}
