package eu.scape_project.planning.selenium;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.thoughtworks.selenium.Selenium;

public class FullPlanMinimal {
    private Selenium selenium;

    @Before
    public void setUp() throws Exception {
        WebDriver driver = new FirefoxDriver();
        String baseUrl = "http://localhost:8080/";
        selenium = new WebDriverBackedSelenium(driver, baseUrl);
    }

    @Test
    public void testFullMinimal() throws Exception {
        selenium.open("/plato/index.jsf");
        selenium.click("//form[@id='mainform']/div[2]/ul/li[3]/a/div/div[2]/img");
        selenium.waitForPageToLoad("30000");
        String date = selenium.getEval("new Date();");
        selenium.type("id=mainform:name:name_input", "Selenium test - minimal - " + date);
        selenium.type("id=mainform:description:description_input",
            "This plan was created automatically using Selenium test - minimal - " + date);
        selenium.waitForCondition("selenium.browserbot.getUserWindow().$.active == 0", "5000");
        selenium.click("id=mainform:createProject");
        selenium.waitForPageToLoad("30000");
        // Define Basis
        selenium.waitForCondition("selenium.browserbot.getUserWindow().$.active == 0", "5000");
        selenium.type("id=mainform:documentTypes:documentTypes_input", "Document types");
        selenium.waitForCondition("selenium.browserbot.getUserWindow().$.active == 0", "5000");
        selenium.click("id=mainform:savediscardproceed:submitButton");
        selenium.waitForPageToLoad("30000");
        // Define Sample Objects
        selenium.type("id=mainform:samplesDescription:samplesDescription_input", "Sample description");
        selenium.click("id=mainform:addRecord");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (selenium.isElementPresent("id=mainform:allrecords:0:fullName:fullName_input"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.type("id=mainform:allrecords:0:fullName:fullName_input", "Sample1_full");
        selenium.type("id=mainform:allrecords:0:shortName:shortName_input", "Sample1");
        selenium.waitForCondition("selenium.browserbot.getUserWindow().$.active == 0", "5000");
        selenium.click("id=mainform:savediscardproceed:submitButton");
        selenium.waitForPageToLoad("30000");
        // Identify Requirements
        selenium.click("//div[@id='mainform:objectivetree-tree:objectivetree-model.0:addLeaf']/span[2]");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (selenium
                    .isElementPresent("id=mainform:objectivetree-tree:objectivetree-model.0.objectivetree-model.0:name"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium
            .type("id=mainform:objectivetree-tree:objectivetree-model.0.objectivetree-model.0:name", "Image height");
        selenium.click("id=mainform:objectivetree-tree:objectivetree-model.0.objectivetree-model.0:showMapping");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (selenium.isVisible("id=editLeafMappingForm:editLeafMapping"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.select("id=editLeafMappingForm:criterionSelector:categories_select",
            "label=Functional correctness: Transformation Independent Property");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (selenium.getText("id=editLeafMappingForm:criterionSelector:attributes_select").matches(
                    "^[\\s\\S]*image size[\\s\\S]*$"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.select("id=editLeafMappingForm:criterionSelector:attributes_select", "label=image size");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (selenium.getText("id=editLeafMappingForm:criterionSelector:measures_select").matches(
                    "^[\\s\\S]*image height equal[\\s\\S]*$"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.select("id=editLeafMappingForm:criterionSelector:measures_select", "label=image height equal");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (selenium.isElementPresent("id=editLeafMappingForm:criterionSelector:saveCriterionMapping"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.click("id=editLeafMappingForm:criterionSelector:saveCriterionMapping");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (!selenium.isVisible("id=editLeafMappingForm:editLeafMapping"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.click("//div[@id='mainform:objectivetree-tree:objectivetree-model.0:addLeaf']/span[2]");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (selenium
                    .isElementPresent("id=mainform:objectivetree-tree:objectivetree-model.0.objectivetree-model.1:name"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.type("id=mainform:objectivetree-tree:objectivetree-model.0.objectivetree-model.1:name", "Image width");
        selenium.click("id=mainform:objectivetree-tree:objectivetree-model.0.objectivetree-model.1:showMapping");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (selenium.isVisible("id=editLeafMappingForm:editLeafMapping"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.select("id=editLeafMappingForm:criterionSelector:categories_select",
            "label=Functional correctness: Transformation Independent Property");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (selenium.getText("id=editLeafMappingForm:criterionSelector:attributes_select").matches(
                    "^[\\s\\S]*image size[\\s\\S]*$"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.select("id=editLeafMappingForm:criterionSelector:attributes_select", "label=image size");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (selenium.getText("id=editLeafMappingForm:criterionSelector:measures_select").matches(
                    "^[\\s\\S]*image width equal[\\s\\S]*$"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.select("id=editLeafMappingForm:criterionSelector:measures_select", "label=image width equal");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (selenium.isElementPresent("id=editLeafMappingForm:criterionSelector:saveCriterionMapping"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.click("id=editLeafMappingForm:criterionSelector:saveCriterionMapping");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (!selenium.isVisible("id=editLeafMappingForm:editLeafMapping"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.waitForCondition("selenium.browserbot.getUserWindow().$.active == 0", "5000");
        selenium.click("id=mainform:savediscardproceed:submitButton");
        selenium.waitForPageToLoad("30000");
        // Define Alternatives
        selenium.click("id=mainform:addAlternative");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (selenium.isElementPresent("id=mainform:alternativeName:alternativeName_input"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.type("id=mainform:alternativeName:alternativeName_input", "Alternative 1");
        selenium.type("id=mainform:alternativeDescription:alternativeDescription_input", "This is alternative 1");
        selenium.click("id=mainform:saveAlternative");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (selenium.isElementPresent("id=mainform:alternativesTable:0:editAlternativeCmd"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.waitForCondition("selenium.browserbot.getUserWindow().$.active == 0", "5000");
        selenium.click("id=mainform:savediscardproceed:submitButton");
        selenium.waitForPageToLoad("30000");
        // Take Go Decision
        selenium.select("id=mainform:decision", "label=Go");
        selenium.type("id=mainform:decisionReason:decisionReason_input", "Go reason");
        selenium.type("id=mainform:decisionAction:decisionAction_input", "Go action");
        selenium.waitForCondition("selenium.browserbot.getUserWindow().$.active == 0", "5000");
        selenium.click("id=mainform:savediscardproceed:submitButton");
        selenium.waitForPageToLoad("30000");
        // Develop Experiments
        selenium.waitForCondition("selenium.browserbot.getUserWindow().$.active == 0", "5000");
        selenium.click("id=mainform:savediscardproceed:submitButton");
        selenium.waitForPageToLoad("30000");
        // Run Experiments
        selenium.waitForCondition("selenium.browserbot.getUserWindow().$.active == 0", "5000");
        selenium.click("id=mainform:savediscardproceed:submitButton");
        selenium.waitForPageToLoad("30000");
        // Evaluate Experiments
        selenium.click("id=mainform:navigationtree:model.0:select");
        selenium.waitForCondition("selenium.browserbot.getUserWindow().$.active == 0", "5000");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (selenium
                    .isElementPresent("id=mainform:evaluationLeafList:0:alternatives:0:multiOrdinal_ix0:ordinalMenu"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.select("id=mainform:evaluationLeafList:0:alternatives:0:multiOrdinal_ix0:ordinalMenu", "label=Yes");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (selenium
                    .isElementPresent("id=mainform:evaluationLeafList:1:alternatives:0:multiOrdinal_ix0:ordinalMenu"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.select("id=mainform:evaluationLeafList:1:alternatives:0:multiOrdinal_ix0:ordinalMenu", "label=Yes");
        selenium.waitForCondition("selenium.browserbot.getUserWindow().$.active == 0", "5000");
        selenium.click("id=mainform:savediscardproceed:submitButton");
        selenium.waitForPageToLoad("30000");
        // Transform Measured Values
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (selenium.isElementPresent("id=mainform:calculateDefaultTransfomationSettings"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.click("id=mainform:calculateDefaultTransfomationSettings");
        selenium.click("id=mainform:navigationtree:model.0:select");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if ("4".equals(selenium.getCssCount("css=span.requiredIndicator")))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.click("id=mainform:confirmTransformationSettings");
        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if ("0".equals(selenium.getCssCount("css=span.requiredIndicator")))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        selenium.waitForCondition("selenium.browserbot.getUserWindow().$.active == 0", "5000");
        selenium.click("id=mainform:savediscardproceed:submitButton");
        selenium.waitForPageToLoad("30000");
        // Set Impartance Factors
        selenium.waitForCondition("selenium.browserbot.getUserWindow().$.active == 0", "5000");
        selenium.click("id=mainform:savediscardproceed:submitButton");
        selenium.waitForPageToLoad("30000");
        // Analyse Results
        selenium.select("id=mainform:recommendation", "label=Alternative 1");
        selenium.type("id=mainform:reasoning:reasoning_input", "Reasonging alternative 1");
        selenium.type("id=mainform:effects:effects_input", "Effects alternative 1");
        selenium.waitForCondition("selenium.browserbot.getUserWindow().$.active == 0", "5000");
        selenium.click("id=mainform:savediscardproceed:submitButton");
        selenium.waitForPageToLoad("30000");
        // Create Executable Plan
        selenium.waitForCondition("selenium.browserbot.getUserWindow().$.active == 0", "5000");
        selenium.click("id=mainform:savediscardproceed:submitButton");
        selenium.waitForPageToLoad("30000");
        // Define Preservation Plan
        selenium.waitForCondition("selenium.browserbot.getUserWindow().$.active == 0", "5000");
        selenium.click("id=mainform:savediscardproceed:submitButton");
        selenium.waitForPageToLoad("30000");
        // Validate Plan
        selenium.click("id=mainform:approveButton");
        selenium.waitForCondition("selenium.browserbot.getUserWindow().$.active == 0", "5000");
        selenium.click("link=Close Plan");
        selenium.waitForPageToLoad("30000");
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
