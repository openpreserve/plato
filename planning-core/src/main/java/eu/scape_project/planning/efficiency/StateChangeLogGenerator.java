package eu.scape_project.planning.efficiency;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.DMinMax;
import org.supercsv.cellprocessor.constraint.NotNull;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.ChangeLog;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Experiment;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.Values;
import eu.scape_project.planning.model.scales.Scale;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.tree.TreeNode;
import eu.scape_project.planning.model.values.Value;

public class StateChangeLogGenerator extends StatisticsGenerator {
    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(StateChangeLogGenerator.class);

    /**
     * Creates a new plan statistic generator which loads the plans via the
     * provided entity manager and outputs to the given writer.
     * 
     * @param writer
     * @param em
     * @throws IOException
     */
    public StateChangeLogGenerator(Writer writer, EntityManager em) throws IOException {
        super(writer, em);

        setupColumns();
        // write the header
        listWriter.writeHeader(headers);
    }

    /**
     * Writes the statistics of the given plan to the writer.
     * 
     * @param plan
     * @throws IOException
     */
    public void writeStatistics(Plan plan) throws IOException {
        List<StateChangeLog> stateChangeLogs = generateStatistics(plan);
        for (StateChangeLog stateChangeLog : stateChangeLogs) {
            listWriter.write(stateChangeLog, headers, processors);
        }
        listWriter.flush();
    }

