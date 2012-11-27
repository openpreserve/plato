package eu.scape_project.planning.plato.wf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.Testable;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.exporter.zip.ZipExporterImpl;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.weld.context.bound.Bound;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.MutableBoundRequest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.application.MockAuthenticatedUserProvider;
import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.manager.PlanManager;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.validation.ValidationError;

@Ignore
@RunWith(Arquillian.class)
public class FullPlanningworkflowIT {

    private static final Logger log = LoggerFactory.getLogger(FullPlanningworkflowIT.class);

    @Inject
    @Bound
    private BoundConversationContext conversationContext;
    
    @Inject private Instance<DefineBasis> defineBasisSource;
    
    @Inject private User user;
    
    @Inject private PlanManager planManager;

    public static WebArchive createSlimDeployment() throws ArchiveExportException, IllegalArgumentException, FileNotFoundException {
        PomEquippedResolveStage pomResolver = Maven.resolver().loadPomFromFile("../pom.xml", "pom.xml");
        
        File[] platoModelJars = pomResolver.resolve("eu.scape-project.pw:plato-model").withoutTransitivity().asFile();
        File[] libs = pomResolver
            .resolve("eu.scape-project.pw:planning-core").withTransitivity().asFile();
        
        JavaArchive platoModel = ShrinkWrap.createFromZipFile(JavaArchive.class, platoModelJars[0]);
        platoModel.addAsResource("test-persistence.xml", "META-INF/persistence.xml");
        
        
        
        WebArchive archive = ShrinkWrap.create(WebArchive.class)
            .addAsLibraries(libs)
            .addAsResource("slimtest-persistence.xml","persistence.xml")
            .addPackage("eu.scape_project.planning.plato")
            .addPackage("eu.scape_project.planning.policies")
            .addPackage("eu.scape_project.planning.user")
            .addPackage("eu.scape_project.planning.utils")
            .addClass(FullPlanningworkflowIT.class);
        
        ZipExporterImpl exporter = new ZipExporterImpl(archive);
        exporter.exportTo(new FileOutputStream("../test-archive.war"));
        return archive;
    }
    
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
    public void test() throws PlanningException {
            log.info("entering test");

            conversationContext.associate(new MutableBoundRequest(
                            new HashMap<String, Object>(), new HashMap<String, Object>()));
            conversationContext.activate();
            
            Assert.assertTrue(conversationContext.isActive());
            
            DefineBasis defineBasis = defineBasisSource.get();
            Assert.assertNotNull(defineBasis);

            Plan plan = new Plan();
            plan.getPlanProperties().setName("Test Plan");
            plan.getPlanProperties().setAuthor(user.getUsername());
            plan.getPlanProperties().setOwner(user.getUsername());
//            planManager.store(plan);
            defineBasis.setPlan(plan);
            Assert.assertNotNull(defineBasis.getPlan());
            
            defineBasis.save();
            log.info("stored plan");
            List<ValidationError> errors = new ArrayList<ValidationError>();
            Assert.assertFalse(defineBasis.proceed(errors));
            Assert.assertTrue(errors.size() != 0);
            for (ValidationError ve: errors) {
                log.debug(ve.getMessage());
            }
            
    }
    
}
