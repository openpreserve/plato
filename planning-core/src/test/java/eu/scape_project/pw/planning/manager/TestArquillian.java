package eu.scape_project.pw.planning.manager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class TestArquillian {

	
	@Deployment
	public static WebArchive createDeployment() {
		System.out.println("hello");
		
		return null;
		
	    }


	@Test
    public void testStoreLoad() {
		
    }
	
	
	
	
}