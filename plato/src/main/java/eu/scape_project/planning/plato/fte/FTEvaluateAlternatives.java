/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
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
package eu.scape_project.planning.plato.fte;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.EvaluationStatus;
import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.model.PreservationActionDefinition;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.plato.bean.ExperimentStatus;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.DefineAlternatives;
import eu.scape_project.planning.plato.wf.EvaluateExperiments;
import eu.scape_project.planning.plato.wf.RunExperiments;
import eu.scape_project.planning.services.IServiceInfo;
import eu.scape_project.planning.services.PlanningServiceException;
import eu.scape_project.planning.services.pa.PreservationActionRegistryDefinition;
import eu.scape_project.planning.validation.ValidationError;

import org.slf4j.Logger;

@Stateful
@ConversationScoped
public class FTEvaluateAlternatives extends AbstractWorkflowStep {

    private static final long serialVersionUID = 1L;

    @Inject
    private Logger log;

    @Inject
    private DefineAlternatives defineAlternatives;
    @Inject
    private RunExperiments runExperiments;
    @Inject
    private EvaluateExperiments evaluateExperiments;

    public FTEvaluateAlternatives() {
        this.requiredPlanState = PlanState.TREE_DEFINED;
        this.correspondingPlanState = PlanState.RESULTS_CAPTURED;
    }

    @Override
    public void init(Plan p) {
        super.init(p);

        defineAlternatives.init(p);

        // we only use automated services in FTE.
        // If we don't have any services, we query the list.
        // Open issue - if you want to add one service, at the moment you would
        // have to remove all and then enter again.
        // Not so terribly bad for now.
        if (plan.getAlternativesDefinition().getAlternatives().size() == 0) {
            addServices();
        }
        //
        // We have to make sure that the value map of our leaves is properly
        // initialized. If not, we have to
        // call initValues to enable evaluation results to be stored for the
        // requirements. In the 'normal'
        // workflow initValues is called in RunExperimentsAction.save, so from
        // there on everything is in order.
        // In FTE we can't do that
        //
        boolean valueMapProperlyInitialized = plan
            .getTree()
            .getRoot()
            .isValueMapProperlyInitialized(plan.getAlternativesDefinition().getConsideredAlternatives(),
                plan.getSampleRecordsDefinition().getRecords().size());

        if (!valueMapProperlyInitialized) {
            plan.getTree().initValues(plan.getAlternativesDefinition().getConsideredAlternatives(),
                plan.getSampleRecordsDefinition().getRecords().size());
        }

        runExperiments.init(p);
        evaluateExperiments.init(p);
    }

    @Override
    protected void saveStepSpecific() {
        defineAlternatives.save();
        runExperiments.save();
        evaluateExperiments.save();

        // super.saveEntity(plan);
    }

    @Override
    protected boolean mayProceed(List<ValidationError> errors) {
        // First we have to check if the experiments have been conducted. This
        // is inevitable, otherwise validator.validate would
        // cause a NullPointerException because leaf.valueMap is empty.
        // Rest of the validation is the same as in {@link
        // EvaluateExperimentsAction.validate}
        // Since validate is protected we cannot call
        // IEvaluateExperiments.validate.

        EvaluationStatus evaluationStatus = plan.getTree().getRoot().getEvaluationStatus();

        if (evaluationStatus != EvaluationStatus.COMPLETE) {
            errors.add(new ValidationError("Experiments have not been conducted.", evaluationStatus));
            return false;
        }
        boolean result = true;
        if (!planValidator.isPlanStateSatisfied(plan, PlanState.ALTERNATIVES_DEFINED, errors)) {
            result = false;
        }
        if (!planValidator.isPlanStateSatisfied(plan, PlanState.EXPERIMENT_DEFINED, errors)) {
            result = false;
        }
        if (!planValidator.isPlanStateSatisfied(plan, PlanState.EXPERIMENT_PERFORMED, errors)) {
            result = false;
        }
        if (!planValidator.isPlanStateSatisfied(plan, PlanState.RESULTS_CAPTURED, errors)) {
            result = false;
        }

        return result;
    }

    /**
     * Removes the given alternative from the plan's list of alternatives - also
     * takes care of values of this alternative
     * 
     * @param alternative
     */
    public void removeAlternative(Alternative alternative) {
        plan.getAlternativesDefinition().removeAlternative(alternative);
        plan.getAlternativesDefinition().touch();
        // important: we also have to remove the values which might have been
        // added here...
        plan.getTree().removeValues(alternative);
    }

    /**
     * Constructs a list of automated services TODO
     */
    private void addServices() {

        List<PreservationActionRegistryDefinition> allRegistries;
        try {
            allRegistries = defineAlternatives.getPreservationActionRegistries();
        } catch (PlanningServiceException e1) {
            log.error("failed to retrieve registries", e1);
            return;
        }

        // get first sample with data
        SampleObject sample = plan.getSampleRecordsDefinition().getFirstSampleWithFormat();
        if (sample == null) {
            return;
        }
        FormatInfo formatInfo = sample.getFormatInfo();

        for (PreservationActionRegistryDefinition reg : allRegistries) {
            try {
                if (reg.getShortname().contains("MiniMEE")) {
                    List<IServiceInfo> actions = defineAlternatives.queryRegistry(formatInfo, reg);
                    /*
                     * populate the list of available services TODO what about
                     * adding planets and filtering services according to
                     * "sensible" target formats (e.g. images:
                     * png,tiff,jp2,jpg,dng) ?
                     */
                    for (IServiceInfo actionInfo : actions) {
                        PreservationActionDefinition actionDefinition = new PreservationActionDefinition();
                        actionDefinition.setActionIdentifier(actionInfo.getServiceIdentifier());
                        actionDefinition.setShortname(actionInfo.getShortname());
                        actionDefinition.setDescriptor(actionInfo.getDescriptor());
                        actionDefinition.setUrl(actionInfo.getUrl());
                        actionDefinition.setInfo(actionInfo.getInfo());

                        Alternative a = Alternative.createAlternative(plan.getAlternativesDefinition()
                            .createUniqueName(actionDefinition.getShortname()), actionDefinition);
                        // and add it to the preservation planning project
                        plan.getAlternativesDefinition().addAlternative(a);
                    }
                }
            } catch (PlatoException e) {
                log.error("failed to query registry: " + reg.getShortname(), e);
            } catch (PlanningException e) {
                log.error("failed to add alternative.", e);
            }
        }
    }

    public ExperimentStatus setupAllExperiments() {
        return runExperiments.setupAllExperiments();
    }

    public void startExperiments() {
        runExperiments.startExperiments();
    }

    public void clearExperiments() {
        plan.getTree().initValues(plan.getAlternativesDefinition().getConsideredAlternatives(),
            plan.getSampleRecordsDefinition().getRecords().size());
    }

    public boolean isAutoEvaluationAvailable() {
        return evaluateExperiments.isAutoEvaluationAvailable();
    }

    public void evaluateAll() throws PlanningException {
        runExperiments.characteriseResults();
        evaluateExperiments.evaluateLeaves(plan.getTree().getRoot().getAllLeaves());
    }

    public void approveAllValues() {
        evaluateExperiments.approveAllValues();
    }
}
