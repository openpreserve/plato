package eu.scape_project.planning.plato.wf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.context.bound.Bound;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.MutableBoundRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.application.PlatoDeploymentBuilder;
import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.validation.ValidationError;

@RunWith(Arquillian.class)
public class FullPlanningworkflowIT {

    private static final Logger log = LoggerFactory.getLogger(FullPlanningworkflowIT.class);

    @Inject
    @Bound
    private BoundConversationContext conversationContext;

    @Inject
    private Instance<DefineBasis> defineBasisSource;
    @Inject
    private Instance<DefineSampleObjects> defineSampleObjectSource;

    @Inject
    private User user;

    @Deployment
    public static WebArchive createDeployment() {

        WebArchive platoWar = PlatoDeploymentBuilder.createPlatoWebArchive();
        platoWar.addClasses(AbstractWorkflowStep.class, DefineBasis.class, DefineAlternatives.class,
            FullPlanningworkflowIT.class, DefineSampleObjects.class);
        ;
        return platoWar;
    }

    @Test
    public void test() throws PlanningException {
        log.info("entering test");

        conversationContext.associate(new MutableBoundRequest(new HashMap<String, Object>(),
            new HashMap<String, Object>()));
        conversationContext.activate();

        Assert.assertTrue(conversationContext.isActive());

        DefineBasis defineBasis = defineBasisSource.get();
        Assert.assertNotNull(defineBasis);

        Plan plan = new Plan();
        plan.getPlanProperties().setName("Test Plan");
        plan.getPlanProperties().setAuthor(user.getUsername());
        plan.getPlanProperties().setOwner(user.getUsername());

        defineBasis.init(plan);
        Assert.assertNotNull(defineBasis.getPlan());

        defineBasis.save();
        log.info("stored plan");
        List<ValidationError> errors = new ArrayList<ValidationError>();

        Assert.assertTrue(defineBasis.proceed(errors));
        Assert.assertEquals(PlanState.BASIS_DEFINED, plan.getPlanProperties().getState());

        DefineSampleObjects defineSamples = defineSampleObjectSource.get();
        defineSamples.init(plan);

        Assert.assertFalse(defineSamples.proceed(errors));
        Assert.assertTrue(errors.size() != 0);
        for (ValidationError ve : errors) {
            log.debug(ve.getMessage());
        }
    }

}
