package eu.scape_project.planning.selenium;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.DefaultSelenium;

import eu.scape_project.planning.annotation.SeleniumTest;
import eu.scape_project.planning.application.PlatoDeploymentBuilder;

@Category(SeleniumTest.class)
@RunWith(Arquillian.class)
public class WebDriverIT {
    private static final Logger log = LoggerFactory.getLogger(WebDriverIT.class);

    private static final String WEBAPP_SRC = "src/main/webapp";

    @ArquillianResource
    URL contextPath;

    @Drone
    DefaultSelenium selenium;

    /**
     * Creates a testing WAR of using ShrinkWrap
     * 
     * @return WebArchive to be tested
     */
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        log.info("Creating deployment");
        WebArchive platoWar = PlatoDeploymentBuilder.createPlatoWebArchive();

        File base = new File(WEBAPP_SRC);
        PlatoDeploymentBuilder.addAsWebResources(platoWar, base.toURI(), base,
            new FilenameFilter() {

            @Override
            public boolean accept(File arg0, String filename) {
                return !filename.startsWith(".") && !"jboss-web.xml".equals(filename) && !"web.xml".equals(filename);
            }
        });
        
        
        File [] jbossUtils = DependencyResolvers.use(MavenDependencyResolver.class).artifact("eu.scape-project.pw:jboss-util:0.0.1").resolveAsFiles();
        platoWar.addAsLibraries(jbossUtils);
        platoWar.addAsResource("plato_messages.properties");
        platoWar.addAsResource("log4j.xml");
        
        log.info("deployment created!");
        log.info(platoWar.toString(true));

        return platoWar;
    }


    @Before
    public void setUp() throws Exception {
        // log.info("setting up");
        //
        // log.info("setup complete");
    }

    @Test
//    @RunAsClient
//    @InSequence(1)
    public void login() throws InterruptedException {
        log.info("login: begin");
        assertNotNull(selenium);
        assertNotNull(contextPath);
        selenium.open(contextPath + "index.jsf");

        assertEquals("SCAPE Planning Suite", selenium.getTitle());
//        selenium.click("//form[@id='mainform']/div[2]/ul/li[3]/a/div/div[2]/img");
//        selenium.waitForPageToLoad("30000");
//        selenium.type("id=mainform:name:name_input", "Test Plan");
//        selenium.type("id=mainform:description:description_input", "Selenium Test");
//        selenium.click("id=mainform:createProject");
//        selenium.waitForPageToLoad("30000");
//        selenium.type("id=mainform:documentTypes:documentTypes_input", "images");
//        selenium.click("id=mainform:savediscardproceed:submitButton");
//        selenium.waitForPageToLoad("30000");
//        selenium.type("id=mainform:collectionID:collectionID_input", "a test collection");
//        selenium.type("id=mainform:collectionDescription:collectionDescription_input", "collection of images");
//        selenium.type("id=mainform:collectionTypeOfObjects:collectionTypeOfObjects_input", "mostly jpg images");
//        selenium.click("id=mainform:collectionNumberOfObjects:collectionNumberOfObjects_input");
//        selenium.type("id=mainform:collectionNumberOfObjects:collectionNumberOfObjects_input", "300");
//        selenium.type("id=mainform:collectionExpectedGrowthRate:collectionExpectedGrowthRate_input", "1000 per year");
//        selenium.click("id=mainform:collectionRetentionPeriod:collectionRetentionPeriod_input");
//        selenium.type("id=mainform:samplesDescription:samplesDescription_input", "some test samples");
//        assertEquals("SCAPE Planning Suite - Define Sample Objects", selenium.getTitle());
    }

}
