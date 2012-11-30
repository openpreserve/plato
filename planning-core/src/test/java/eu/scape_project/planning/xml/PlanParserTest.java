package eu.scape_project.planning.xml;

import java.io.InputStream;
import java.util.List;

import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.ExecutablePlanDefinition;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanProperties;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.PlanType;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.xml.PlanParser;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PlanParserTest {
    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    @Test
    public void importProjectsMinimalMainOjects() throws PlatoException {
        PlanParser parser = new PlanParser();

        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("plans/PlanParserTest/PlanParserTest-PLAN_VALIDATED-minimal.xml");

        List<Plan> plans = parser.importProjects(in);

        Assert.assertTrue(plans.size() == 1);

        Plan plan = plans.get(0);
        Assert.assertNotNull(plan.getPlanProperties());
        Assert.assertNotNull(plan.getSampleRecordsDefinition());
        Assert.assertNotNull(plan.getProjectBasis());
        Assert.assertNotNull(plan.getRequirementsDefinition());
        Assert.assertNotNull(plan.getTree());
        Assert.assertNotNull(plan.getAlternativesDefinition());
        Assert.assertNotNull(plan.getEvaluation());
        Assert.assertNotNull(plan.getTransformation());
        Assert.assertNotNull(plan.getImportanceWeighting());
        Assert.assertNotNull(plan.getRecommendation());
        Assert.assertNotNull(plan.getDecision());
        Assert.assertNotNull(plan.getExecutablePlanDefinition());
        Assert.assertNotNull(plan.getPreservationActionPlan());
        Assert.assertNotNull(plan.getPlanDefinition());

        Assert.assertNotNull(plan.getChangeLog());
        Assert.assertTrue(plan.getChangeLog().getCreatedBy().equals("test1"));
    }

    @Test
    public void importProjectsMinimalPlanProperties() throws PlatoException {
        PlanParser parser = new PlanParser();

        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("plans/PlanParserTest/PlanParserTest-PLAN_VALIDATED-minimal.xml");

        List<Plan> plans = parser.importProjects(in);

        Assert.assertTrue(plans.size() == 1);

        Plan plan = plans.get(0);
        PlanProperties pp = plan.getPlanProperties();

        Assert.assertTrue("PlanParser Test - minimal".equals(pp.getName()));
        Assert.assertTrue("Test".equals(pp.getDescription()));
        Assert.assertTrue("Test1 Test1".equals(pp.getAuthor()));
        Assert.assertTrue("TUW".equals(pp.getOrganization()));
        Assert.assertTrue(pp.isPrivateProject());
        Assert.assertFalse(pp.isReportPublic());
        Assert.assertTrue(pp.getPlanType() == PlanType.FULL);
        Assert.assertTrue(pp.getState() == PlanState.PLAN_VALIDATED);
        Assert.assertTrue("test1".equals(pp.getOwner()));
        Assert.assertNotNull(pp.getReportUpload());
        Assert.assertTrue("".equals(pp.getOpenedByUser()));

        Assert.assertNotNull(pp.getChangeLog());
        Assert.assertTrue("test1".equals(pp.getChangeLog().getCreatedBy()));
    }

    @Test
    public void importProjectsProfileSamplesPAPExecutablePlanDefinition() throws PlatoException {
        PlanParser parser = new PlanParser();

        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("plans/PlanParserTest/PlanParserTest-PLAN_VALIDATED-ProfileSamplesPAP.xml");

        List<Plan> plans = parser.importProjects(in);

        Assert.assertTrue(plans.size() == 1);

        Plan plan = plans.get(0);
        ExecutablePlanDefinition ex = plan.getExecutablePlanDefinition();

        Assert.assertNull(ex.getObjectPath());
        Assert.assertTrue("".equals(ex.getToolParameters()));
        Assert.assertTrue("".equals(ex.getTriggersConditions()));
        Assert.assertTrue("".equals(ex.getValidateQA()));
        Assert.assertNull(ex.getExecutablePlan());

        DigitalObject t2flow = ex.getT2flowExecutablePlan();
        Assert.assertNull(t2flow.getPid());
        Assert.assertTrue("Create_tmp_file_and_convert_by_target_extension.t2flow".equals(t2flow.getFullname()));
        Assert.assertTrue("application/vnd.taverna.t2flow+xml".equals(t2flow.getContentType()));

        Assert.assertNull(t2flow.getJhoveXMLString());
        Assert.assertNull(t2flow.getFitsXMLString());
        Assert.assertNull(t2flow.getXcdlDescription());
        Assert.assertNotNull(t2flow.getFormatInfo());

        Assert.assertTrue(t2flow.getSizeInBytes() == 0);
        Assert.assertTrue(t2flow.getSizeInMB() == 0);

        Assert.assertNotNull(t2flow.getData());
        Assert.assertNotNull(t2flow.getData());
        Assert.assertNotNull(t2flow.getData().getData());
        Assert.assertTrue(t2flow.getData().getData().length > 0);
        Assert.assertTrue(t2flow.getData().getSize() > 0);

        Assert.assertNotNull(t2flow.getChangeLog());
        Assert.assertNull(t2flow.getChangeLog().getCreatedBy());

        Assert.assertNotNull(ex.getChangeLog());
        Assert.assertTrue(ex.getChangeLog().getCreatedBy().equals("test1"));
    }

    @Test
    public void importProjectsProfileSamplesPAPPreservationActionPlan() throws PlatoException {
        PlanParser parser = new PlanParser();

        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("plans/PlanParserTest/PlanParserTest-PLAN_VALIDATED-ProfileSamplesPAP.xml");

        List<Plan> plans = parser.importProjects(in);

        Assert.assertTrue(plans.size() == 1);

        Plan plan = plans.get(0);
        DigitalObject pap = plan.getPreservationActionPlan();

        Assert.assertNull(pap.getPid());
        Assert.assertTrue("PreservationActionPlan.xml".equals(pap.getFullname()));
        Assert.assertTrue("application/xml".equals(pap.getContentType()));

        Assert.assertNull(pap.getJhoveXMLString());
        Assert.assertNull(pap.getFitsXMLString());
        Assert.assertNull(pap.getXcdlDescription());
        Assert.assertNotNull(pap.getFormatInfo());

        Assert.assertTrue(pap.getSizeInBytes() == 0);
        Assert.assertTrue(pap.getSizeInMB() == 0);

        Assert.assertNotNull(pap.getData());
        Assert.assertNotNull(pap.getData());
        Assert.assertNotNull(pap.getData().getData());
        Assert.assertTrue(pap.getData().getData().length > 0);
        Assert.assertTrue(pap.getData().getSize() > 0);

        Assert.assertNotNull(pap.getChangeLog());
        Assert.assertNull(pap.getChangeLog().getCreatedBy());
    }
}
