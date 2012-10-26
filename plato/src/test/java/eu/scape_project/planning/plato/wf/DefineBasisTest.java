package eu.scape_project.planning.plato.wf;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.scape_project.planning.plato.wf.DefineBasis;

//FIXME at least the deployment should work: @RunWith(Arquillian.class)
public class DefineBasisTest {

    //@Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "platotest.jar")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addClass(DefineBasis.class);
    }

    //@Test
    public void test() {

    }
}
