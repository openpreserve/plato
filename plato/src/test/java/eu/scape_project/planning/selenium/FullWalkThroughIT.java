package eu.scape_project.planning.selenium;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.thoughtworks.selenium.DefaultSelenium;

@Ignore
public class FullWalkThroughIT {
        private DefaultSelenium selenium;
    
	@Before
	public void setUp() throws Exception {
		selenium = new DefaultSelenium("localhost", 4444, "*chrome", "http://localhost:8080/idp/");
		selenium.start();
	}

	@Test
	public void testFullWalkThrough() throws Exception {
            selenium.open("http://localhost:8080/plato/index.jsf?GLO=true");
            selenium.waitForPageToLoad("30000");
            selenium.open("/plato/index.jsf");
            selenium.waitForPageToLoad("30000");
            selenium.type("id=j_username", "selenium");
            selenium.type("id=j_password", "selenium");
            selenium.click("name=j_idt17");
            selenium.waitForPageToLoad("300000");
//            selenium.open("/plato/index.jsf");
  //          selenium.waitForPageToLoad("60000");
            for (int second = 0;; second++) {
                if (second >= 60) {
                    Assert.fail("timeout");
                }
                try { if (selenium.isElementPresent("//form[@id='mainform']/div[2]/ul/li[3]/a/div/div[2]/img")) break; } catch (Exception e) {}
                Thread.sleep(1000);
            }
            
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
//            selenium.wait(30000);
            selenium.type("css=input.rf-fu-inp", "/home/kraxner/workspace/plato_trunk/view/img/arrow-down.gif");
            selenium.click("css=span.rf-fu-btn-cnt-upl");
            for (int second = 0;; second++) {
                    if (second >= 60) {
                        Assert.fail("timeout");
                    }
                    try { if (selenium.isElementPresent("//input[@value=\"arrow-down.gif\"]")) break; } catch (Exception e) {}
                    Thread.sleep(1000);
            }

            selenium.click("id=mainform:j_idt153:submitButton");
            selenium.waitForPageToLoad("30000");

	}

	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
