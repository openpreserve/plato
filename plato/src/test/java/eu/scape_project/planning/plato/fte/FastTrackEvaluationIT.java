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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.plato.wfview.fte.FTCreatePlanView;

@RunWith(Arquillian.class)
public class FastTrackEvaluationIT {
    private static final Logger log = LoggerFactory.getLogger(FastTrackEvaluationIT.class);

    @Inject
    @Bound
    private BoundConversationContext conversationContext;

    @Inject
    private FTCreatePlanView fcv;

    @Deployment
    public static EnterpriseArchive createDeployment() {

        EnterpriseArchive planningsuiteEar = ShrinkWrap.createFromZipFile(EnterpriseArchive.class, new File(
            "../planningsuite-ear/target/planningsuite-ear.ear"));
        WebArchive platoArch = ShrinkWrap.createFromZipFile(WebArchive.class, new File(
            "../planningsuite-ear/target/planningsuite-ear/plato-0.0.1-SNAPSHOT.war"));

        log.info("preparing archive");
        // at the moment conversations are not supported by arquillian, so we
        // have to provide a mock
        // we also do not need user management for the tests, so we mock the
        // sessionscope producer
        platoArch.delete(platoArch.get("WEB-INF/web.xml").getPath());
        platoArch.delete(platoArch.get("WEB-INF/beans.xml").getPath());

        // we have to provide mocks for handling conversations and provide the
        // user
        platoArch.addAsWebInfResource("test-beans.xml", "beans.xml").addPackage("eu.scape_project.planning.plato.mock")
            .addPackage("eu.scape_project.planning.plato.fte");

        log.info(platoArch.toString(true));

        // now we can replace the original plato-war with the configured one
        Node node = planningsuiteEar.get("plato-0.0.1-SNAPSHOT.war");
        planningsuiteEar.delete(node.getPath());
        planningsuiteEar.addAsModule(Testable.archiveToTest(platoArch));

        log.info(planningsuiteEar.toString(true));
        return planningsuiteEar;
    }

    @Test
    public void test() {

        log.info("Entering test");

        conversationContext.associate(new MutableBoundRequest(new HashMap<String, Object>(),
            new HashMap<String, Object>()));
        conversationContext.activate();

        Assert.assertNotNull("Viewbean not injected", fcv);
        fcv.createPlan();
        Plan plan = fcv.getPlan();
        Assert.assertNotNull(plan);
        Assert.assertEquals("Testing Plato", plan.getPlanProperties().getAuthor());
    }
}
