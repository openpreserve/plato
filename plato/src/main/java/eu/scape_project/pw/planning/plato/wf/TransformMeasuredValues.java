/**
 * 
 */
package eu.scape_project.pw.planning.plato.wf;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;

import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.tree.Leaf;

/**
 * Class containing business logic for workflow-step transform measured values.
 * 
 * @author Markus Hamm, Michael Kraxner
 */
@Stateful
@ConversationScoped
public class TransformMeasuredValues extends AbstractWorkflowStep {
    private static final long serialVersionUID = 8079697882774756837L;

    public TransformMeasuredValues() {
        requiredPlanState = PlanState.RESULTS_CAPTURED;
        correspondingPlanState = PlanState.TRANSFORMATION_DEFINED;
    }

    /**
     * sets (primitive) default values for all numeric and boolean transformers
     */
    public void calculateDefaultTransformers() {
        plan.calculateDefaultTransformers();
    }

    /**
     * Method responsible for approving transformer settings for the given
     * leaves. (Approval is done by touching the leaves transformer.)
     * 
     * @param leaves
     *            Leaves whose transformer settings should be approved.
     */
    public void approve(List<Leaf> leaves) {
        for (Leaf leaf : leaves) {
            leaf.getTransformer().touch();
        }
    }

    @Override
    protected void saveStepSpecific() {
        prepareChangesForPersist.prepare(plan);

        saveEntity(plan.getTree());
        saveEntity(plan.getTransformation());
    }
}
