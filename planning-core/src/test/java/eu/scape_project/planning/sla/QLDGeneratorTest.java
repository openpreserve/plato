package eu.scape_project.planning.sla;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.xml.PlanParser;


public class QLDGeneratorTest {

    @Test
    public void generateQLDsBasedOnPlan() throws Exception{
        PlanParser  planParser = new PlanParser();
        List<Plan> plans = planParser.importProjects(getClass().getClassLoader().getResourceAsStream("qld/plan.xml"));
        assertNotNull(plans);
        assertEquals(1, plans.size());
        
        Plan plan = plans.get(0);

        QLDGenerator qldGen = new QLDGenerator();
        
        qldGen.generateQLD(plan);
        String qlds = qldGen.getQLDs();
        assertTrue(qlds.length() > 0);
//        System.out.println(qlds);
    }
    
}
