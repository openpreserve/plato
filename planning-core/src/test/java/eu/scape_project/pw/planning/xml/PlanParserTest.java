package eu.scape_project.pw.planning.xml;

import java.io.InputStream;
import java.util.List;

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
        Assert.assertNotNull(plan.getExecutablePlanDefinition());
        Assert.assertNotNull(plan.getPlanDefinition());
        Assert.assertNotNull(plan.getChangeLog());
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

        Assert.assertTrue(pp.getAuthor().equals("Test1 Test1"));
        Assert.assertTrue(pp.getOrganization().equals("TUW"));
        Assert.assertTrue(pp.getName().equals("PlanParserTest"));
        Assert.assertTrue(pp.isPrivateProject());
        Assert.assertFalse(pp.isReportPublic());
        Assert.assertTrue(pp.getPlanType() == PlanType.FULL);
        Assert.assertTrue(pp.getState() == PlanState.PLAN_VALIDATED);
        Assert.assertTrue(pp.getDescription().equals("Testing plan"));
        Assert.assertTrue(pp.getOwner().equals("test1"));
    }

    @Test
    public void importProjectsProfileResultsPAPExecutablePlanDefinition() throws PlatoException {
        PlanParser parser = new PlanParser();

        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("plans/PlanParserTest/PlanParserTest-PLAN_VALIDATED-ProfileResultsPAP.xml");

        List<Plan> plans = parser.importProjects(in);

        Assert.assertTrue(plans.size() == 1);

        Plan plan = plans.get(0);
        ExecutablePlanDefinition ex = plan.getExecutablePlanDefinition();

        Assert.assertNull(ex.getObjectPath());
        Assert.assertTrue("png".equals(ex.getToolParameters()));
        Assert.assertTrue("".equals(ex.getTriggersConditions()));
        Assert.assertTrue("".equals(ex.getValidateQA()));
        Assert.assertNull(ex.getExecutablePlan());

        Assert.assertNotNull(ex.getT2flowExecutablePlan());
        Assert.assertFalse(ex.getT2flowExecutablePlan().getFullname().equals(""));
        Assert.assertNotNull(ex.getT2flowExecutablePlan().getData());
        Assert.assertNotNull(ex.getT2flowExecutablePlan().getData().getRealByteStream());
        Assert.assertNotNull(ex.getT2flowExecutablePlan().getData().getRealByteStream().getData());
        Assert.assertTrue(ex.getT2flowExecutablePlan().getData().getRealByteStream().getData().length > 0);

        Assert.assertNotNull(ex.getPreservationActionPlan());
        Assert.assertFalse(ex.getPreservationActionPlan().getFullname().equals(""));
        Assert.assertNotNull(ex.getPreservationActionPlan().getData());
        Assert.assertNotNull(ex.getPreservationActionPlan().getData().getRealByteStream());
        Assert.assertNotNull(ex.getPreservationActionPlan().getData().getRealByteStream().getData());
        Assert.assertTrue(ex.getPreservationActionPlan().getData().getRealByteStream().getData().length > 0);
    }
}
