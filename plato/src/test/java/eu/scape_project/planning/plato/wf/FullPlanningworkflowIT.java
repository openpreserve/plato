package eu.scape_project.planning.plato.wf;

import java.io.File;
import java.io.FileOutputStream;
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
import org.jboss.shrinkwrap.impl.base.exporter.zip.ZipExporterImpl;
import org.jboss.weld.context.bound.Bound;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.MutableBoundRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.application.Mock;
import eu.scape_project.planning.application.MockAuthenticatedUserProvider;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.plato.wfview.full.CreatePlanView;

@RunWith(Arquillian.class)
public class FullPlanningworkflowIT {

    private static final Logger log = LoggerFactory.getLogger(FullPlanningworkflowIT.class);

    @Inject
    @Bound
    private BoundConversationContext conversationContext;
    
    @Inject
    private CreatePlanView createPlanView;

    @Deployment
    public static EnterpriseArchive createDeployment() {

        EnterpriseArchive planningsuiteEar = ShrinkWrap.createFromZipFile(EnterpriseArchive.class, new File(
            "../planningsuite-ear/target/planningsuite-ear.ear"));
        WebArchive platoArch = ShrinkWrap.createFromZipFile(WebArchive.class, new File(
            "../planningsuite-ear/target/planningsuite-ear/plato-0.0.1-SNAPSHOT.war"));

        JavaArchive planningcoreArch = ShrinkWrap.createFromZipFile(JavaArchive.class, new File(
            "../planningsuite-ear/target/planningsuite-ear/planning-core-0.0.1-SNAPSHOT.jar"));
        
        log.info("preparing archive");
        // at the moment conversations are not supported by arquillian, so we have to provide a mock
        // we also do not need user management for the tests, so we mock the session scope producer
        // we have to provide mocks for handling conversations and provide the user
        final Class<MockAuthenticatedUserProvider> mockAuthenticatedUserProvider = MockAuthenticatedUserProvider.class;

        planningcoreArch.addClasses(mockAuthenticatedUserProvider);
        planningcoreArch.delete("META-INF/beans.xml");
        planningcoreArch.addAsResource("META-INF/test-beans.xml", "META-INF/beans.xml");
        
        Node planningcoreNode = planningsuiteEar.get("planning-core-0.0.1-SNAPSHOT.jar");
        planningsuiteEar.delete(planningcoreNode.getPath());
        planningsuiteEar.addAsModule(planningcoreArch);        
        
        platoArch.delete(platoArch.get("WEB-INF/web.xml").getPath());
        platoArch.delete(platoArch.get("WEB-INF/beans.xml").getPath());
        
        platoArch.addAsWebInfResource("test-beans.xml", "beans.xml")
            .addPackage("eu.scape_project.planning.plato.mock")
            .addPackage("eu.scape_project.planning.plato.wf");

        // now we can replace the original plato-war with the configured one
        Node node = planningsuiteEar.get("plato-0.0.1-SNAPSHOT.war");
        planningsuiteEar.delete(node.getPath());
        planningsuiteEar.addAsModule(Testable.archiveToTest(platoArch));
        
        return planningsuiteEar;
    }
    
    @Test
    public void test() {

            conversationContext.associate(new MutableBoundRequest(
                            new HashMap<String, Object>(), new HashMap<String, Object>()));
            conversationContext.activate();

            Assert.assertNotNull("Viewbean not injected", createPlanView);
            
            Assert.assertNull(createPlanView.getPlan());
            createPlanView.createPlan();
            Plan plan = createPlanView.getPlan();
            Assert.assertNotNull(plan);
            plan.getPlanProperties().setName("Test Plan");
            createPlanView.savePlan();
    }
    
}
