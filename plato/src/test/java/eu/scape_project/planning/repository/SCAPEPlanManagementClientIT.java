package eu.scape_project.planning.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.StringReader;

import org.custommonkey.xmlunit.Diff;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import eu.scape_project.planning.annotation.ManualTest;
import eu.scape_project.planning.utils.FileUtils;

@ManualTest
public class SCAPEPlanManagementClientIT {
    private static final Logger LOG = LoggerFactory.getLogger(SCAPEPlanManagementClientIT.class);

    private static SCAPEPlanManagementClient client;
    
    @BeforeClass
    public static void setUp() {
        client = new SCAPEPlanManagementClient("http://localhost:6080/fcrepo/rest/scape/", "", "");
    }
    

    @Test
    public void reservePlanIdentifierTest() throws Exception {
        String id = client.reservePlanIdentifier();
        assertNotNull(id);
        assertFalse("".equals(id));
    }
    
    @Test
    public void deployPlanTest() throws Exception {
        assertTrue(client.deployPlan("testident",  getClass().getClassLoader().getResourceAsStream("plans/plan_with_pap.xml")));
        InputStream in = client.retrievePlan("testident");
        String retrievedPlan = new String(FileUtils.inputStreamToBytes(in));
        LOG.debug("plan: {}", retrievedPlan);
        Diff diff = new Diff(new InputSource(getClass().getClassLoader().getResourceAsStream("plans/plan_with_pap.xml")),
            new InputSource(new StringReader(retrievedPlan)));
        assertTrue(diff.similar());
    }
}