    /**
     * Generates the statistics for the given plan.
     * 
     * @param plan
     * @return
     */
    private List<StateChangeLog> generateStatistics(Plan plan) {
        List<StateChangeLog> stateChangeLogs = new ArrayList<StateChangeLog>();

        long planId = plan.getPlanProperties().getId();

        long createdAt = plan.getChangeLog().getCreated();
        // 0 CREATED
        // 1 INITIALISED
        ChangeLog changelog = plan.getChangeLog();
        
        stateChangeLogs.add(new StateChangeLog(planId, 1, createdAt, createdAt, changelog.getCreatedBy()));

        // 2 Define Basis - BASIS_DEFINED(2)
        // - ProjectBasis
        changelog = plan.getProjectBasis().getChangeLog();
        stateChangeLogs.add(new StateChangeLog(planId, 2, createdAt, changelog.getChanged(), changelog.getChangedBy()));

        // 3. Define Sample Objects - RECORDS_CHOSEN(3)
        // - SampleRecordsDefinition
        // - samples (also created here)
        changelog = plan.getSampleRecordsDefinition().getChangeLog();
        if (changelog.getChanged() > createdAt) {
            stateChangeLogs.add(new StateChangeLog(planId, 3, createdAt, changelog.getChanged(), changelog.getChangedBy()));
        }
        for (SampleObject sample : plan.getSampleRecordsDefinition().getRecords()) {
            ChangeLog sampleCL = sample.getChangeLog();
            stateChangeLogs.add(new StateChangeLog(planId, 3, createdAt, sampleCL.getCreated(), sampleCL.getCreatedBy()));
            if (sampleCL.getChanged() > sampleCL.getCreated()) {
                stateChangeLogs.add(new StateChangeLog(planId, 3, createdAt, sampleCL.getChanged(), sampleCL.getChangedBy()));
            }
        }

        // 4. Identify requirements - TREE_DEFINED(4, "Tree Defined"),
        // - nodes/leaves (also created!)
        // - scales
        // - RequirementsDefinition
        changelog = plan.getRequirementsDefinition().getChangeLog();
        if (changelog.getChanged() > createdAt) {
            stateChangeLogs.add(new StateChangeLog(planId, 4, createdAt, changelog.getChanged(), changelog.getChangedBy()));
        }

        List<TreeNode> nodes = plan.getTree().getRoot().getAllChildren();
        for (TreeNode treeNode : nodes) {
            ChangeLog nodeCL = treeNode.getChangeLog();

            stateChangeLogs.add(new StateChangeLog(planId, 4, createdAt, nodeCL.getCreated(), nodeCL.getCreatedBy()));

            // we can only use the scales of leaves, as
            // - nodes can be changed when weighting is changed
            // - leaves are "changed" when evaluated, aggregation mode is
            // changed
            if (treeNode instanceof Leaf) {
                Scale scale = ((Leaf) treeNode).getScale();
                if (scale != null) {
                    ChangeLog scaleCL = scale.getChangeLog();
                    stateChangeLogs.add(new StateChangeLog(planId, 4, createdAt, scaleCL.getChanged(), scaleCL.getChangedBy()));

                }
            }
        }

        // 5. Define alternatives - ALTERNATIVES_DEFINED(5,
        // "Alternatives Defined")
        // - AlternativesDefinition
        // - Alternative - only created, "changed" can be overwritten in
        // GoDecision)
        changelog = plan.getAlternativesDefinition().getChangeLog();
        if (changelog.getChanged() > createdAt) {
            stateChangeLogs.add(new StateChangeLog(planId, 5, createdAt, changelog.getChanged(), changelog.getChangedBy()));
        }

        for (Alternative alternative : plan.getAlternativesDefinition().getAlternatives()) {
            ChangeLog altCL = alternative.getChangeLog();
            stateChangeLogs.add(new StateChangeLog(planId, 5, createdAt, altCL.getCreated(), altCL.getCreatedBy()));
        }

        // 6. Take go decision - GO_CHOSEN(6, "Go Decision Taken")
        // - no: Alternative (! - if discarded)
        // - GoDecision
        changelog = plan.getDecision().getChangeLog();
        if (changelog.getChanged() > createdAt) {
            stateChangeLogs.add(new StateChangeLog(planId, 6, createdAt, changelog.getChanged(), changelog.getChangedBy()));
        }

        // 7. Develop Experiments - EXPERIMENT_DEFINED(7, "Experiments Defined")
        // Alternative.Experiment
        // as the experiment is created together with the alternative, this
        // might result in the same timestamps
        for (Alternative alternative : plan.getAlternativesDefinition().getAlternatives()) {
            ChangeLog expCL = alternative.getExperiment().getChangeLog();
            stateChangeLogs.add(new StateChangeLog(planId, 7, createdAt, expCL.getChanged(), expCL.getChangedBy()));
        }
        // 8. Run Experiments - EXPERIMENT_PERFORMED(8, "Experiments Performed")
        // - detailedExperimentInfo
        // - result files (alternative.experiment.results)
        // as the experiment is created together with the alternative, this
        // might result in the same timestamps
        for (Alternative alternative : plan.getAlternativesDefinition().getAlternatives()) {
            Experiment experiment = alternative.getExperiment();
            // - result files (alternative.experiment.results)
            for (DigitalObject result : experiment.getResults().values()) {
                ChangeLog resultCL = result.getChangeLog();
                stateChangeLogs.add(new StateChangeLog(planId, 8, createdAt, resultCL.getChanged(), resultCL.getChangedBy()));
            }
        }

        // 9. Evaluate Experiments - RESULTS_CAPTURED(9, "Results Captured")
        // - Evaluation
        // - ValueMap-Values
        changelog = plan.getEvaluation().getChangeLog();
        if (changelog.getChanged() > createdAt) {
            stateChangeLogs.add(new StateChangeLog(planId, 9, createdAt, changelog.getChanged(), changelog.getChangedBy()));
        }

        for (TreeNode treeNode : nodes) {
            if (treeNode instanceof Leaf) {
                Leaf leaf = (Leaf) treeNode;
                // ValueMap-Values
                for (Values values : leaf.getValueMap().values()) {
                    for (Value value : values.getList()) {
                        ChangeLog valueCL = value.getChangeLog();
                        stateChangeLogs
                            .add(new StateChangeLog(planId, 9, createdAt, valueCL.getChanged(), valueCL.getChangedBy()));
                    }
                }
            }
        }

        // 10. Transform measured values - TRANSFORMATION_DEFINED(10,
        // "Transformations Defined")
        // - leaf.transformer
        // - Transformation
        // - leaf (!! - leaf.aggregationMode )
        changelog = plan.getTransformation().getChangeLog();
        if (changelog.getChanged() > createdAt) {
            stateChangeLogs.add(new StateChangeLog(planId, 10, createdAt, changelog.getChanged(), changelog.getChangedBy()));
        }

        for (TreeNode treeNode : nodes) {
            if (treeNode instanceof Leaf) {
                Leaf leaf = (Leaf) treeNode;
                if (leaf.getTransformer() != null) {
                    ChangeLog tCL = leaf.getTransformer().getChangeLog();
                    stateChangeLogs.add(new StateChangeLog(planId, 10, createdAt, tCL.getChanged(), tCL.getChangedBy()));
                }
            }
        }

        // 11. Set importance factors - WEIGHTS_SET(11, "Weights Set")
        // - no: node (! - lock )
        // - ImportanceWeighting
        changelog = plan.getImportanceWeighting().getChangeLog();
        if (changelog.getChanged() > createdAt) {
            stateChangeLogs.add(new StateChangeLog(planId, 11, createdAt, changelog.getChanged(), changelog.getChangedBy()));
        }

        // 12. Analyze results - ANALYSED(12, "Analyzed")
        // - Recommendation
        changelog = plan.getRecommendation().getChangeLog();
        if (changelog.getChanged() > createdAt) {
            stateChangeLogs.add(new StateChangeLog(planId, 12, createdAt, changelog.getChanged(), changelog.getChangedBy()));
        }
        //
        // 13. Create Executable plan - EXECUTEABLE_PLAN_CREATED(13,
        // "Executable Plan Created")
        // - ExecutablePlanDefinition
        changelog = plan.getExecutablePlanDefinition().getChangeLog();
        if (changelog.getChanged() > createdAt) {
            stateChangeLogs.add(new StateChangeLog(planId, 13, createdAt, changelog.getChanged(), changelog.getChangedBy()));
        }

        //
        // 14. Define Plan - PLAN_DEFINED(14, "Plan Defined")
        // - PlanDefinition
        changelog = plan.getPlanDefinition().getChangeLog();
        if (changelog.getChanged() > createdAt) {
            stateChangeLogs.add(new StateChangeLog(planId, 14, createdAt, changelog.getChanged(), changelog.getChangedBy()));
        }

        // 15. Validate Plan
        if (plan.getPlanProperties().getState() == PlanState.PLAN_VALIDATED) {
            changelog = plan.getPlanProperties().getChangeLog();
            if (changelog.getChanged() > createdAt) {
                stateChangeLogs.add(new StateChangeLog(planId, 15, createdAt, changelog.getChanged(), changelog.getChangedBy()));
            }
        }

        return stateChangeLogs;
    }

    protected void setupColumns() {
        addColumn("planId", new NotNull());
        addColumn("stageNr", new LUndef());
        addColumn("hoursSinceStart", new DMinMax(0.0, Double.MAX_VALUE));
        addColumn("user", new Optional());
        addColumn("timestamp", new FmtDate("yyyy-MM-dd'T'HH:mm:ss"));

        finishColumns();
    }
}
