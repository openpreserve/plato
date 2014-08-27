package eu.scape_project.planning.efficiency;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.DMinMax;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.NotNull;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.ChangeLog;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Experiment;
import eu.scape_project.planning.model.IChangesHandler;
import eu.scape_project.planning.model.ITouchable;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanProperties;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.ProjectBasis;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.SampleRecordsDefinition;
import eu.scape_project.planning.model.Values;
import eu.scape_project.planning.model.scales.Scale;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.tree.TreeNode;
import eu.scape_project.planning.model.values.Value;
import eu.scape_project.planning.validation.ValidationError;

public class PlanStatisticsGenerator extends StatisticsGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlanStatisticsGenerator.class);

    class ChangelogStatistics implements IChangesHandler {
        Set<String> users = new HashSet<String>();

        @Override
        public void visit(ITouchable t) {
            String user = t.getChangeLog().getChangedBy();
            if (user != null) {
                users.add(user);
            }
            user = t.getChangeLog().getCreatedBy();
            if (user != null) {
                users.add(user);
            }
        }

        public int getNumberOfUsers() {
            return users.size();
        }

    }

    /**
     * Creates a new plan statistic generator which loads the plans via the
     * provided entity manager and outputs to the given writer.
     * 
     * @param writer
     * @param em
     * @throws IOException
     */
    public PlanStatisticsGenerator(Writer writer, EntityManager em) throws IOException {
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
        PlanStatistics stats = generateStatistics(plan);
        listWriter.write(stats, headers, processors);
        listWriter.flush();
    }

    /**
     * Generates the statistics for the given plan.
     * 
     * @param plan
     * @return
     */
    private PlanStatistics generateStatistics(Plan plan) {
        PlanProperties properties = plan.getPlanProperties();
        ProjectBasis basis = plan.getProjectBasis();
        SampleRecordsDefinition samples = plan.getSampleRecordsDefinition();

        long numSamples = samples.getRecords().size();
        List<Leaf> leaves = plan.getTree().getRoot().getAllLeaves();
        long numLeaves = leaves.size();
        long numMappedLeaves = 0;
        long numMeasuresNeededTotal = 0;
        long numEvaluated = 0;
        long numAlternatives = plan.getAlternativesDefinition().getAlternatives().size();
        long numConsideredAlternatives = plan.getAlternativesDefinition().getConsideredAlternatives().size();

        // determine the number of users who have been working on the plan
        ChangelogStatistics changelogStatistics = new ChangelogStatistics();
        plan.handleChanges(changelogStatistics);
        int numDistinctUsers = changelogStatistics.getNumberOfUsers();

        double percentageDefinedTransformers = 0.0;
        List<String> consideredAltNames = new ArrayList<String>();
        for (Alternative alt : plan.getAlternativesDefinition().getConsideredAlternatives()) {
        	consideredAltNames.add(alt.getName());
        }
        List<ValidationError> errors = new ArrayList<ValidationError>();
        for (Iterator<Leaf> iter = leaves.iterator(); iter.hasNext();) {
            Leaf l = iter.next();
            if (l.isMapped()) {
                numMappedLeaves++;
            }
            int numMeasuresNeeded = 0;
            if (l.isSingle()) {
                numMeasuresNeeded += numConsideredAlternatives;
            } else {
                numMeasuresNeeded += (numConsideredAlternatives * numSamples);
            }
            numMeasuresNeededTotal += numMeasuresNeeded;

            for (Iterator<String> alts = consideredAltNames.iterator(); alts.hasNext();) {
                String alt = alts.next();
                Values values = l.getValues(alt);
                if (values != null) {
	                for (Iterator<Value> valueiter = values.getList().iterator(); valueiter.hasNext();) {
	                    Value value = valueiter.next();
	                    if ((value != null) && (value.getScale() != null) && value.isEvaluated()) {
	                        numEvaluated++;
	                    }
	                }
                }
            }

            if (l.getTransformer() != null) {
                if (l.isCompletelyTransformed(errors)) {
                    percentageDefinedTransformers += 1;
                }
            }
        }
        percentageDefinedTransformers = percentageDefinedTransformers / numLeaves;
        double percentagePopulatedValues = (numMeasuresNeededTotal == 0)? 0.0 : numEvaluated / (double) numMeasuresNeededTotal;

        String creatorUsername = properties.getOwner();
        String creatorEmail = null;
        try {
            if (em != null) {
                creatorEmail = em.createQuery("select email from User where username = :username", String.class)
                    .setParameter("username", creatorUsername).getSingleResult();
            }
        } catch (Exception e) {
            LOGGER.debug("Failed to retrieve creator email.", e);
        }
        int numPlansCreated = 0;

        try {
            if (em != null) {
                numPlansCreated = (em.createQuery("select count(*) from PlanProperties p where p.owner = :username",
                    Long.class).setParameter("username", creatorUsername).getSingleResult()).intValue();
            }
        } catch (Exception e) {
            LOGGER.debug("Failed to determine number of plans created", e);
        }
        Date decisionOn = null;
        if (plan.getRecommendation().getAlternative() != null) {
            decisionOn = new Date(plan.getRecommendation().getChangeLog().getChanged()); // decision
                                                                                         // on
        }

        PlanStatistics statistics = new PlanStatistics(
            plan.getId(),
            properties.getId(),
            creatorUsername,
            properties.getAuthor(),
            creatorEmail,
            properties.getName(),
            properties.getState().getValue(), // "status"
            new Date(plan.getChangeLog().getCreated()), // created on
            decisionOn, // decision on
            properties.getChangeLog().getChanged(), // "stage last accessed"
            properties.getState().getValue(), // "highest stage achieved"
            numSamples, // # samples
            numLeaves, // # leaves
            numMappedLeaves, // # mapped leaves
            numMeasuresNeededTotal, // # measures needed
            numAlternatives, // # alternatives
            StringUtils.length(basis.getDocumentTypes()), StringUtils.length(properties.getDescription()),
            StringUtils.length(basis.getMandate()), StringUtils.length(basis.getPlanningPurpose()),
            StringUtils.length(basis.getDesignatedCommunity()), StringUtils.length(basis.getApplyingPolicies()),
            StringUtils.length(basis.getOrganisationalProcedures()), StringUtils.length(basis.getPreservationRights()),
            StringUtils.length(basis.getReferenceToAgreements()), StringUtils.length(basis.getPlanRelations()),
            StringUtils.length(basis.getTriggers().getNewCollection().getDescription()), StringUtils.length(basis
                .getTriggers().getPeriodicReview().getDescription()), StringUtils.length(basis.getTriggers()
                .getChangedEnvironment().getDescription()), StringUtils.length(basis.getTriggers()
                .getChangedObjective().getDescription()), StringUtils.length(basis.getTriggers()
                .getChangedCollectionProfile().getDescription()), StringUtils.length(samples.getCollectionProfile()
                .getDescription()), StringUtils.length(samples.getCollectionProfile().getTypeOfObjects()),
            StringUtils.length(samples.getCollectionProfile().getExpectedGrowthRate()), StringUtils.length(samples
                .getCollectionProfile().getRetentionPeriod()), StringUtils.length(samples.getSamplesDescription()),
            StringUtils.length(plan.getRequirementsDefinition().getDescription()),
            // mean of leaf-comment length ?
            StringUtils.length(plan.getAlternativesDefinition().getDescription()), StringUtils.length(plan
                .getDecision().getReason()), StringUtils.length(plan.getDecision().getActionNeeded()),
            // mean of alternative description length ?
            StringUtils.length(plan.getEvaluation().getComment()),
            // mean of comment length of evaluation values?
            StringUtils.length(plan.getImportanceWeighting().getComment()),
            // and percentage of leaves with changed weight?
            StringUtils.length(plan.getRecommendation().getReasoning()), StringUtils.length(plan.getRecommendation()
                .getEffects()), StringUtils.length(plan.getPlanDefinition().getCostsRemarks()),
            percentagePopulatedValues, percentageDefinedTransformers, numDistinctUsers, numPlansCreated);
        // and add information on changelogs
        analyseChangelogs(statistics, plan);
        
        return statistics;

    }

    private void analyseChangelogs(PlanStatistics statistics, Plan plan) {
        long[] minChangeLogs = new long[PlanStatistics.MAX_STATE + 1];
        long[] maxChangeLogs = new long[PlanStatistics.MAX_STATE + 1];

        PlanState maxState = plan.getPlanProperties().getState();

        long createdAt = plan.getChangeLog().getCreated();

        // 0 CREATED
        minChangeLogs[0] = createdAt;
        maxChangeLogs[0] = createdAt;
        // 1 INITIALISED
        minChangeLogs[1] = createdAt;
        maxChangeLogs[1] = createdAt;

        // 2 Define Basis - BASIS_DEFINED(2)
        // - ProjectBasis
        minChangeLogs[2] = plan.getProjectBasis().getChangeLog().getChanged();
        maxChangeLogs[2] = minChangeLogs[2];

        // 3. Define Sample Objects - RECORDS_CHOSEN(3)
        // - SampleRecordsDefinition
        // - samples (also created here)
        long minCL = plan.getSampleRecordsDefinition().getChangeLog().getChanged();
        long maxCL = minCL;
        if (minCL <= createdAt) {
        	minCL = Long.MAX_VALUE;
        }

        for (SampleObject sample : plan.getSampleRecordsDefinition().getRecords()) {
            ChangeLog sampleCL = sample.getChangeLog();
            if (sampleCL.getChanged() > maxCL) {
                maxCL = sampleCL.getChanged();
            } 
            if (sampleCL.getCreated() < minCL) {
                minCL = sampleCL.getCreated();
            }
        }
        minChangeLogs[3] = minCL;
        maxChangeLogs[3] = maxCL;

        // 4. Identify requirements - TREE_DEFINED(4, "Tree Defined"),
        // - nodes/leaves (also created!)
        // - scales
        // - RequirementsDefinition
        minCL = plan.getRequirementsDefinition().getChangeLog().getChanged();
        maxCL = minCL;
        if (minCL <= createdAt) {
        	minCL = Long.MAX_VALUE;
        }

        List<TreeNode> nodes = plan.getTree().getRoot().getAllChildren();
        for (TreeNode treeNode : nodes) {
            ChangeLog nodeCL = treeNode.getChangeLog();
            // we can only consider the creation time stamp, as changed is used
            // to confirm values
            if (minCL > nodeCL.getCreated()) {
                minCL = nodeCL.getCreated();
            }
            if (maxCL < nodeCL.getCreated()) {
                maxCL = nodeCL.getCreated();
            }
            // we can only use the scales of leaves, as
            // - nodes can be changed when weighting is changed
            // - leaves are "changed" when evaluated, aggregation mode is
            // changed
            if (treeNode instanceof Leaf) {
                Scale scale = ((Leaf) treeNode).getScale();
                if (scale != null) {
                    ChangeLog scaleCL = scale.getChangeLog();
                    if (maxCL < scaleCL.getChanged()) {
                        maxCL = scaleCL.getChanged();
                    }
                }
            }
        }
        minChangeLogs[4] = minCL;
        maxChangeLogs[4] = maxCL;

        // 5. Define alternatives - ALTERNATIVES_DEFINED(5,
        // "Alternatives Defined")
        // - AlternativesDefinition
        // - Alternative - only created, "changed" can be overwritten in
        // GoDecision)
        minCL = plan.getAlternativesDefinition().getChangeLog().getChanged();
        maxCL = minCL;
        if (minCL <= createdAt) {
        	minCL = Long.MAX_VALUE;
        }
        for (Alternative alternative : plan.getAlternativesDefinition().getAlternatives()) {
            ChangeLog altCL = alternative.getChangeLog();
            if (minCL > altCL.getCreated()) {
                minCL = altCL.getCreated();
            }
            if (maxCL < altCL.getCreated()) {
                maxCL = altCL.getCreated();
            }
            // we cannot check for the changed(), as the alternative is also
            // changed in GoDecision
        }
        minChangeLogs[5] = minCL;
        maxChangeLogs[5] = maxCL;

        // 6. Take go decision - GO_CHOSEN(6, "Go Decision Taken")
        // - no: Alternative (! - if discarded)
        // - GoDecision
        minCL = plan.getDecision().getChangeLog().getChanged();
        maxCL = minCL;
        if (minCL <= createdAt) {
        	minCL = Long.MAX_VALUE;
        }
        // for (Alternative alternative :
        // plan.getAlternativesDefinition().getAlternatives()) {
        // ChangeLog altCL = alternative.getChangeLog();
        // if (maxCL < altCL.getChanged()) {
        // maxCL = altCL.getChanged();
        // }
        // }
        minChangeLogs[6] = minCL;
        maxChangeLogs[6] = maxCL;
        // 7. Develop Experiments - EXPERIMENT_DEFINED(7, "Experiments Defined")
        // Alternative.Experiment
        minCL = Long.MAX_VALUE;
        maxCL = Long.MIN_VALUE;
        // as the experiment is created together with the alternative, this
        // might result in the same timestamps
        for (Alternative alternative : plan.getAlternativesDefinition().getAlternatives()) {
            ChangeLog expCL = alternative.getExperiment().getChangeLog();
            if (maxCL < expCL.getChanged()) {
                maxCL = expCL.getChanged();
            }
            if (minCL > expCL.getChanged()) {
                minCL = expCL.getChanged();
            }
        }
        minChangeLogs[7] = minCL;
        maxChangeLogs[7] = maxCL;
        // 8. Run Experiments - EXPERIMENT_PERFORMED(8, "Experiments Performed")
        // - detailedExperimentInfo
        // - result files (alternative.experiment.results)
        // - initValues on save
        minCL = Long.MAX_VALUE;
        maxCL = Long.MIN_VALUE;
        // as the experiment is created together with the alternative, this
        // might result in the same timestamps
        for (Alternative alternative : plan.getAlternativesDefinition().getAlternatives()) {
            Experiment experiment = alternative.getExperiment();
            // - detailedExperimentInfo
//            for (DetailedExperimentInfo detailedInfo : experiment.getDetailedInfo().values()) {
//                ChangeLog diCL = detailedInfo.getChangeLog();
//                if (maxCL < diCL.getChanged()) {
//                    maxCL = diCL.getChanged();
//                }
//                if (minCL > diCL.getChanged()) {
//                    minCL = diCL.getChanged();
//                }
//            }
            // - result files (alternative.experiment.results)
            for (DigitalObject result : experiment.getResults().values()) {
                ChangeLog resultCL = result.getChangeLog();
                if (maxCL < resultCL.getChanged()) {
                    maxCL = resultCL.getChanged();
                }
                if (minCL > resultCL.getChanged()) {
                    minCL = resultCL.getChanged();
                }
            }
            // - initValues on save
            // cannot be used, as automatic evaluators replace the value objects
            // for (TreeNode treeNode : nodes) {
            // ChangeLog nodeCL = treeNode.getChangeLog();
            // if (treeNode instanceof Leaf) {
            // Leaf leaf = (Leaf)treeNode;
            // for (Values values : leaf.getValueMap().values()) {
            // for (Value value : values.getList()) {
            // ChangeLog valueCL = value.getChangeLog();
            // // only the creation time of the value is relevant!
            // if (maxCL.getChanged() < valueCL.getCreated()) {
            // maxCL = valueCL;
            // }
            // }
            // }
            // }
            // }
        }

        minChangeLogs[8] = minCL;
        maxChangeLogs[8] = maxCL;

        // 9. Evaluate Experiments - RESULTS_CAPTURED(9, "Results Captured")
        // - Evaluation
        // - ValueMap-Values
        minCL = plan.getEvaluation().getChangeLog().getChanged();
        maxCL = minCL;
        if (minCL <= createdAt) {
        	minCL = Long.MAX_VALUE;
        }
        for (TreeNode treeNode : nodes) {
            if (treeNode instanceof Leaf) {
                Leaf leaf = (Leaf) treeNode;
                // ValueMap-Values
                for (Values values : leaf.getValueMap().values()) {
                    for (Value value : values.getList()) {
                        ChangeLog valueCL = value.getChangeLog();
                        if (maxCL < valueCL.getChanged()) {
                            maxCL = valueCL.getChanged();
                        }
                    }
                }
                // values are created in a previous step, therefore the min
                // values cannot be set regarding the created timestamp
            }
        }
        minChangeLogs[9] = minCL;
        maxChangeLogs[9] = maxCL;

        // 10. Transform measured values - TRANSFORMATION_DEFINED(10,
        // "Transformations Defined")
        // - leaf.transformer
        // - Transformation
        // - leaf (!! - leaf.aggregationMode )
        minCL = plan.getTransformation().getChangeLog().getChanged();
        maxCL = minCL;
        if (minCL <= createdAt) {
        	minCL = Long.MAX_VALUE;
        }
        for (TreeNode treeNode : nodes) {
            if (treeNode instanceof Leaf) {
                Leaf leaf = (Leaf) treeNode;
                if (leaf.getTransformer() != null) {
                    ChangeLog tCL = leaf.getTransformer().getChangeLog();
                    if (maxCL < tCL.getChanged()) {
                        maxCL = tCL.getChanged();
                    }
                    if (minCL > tCL.getChanged()) {
                    	minCL = tCL.getChanged();
                    }
                }
            }
        }
        minChangeLogs[10] = minCL;
        maxChangeLogs[10] = maxCL;

        // 11. Set importance factors - WEIGHTS_SET(11, "Weights Set")
        // - no: node (! - lock )
        // - ImportanceWeighting
        minCL = plan.getImportanceWeighting().getChangeLog().getChanged();
        maxCL = minCL;
        if (minCL <= createdAt) {
        	minCL = Long.MAX_VALUE;
        }

        minChangeLogs[11] = minCL;
        maxChangeLogs[11] = maxCL;

        // 12. Analyse results - ANALYSED(12, "Analyzed")
        // - Recommendation
        minCL = plan.getRecommendation().getChangeLog().getChanged();
        maxCL = minCL;
        if (minCL <= createdAt) {
        	minCL = Long.MAX_VALUE;
        }
        minChangeLogs[12] = minCL;
        maxChangeLogs[12] = maxCL;
        //
        // 13. Create Executable plan - EXECUTEABLE_PLAN_CREATED(13,
        // "Executable Plan Created")
        // - ExecutablePlanDefinition
        minCL = plan.getExecutablePlanDefinition().getChangeLog().getChanged();
        maxCL = minCL;        
        if (minCL <= createdAt) {
        	minCL = Long.MAX_VALUE;
        }
        minChangeLogs[13] = minCL;
        maxChangeLogs[13] = maxCL;

        //
        // 14. Define Plan - PLAN_DEFINED(14, "Plan Defined")
        // - PlanDefinition
        minCL = plan.getPlanDefinition().getChangeLog().getChanged();
        maxCL = minCL;        
        if (minCL <= createdAt) {
        	minCL = Long.MAX_VALUE;
        }
        minChangeLogs[14] = minCL;
        maxChangeLogs[14] = maxCL;

        // 15. Validate Plan
        if (plan.getPlanProperties().getState() == PlanState.PLAN_VALIDATED) {
            minCL = plan.getPlanProperties().getChangeLog().getChanged();
        } else {
            minCL = Long.MAX_VALUE;
        }

        minChangeLogs[15] = minCL;
        maxChangeLogs[15] = minCL;

        // for (int i =0; i <= PlanStatistics.MAX_STATE; i ++) {
        // if (minChangeLogs[i] == Long.MAX_VALUE) {
        // minChangeLogs[i] = 0L;
        // }
        // if (maxChangeLogs[i] == Long.MIN_VALUE) {
        // maxChangeLogs[i] = 0L;
        // }
        // }
        // calculate durations of each step
        for (int stage = 1; stage < PlanStatistics.MAX_STATE; stage++) {
            long enter = minChangeLogs[stage];
            long exit = minChangeLogs[stage + 1];
            if (LUndef.isDefined(enter) && LUndef.isDefined(exit)) {
                // in minutes, not milliseconds!
                long duration = exit - enter;
                statistics.getPhaseDurations()[stage] = duration / 60000;
            } else {
                statistics.getPhaseDurations()[stage] = Long.MAX_VALUE;
            }
        }

        
        minCL = minChangeLogs[PlanState.GO_CHOSEN.getValue()];
        if (LUndef.isDefined(minCL)) {
            statistics.setToDecision( (minCL-createdAt) / 60000 );
        } else {
            statistics.setToDecision( Long.MIN_VALUE );            
        }
        
        minCL = minChangeLogs[PlanState.PLAN_VALIDATED.getValue()];
        if (LUndef.isDefined(minCL)) {
            statistics.setToCompletion((minCL-createdAt) / 60000 );
        } else {
            statistics.setToCompletion( Long.MIN_VALUE );            
        }

        if (maxState != PlanState.PLAN_VALIDATED) {
            for (int i = PlanStatistics.MAX_STATE-1; i >= 1; i--) {
                if (LUndef.isDefined(maxChangeLogs[i]) && (maxChangeLogs[i] > createdAt) && (i > maxState.getValue())) {
                    maxState = PlanState.valueOf(i);
                }
            }
        }
        statistics.setHighestStateAchieved(maxState.getValue());
    }
    
    protected void setupColumns() {
        addColumn("id", new LUndef());
        addColumn("propertyId", new LUndef());
        addColumn("creatorUsername", new NotNull());
        addColumn("creatorName", new NotNull());
        addColumn("creatorEmail", new Optional());
        addColumn("name", new NotNull());
        addColumn("state", new LMinMax(0L, PlanState.PLAN_VALIDATED.getValue()));
        addColumn("highestStateAchieved", new LMinMax(0L, PlanState.PLAN_VALIDATED.getValue()));
        addColumn("createdOn", new FmtDate("yyyy.MM.dd HH:mm:ss"));
        addColumn("decisionOn", new Optional(new FmtDate("yyyy.MM.dd HH:mm:ss")));
        addColumn("toDecision", new LUndef());
        addColumn("toCompletion", new LUndef());
        addColumn("numOfSamples", new LUndef());
        addColumn("numOfLeaves", new LUndef());
        addColumn("numOfMappedLeaves", new LUndef());
        addColumn("numOfMeasurementNeeded", new LUndef());
        addColumn("numOfAlternatives", new LUndef());
        addColumn("percentagePopulatedValues", new DMinMax(0.0, 10.0));
        addColumn("percentageDefinedTransformers", new DMinMax(0.0, 10.0));
        addColumn("numDistinctUsers", new LUndef());
        addColumn("numPlansCreated", new LUndef());
        addColumn("phase1", new LUndef());
        addColumn("phase2", new LUndef());
        addColumn("phase3", new LUndef());
        addColumn("phase4", new LUndef());
        addColumn("phase5", new LUndef());
        addColumn("phase6", new LUndef());
        addColumn("phase7", new LUndef());
        addColumn("phase8", new LUndef());
        addColumn("phase9", new LUndef());
        addColumn("phase10", new LUndef());
        addColumn("phase11", new LUndef());
        addColumn("phase12", new LUndef());
        addColumn("phase13", new LUndef());
        addColumn("phase14", new LUndef());
        addColumn("phase15", new LUndef());

        addColumn("lengthDefineBasis", new LUndef());
        addColumn("lengthDefineSamples", new LUndef());

        addColumn("lengthRequirementsDefinitionDescription", new LUndef());
        addColumn("lengthAlternativesDefinitionDescription", new LUndef());

        addColumn("lengthDecision", new LUndef());
        
        addColumn("lengthEvaluationComment", new LUndef());
        addColumn("lengthImportanceWeightingComment", new LUndef());
        addColumn("lengthRecommendationReasoning", new LUndef());
        addColumn("lengthRecommendationEffects", new LUndef());
        addColumn("lengthPlanDefinitionCostsRemarks", new LUndef());

        addColumn("lengthDocumentTypes", new LUndef());
        addColumn("lengthPropertiesDescription", new LUndef());
        addColumn("lengthBasisMandate", new LUndef());
        addColumn("lengthBasisPlanningPurpose", new LUndef());
        addColumn("lengthBasisDesignatedCommunity", new LUndef());
        addColumn("lengthBasisApplyingPolicies", new LUndef());
        addColumn("lengthBasisOrganisationalProcedures", new LUndef());
        addColumn("lengthBasisPreservationRights", new LUndef());
        addColumn("lengthBasisReferenceToAgreements", new LUndef());
        addColumn("lengthBasisPlanRelations", new LUndef());
        addColumn("lengthTriggersNewCollection", new LUndef());
        addColumn("lengthTriggersPeriodicReview", new LUndef());
        addColumn("lengthTriggersChangedEnvironment", new LUndef());
        addColumn("lengthTriggersChangedObjective", new LUndef());
        addColumn("lengthTriggersChangedCollectionProfile", new LUndef());
        addColumn("lengthCollectionProfileDescription", new LUndef());
        addColumn("lengthCollectionProfileTypeOfObjects", new LUndef());
        addColumn("lengthCollectionProfileExpectedGrowthRate", new LUndef());
        addColumn("lengthCollectionProfileRetentionPeriod", new LUndef());
        addColumn("lengthSamplesDescription", new LUndef());
        addColumn("lengthDecisionReason", new LUndef());
        addColumn("lengthDecisionActionNeeded", new LUndef());
        
        finishColumns();

    }    
}
