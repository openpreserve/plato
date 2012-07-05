package eu.scape_project.pw.planning.manager;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.scape_project.planning.manager.ByteStreamManager;
import eu.scape_project.planning.manager.FileStorage;
import eu.scape_project.planning.manager.StorageException;
import eu.scape_project.planning.utils.LoggerFactory;

@RunWith(Arquillian.class)
public class ByteStreamManagerTest {

	
	@Deployment
	public static JavaArchive createDeployment() {
		return ShrinkWrap.create(JavaArchive.class).
				addClasses(ByteStreamManager.class, FileStorage.class, LoggerFactory.class).
				addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		
	    }

	@Inject ByteStreamManager bm;

	@Test
    public void test() {
		byte[] array = {1,2,3,4};
        try {
			bm.store(null, array);
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
}
