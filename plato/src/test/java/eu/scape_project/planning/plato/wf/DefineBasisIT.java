package eu.scape_project.planning.plato.wf;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

//FIXME at least the deployment should work: @RunWith(Arquillian.class)
public class DefineBasisIT {

    //@Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "platotest.jar")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addClass(DefineBasis.class);
    }

    //@Test
    public void test() {

    }
}
