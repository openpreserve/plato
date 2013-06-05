package eu.scape_project.planning.selenium;

import static org.junit.Assert.fail;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.DefaultSelenium;

import eu.scape_project.planning.application.PlatoDeploymentBuilder;

@RunWith(Arquillian.class)
public class WebDriverIT {
    private static final Logger log = LoggerFactory.getLogger(WebDriverIT.class);
    
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
        platoWar
        .addAsResource("webapp", "")
        .addAsResource("data", "data");
        
        log.info("deployment created!");
        log.info(platoWar.toString(true));
        
        return platoWar;    
    }
 
    @Before
    public void setUp() throws Exception {
//        log.info("setting up");
//        
//        log.info("setup complete");
    }

    @Test
    @InSequence(1)
    public void login() throws InterruptedException {
        log.info("login: begin");
        selenium.open("http://localhost:8080/plato/index.jsf");
        selenium.click("//form[@id='mainform']/div[2]/ul/li[3]/a/div/div[2]/img");
        selenium.waitForPageToLoad("30000");
        selenium.type("id=mainform:name:name_input", "Test Plan");
        selenium.type("id=mainform:description:description_input", "TestPlan created with selenium");
        selenium.type("id=mainform:organization:organization_input", "no org");
        selenium.click("id=mainform:createProject");
        selenium.waitForPageToLoad("30000");
        selenium.type("id=mainform:documentTypes:documentTypes_input", "sample images");
        selenium.click("id=mainform:j_idt145:submitButton");
        selenium.waitForPageToLoad("30000");
        selenium.type("id=mainform:samplesDescription:samplesDescription_input", "samples");
        selenium.click("css=input[type=\"button\"]");
        selenium.waitForPageToLoad("30000");
        selenium.type("css=input.rf-fu-inp", "/home/kraxner/workspace/plato_trunk/view/img/arrow-down.gif");
        selenium.click("css=span.rf-fu-btn-cnt-upl");
        for (int second = 0;; second++) {
                if (second >= 60) fail("timeout");
                try { if (selenium.isElementPresent("//input[@value=\"arrow-down.gif\"]")) break; } catch (Exception e) {}
                Thread.sleep(1000);
        }

        selenium.click("id=mainform:j_idt153:submitButton");
        selenium.waitForPageToLoad("30000");
        selenium.click("//div[@id='treeForm:richTree:j__idt101.0:addLeaf']/span[2]");
        for (int second = 0;; second++) {
                if (second >= 60) fail("timeout");
                try { if (selenium.isElementPresent("id=treeForm:richTree:j__idt101.0.j__idt101.0:name")) break; } catch (Exception e) {}
                Thread.sleep(1000);
        }

        selenium.click("id=treeForm:richTree:j__idt101.0.j__idt101.0:showMapping");
        for (int second = 0;; second++) {
                if (second >= 60) fail("timeout");
                try { if (selenium.isElementPresent("//option[@value='Functional correctness: Representation Instance Property']")) break; } catch (Exception e) {}
                Thread.sleep(1000);
        }

        selenium.select("//select[option/@value='Functional correctness: Representation Instance Property']", "label=Functional correctness: Representation Instance Property");
        for (int second = 0;; second++) {
                if (second >= 60) fail("timeout");
                try { if (selenium.isElementPresent("//option[@value='file size']")) break; } catch (Exception e) {}
                Thread.sleep(1000);
        }

        selenium.select("//select[option/@value='file size']", "label=file size");
        for (int second = 0;; second++) {
                if (second >= 60) fail("timeout");
                try { if (selenium.isElementPresent("//a[@id='j_idt195:saveCriterionMapping']")) break; } catch (Exception e) {}
                Thread.sleep(1000);
        }

        selenium.click("css=img[title=\"Map measure to decision criteria\"]");        
    }

}
