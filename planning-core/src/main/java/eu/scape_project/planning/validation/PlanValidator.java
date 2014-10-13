/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.scape_project.planning.validation;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.tree.TreeNode;

/**
 * Validates a plan against a plan state.
 * 
 * @author Michael Kraxner
 */
public class PlanValidator implements Serializable {
    private static final long serialVersionUID = -1592023039267764507L;

    @Inject
    private TreeValidator treeValidator;

    /**
     * Empty constructor.
     */
    public PlanValidator() {
    }

    /**
     * Checks if the plan contains all information required for the given state.
     * 
     * Note: Does not validate the preceding plan states.
     * 
     * @param plan
     *            the plan to validate
     * @param state
     *            the plan state to validate against
     * @param errors
     *            a list of validation errors
     * @return true if the validation succeeded, false otherwise
     */
    public boolean isPlanStateSatisfied(final Plan plan, final PlanState state, List<ValidationError> errors) {
        boolean result = true;

        switch (state) {
            case BASIS_DEFINED:
                result = isBasisDefinedSatisfied(plan, errors);
                break;
            case RECORDS_CHOSEN:
                result = isRecordsChosenSatisfied(plan, errors);
                break;
            case TREE_DEFINED:
                result = isRequirementsDefinedSatisfied(plan, errors);
                break;
            case ALTERNATIVES_DEFINED:
                result = isAlternativesDefinedSatisfied(plan, errors);
                break;
            case GO_CHOSEN:
                result = isDecisionChosenSatisfied(plan, errors);
                break;
            case EXPERIMENT_DEFINED:
                result = isExperimentDefinedSatisfied(plan, errors);
                break;
            case EXPERIMENT_PERFORMED:
                result = isExperimentPerformedSatisfied(plan, errors);
                break;
            case RESULTS_CAPTURED:
                result = isResultsCapturedSatisfied(plan, errors);
                break;
            case TRANSFORMATION_DEFINED:
                result = isTransformationDefinedSatisfied(plan, errors);
                break;
            case WEIGHTS_SET:
                result = isWeightsSetSatisfied(plan, errors);
                break;
            case ANALYSED:
                result = isAnalysedSatisfied(plan, errors);
                break;
            case EXECUTEABLE_PLAN_CREATED:
                result = isExecutablePlanCreatedSatisfied(plan, errors);
                break;
            case PLAN_DEFINED:
                result = isPlanDefinedSatisfied(plan, errors);
                break;
            case PLAN_VALIDATED:
                result = isPlanValidatedSatisfied(plan, errors);
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * Checks if the state {@link PlanState#BASIS_DEFINED} is satisfied.
     * 
     * @param plan
     *            the plan to validate
     * @param errors
     *            a list of errors
     * @return true if the validation succeeded, false otherwise
     */
    private boolean isBasisDefinedSatisfied(final Plan plan, List<ValidationError> errors) {
        return true;
    }

    /**
     * Checks if the state {@link PlanState#RECORDS_CHOSEN} is satisfied.
     * 
     * @param plan
     *            the plan to validate
     * @param errors
     *            a list of errors
     * @return true if the validation succeeded, false otherwise
     */
    private boolean isRecordsChosenSatisfied(final Plan plan, List<ValidationError> errors) {
        boolean result = true;
        // At least one sample must be defined
        if (plan.getSampleRecordsDefinition().getRecords().size() == 0) {
            result = false;
            errors.add(new ValidationError("At least one sample must be added to proceed with the workflow."));
        }
        // Sample names must be unique
        Set<String> names = new HashSet<String>(plan.getSampleRecordsDefinition().getRecords().size());
        for (SampleObject sample : plan.getSampleRecordsDefinition().getRecords()) {
            if (names.contains(sample.getShortName())) {
                result = false;
                errors.add(new ValidationError("There are two samples with the same short name '"
                    + sample.getShortName() + "'. Please provide unique names."));
            }
            names.add(sample.getShortName());
        }

        return result;
    }

    /**
     * Checks if the state {@link PlanState#TREE_DEFINED} is satisfied.
     * 
     * @param plan
     *            the plan to validate
     * @param errors
     *            a list of errors
     * @return true if the validation succeeded, false otherwise
     */
    private boolean isRequirementsDefinedSatisfied(final Plan plan, List<ValidationError> errors) {
        return treeValidator.validate(plan.getTree().getRoot(), new INodeValidator() {
            public boolean validateNode(TreeNode node, List<ValidationError> errors) {
                return node.isCompletelySpecified(errors);
            }
        }, errors);
    }

    /**
     * Checks if the state {@link PlanState#ALTERNATIVES_DEFINED} is satisfied.
     * 
     * @param plan
     *            the plan to validate
     * @param errors
     *            a list of errors
     * @return true if the validation succeeded, false otherwise
     */
    private boolean isAlternativesDefinedSatisfied(final Plan plan, List<ValidationError> errors) {
        // At least one alternative must be defined
        if (plan.getAlternativesDefinition().getAlternatives().size() <= 0) {
            ValidationError error = new ValidationError(
                "At least one alternative must be added to proceed with the workflow.");
            errors.add(error);
            return false;
        }

        return true;
    }

    /**
     * Checks if the state {@link PlanState#GO_CHOSEN} is satisfied.
     * 
     * @param plan
     *            the plan to validate
     * @param errors
     *            a list of errors
     * @return true if the validation succeeded, false otherwise
     */
    private boolean isDecisionChosenSatisfied(final Plan plan, List<ValidationError> errors) {
        boolean result = true;

        if (!plan.getDecision().isGoDecision()) {
            result = false;
            errors.add(new ValidationError("You have to take the GO decision to proceed with the workflow."));
        }
        if (plan.getAlternativesDefinition().getConsideredAlternatives().size() == 0) {
            errors.add(new ValidationError("At least one alternative must be considered for evaluation."));
            result = false;
        }

        return result;
    }

    /**
     * Checks if the state {@link PlanState#EXPERIMENT_DEFINED} is satisfied.
     * 
     * @param plan
     *            the plan to validate
     * @param errors
     *            a list of errors
     * @return true if the validation succeeded, false otherwise
     */
    private boolean isExperimentDefinedSatisfied(final Plan plan, List<ValidationError> errors) {
        return true;
    }

    /**
     * Checks if the state {@link PlanState#EXPERIMENT_PERFORMED} is satisfied.
     * 
     * @param plan
     *            the plan to validate
     * @param errors
     *            a list of errors
     * @return true if the validation succeeded, false otherwise
     */
    private boolean isExperimentPerformedSatisfied(final Plan plan, List<ValidationError> errors) {
        return true;
    }

    /**
     * Checks if the state {@link PlanState#RESULTS_CAPTURED} is satisfied.
     * 
     * @param plan
     *            the plan to validate
     * @param errors
     *            a list of errors
     * @return true if the validation succeeded, false otherwise
     */
    private boolean isResultsCapturedSatisfied(final Plan plan, List<ValidationError> errors) {
        boolean result = treeValidator.validate(plan.getTree().getRoot(), new INodeValidator() {
            private List<Alternative> consideredAlternatives = plan.getAlternativesDefinition()
                .getConsideredAlternatives();

            public boolean validateNode(TreeNode node, List<ValidationError> errors) {
                return node.isCompletelyEvaluated(consideredAlternatives, errors);
            }
        }, errors);

        return result;
    }

    /**
     * Checks if the state {@link PlanState#TRANSFORMATION_DEFINED} is
     * satisfied.
     * 
     * @param plan
     *            the plan to validate
     * @param errors
     *            a list of errors
     * @return true if the validation succeeded, false otherwise
     */
    private boolean isTransformationDefinedSatisfied(final Plan plan, List<ValidationError> errors) {
        return treeValidator.validate(plan.getTree().getRoot(), new INodeValidator() {
            public boolean validateNode(TreeNode node, List<ValidationError> errors) {
                return node.isCompletelyTransformed(errors);
            }
        }, errors);
    }

    /**
     * Checks if the state {@link PlanState#WEIGHTS_SET} is satisfied.
     * 
     * @param plan
     *            the plan to validate
     * @param errors
     *            a list of errors
     * @return true if the validation succeeded, false otherwise
     */
    private boolean isWeightsSetSatisfied(final Plan plan, List<ValidationError> errors) {
        return treeValidator.validate(plan.getTree().getRoot(), new INodeValidator() {
            public boolean validateNode(TreeNode node, List<ValidationError> errors) {
                return node.isCorrectlyWeighted(errors);
            }
        }, errors);
    }

    /**
     * Checks if the state {@link PlanState#ANALYSED} is satisfied.
     * 
     * @param plan
     *            the plan to validate
     * @param errors
     *            a list of errors
     * @return true if the validation succeeded, false otherwise
     */
    private boolean isAnalysedSatisfied(final Plan plan, List<ValidationError> errors) {
        // if no recommendation is set - validation fails
        if (plan.getRecommendation().getAlternative() == null) {
            errors.add(new ValidationError("You have to select a recommendation to proceed with the workflow."));
            return false;
        }

        return true;
    }

    /**
     * Checks if the state {@link PlanState#EXECUTEABLE_PLAN_CREATED} is
     * satisfied.
     * 
     * @param plan
     *            the plan to validate
     * @param errors
     *            a list of errors
     * @return true if the validation succeeded, false otherwise
     */
    private boolean isExecutablePlanCreatedSatisfied(final Plan plan, List<ValidationError> errors) {
        // No validation at this step
        return true;
    }

    /**
     * Checks if the state {@link PlanState#PLAN_DEFINED} is satisfied.
     * 
     * @param plan
     *            the plan to validate
     * @param errors
     *            a list of errors
     * @return true if the validation succeeded, false otherwise
     */
    private boolean isPlanDefinedSatisfied(final Plan plan, List<ValidationError> errors) {
        // No validation at this step
        return true;
    }

    /**
     * Checks if the state {@link PlanState#PLAN_VALIDATED} is satisfied.
     * 
     * @param plan
     *            the plan to validate
     * @param errors
     *            a list of errors
     * @return true if the validation succeeded, false otherwise
     */
    private boolean isPlanValidatedSatisfied(final Plan plan, List<ValidationError> errors) {
        // No validation at this step
        return true;
    }
}
