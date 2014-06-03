package eu.scape_project.planning.services.myexperiment;

import java.util.List;

import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription;
import eu.scape_project.planning.services.taverna.generator.T2FlowExecutablePlanGenerator.InputSource;
import eu.scape_project.planning.services.taverna.generator.T2FlowExecutablePlanGenerator.RelatedObject;

/**
 * Describes a recommended component and related data.
 */
public final class RecommendedComponent {
    /**
     * Description of the recommended component workflow.
     */
    public final WorkflowDescription workflow;

    /**
     * Measures recommended that this component is used for.
     */
    public final List<String> measures;

    /**
     * The related object of the measures for the component.
     */
    public final RelatedObject relatedObject;

    /**
     * Source for left input.
     */
    public final InputSource leftSource;

    /**
     * Source for right input.
     */
    public final InputSource rightSource;

    /**
     * Creates a new recommended component.
     * 
     * @param workflowDescription
     *            the workflow description
     * @param measures
     *            the measures of this recommended component
     * @param relatedObject
     *            the related object or null
     */
    public RecommendedComponent(final WorkflowDescription workflowDescription, final List<String> measures,
        final InputSource leftSource, final InputSource rightSource, final RelatedObject relatedObject) {
        this.workflow = workflowDescription;
        this.measures = measures;
        this.leftSource = leftSource;
        this.rightSource = rightSource;
        this.relatedObject = relatedObject;
    }
}