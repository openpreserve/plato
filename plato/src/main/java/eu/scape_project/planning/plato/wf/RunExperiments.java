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
package eu.scape_project.planning.plato.wf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import eu.scape_project.planning.manager.StorageException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.AlternativesDefinition;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Experiment;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.plato.ExperimentRunner;
import eu.scape_project.planning.plato.bean.ExperimentStatus;

/**
 * Business logic for workflow step Run Experiments.
 * 
 * @author Markus Hamm, Michael Kraxner
 */
@Stateful
@ConversationScoped
public class RunExperiments extends AbstractWorkflowStep {
    private static final long serialVersionUID = -31490789968518812L;

    @Inject
    private Logger log;

    @Inject
    private ExperimentRunner experimentRunner;

    private ExperimentStatus experimentStatus = new ExperimentStatus();

    /**
     * Creates a new run experiments step.
     */
    public RunExperiments() {
        requiredPlanState = PlanState.EXPERIMENT_DEFINED;
        correspondingPlanState = PlanState.EXPERIMENT_PERFORMED;
    }

    @Override
    public void init(Plan p) {
        super.init(p);

        // add empty result files where missing (only for considered
        // alternatives!)
        List<SampleObject> allRecords = p.getSampleRecordsDefinition().getRecords();
        for (Alternative alternative : p.getAlternativesDefinition().getConsideredAlternatives()) {
            Experiment exp = alternative.getExperiment();

            for (SampleObject record : allRecords) {
                DigitalObject u = exp.getResults().get(record);

                if (u == null) {
                    exp.addRecord(record);
                    u = exp.getResults().get(record);
                }
            }
        }

    }

    /**
     * Sets up the experiment for a single alternative.
     * 
     * @param alternative
     *            the alternative to set up
     * @return the current experiment status
     */
    public ExperimentStatus setupExperiment(Alternative alternative) {
        experimentStatus.experimentSetup(Arrays.asList(alternative), plan.getSampleRecordsDefinition().getRecords());
        return experimentStatus;
    }

    /**
     * Sets up the experiment for all selected, runnable alternatives.
     * 
     * @return the current experiment status
     */
    public ExperimentStatus setupAllExperiments() {
        List<Alternative> runnableAlternatives = getRunnableAlternatives();
        experimentStatus.experimentSetup(runnableAlternatives, plan.getSampleRecordsDefinition().getRecords());
        return experimentStatus;
    }

    /**
     * Returns all runnable alternatives.
     * 
     * @return the runnable alternatives
     */
    private List<Alternative> getRunnableAlternatives() {
        List<Alternative> runnableAlternatives = new ArrayList<Alternative>();
        for (Alternative a : plan.getAlternativesDefinition().getAlternatives()) {
            if (!a.isDiscarded() && a.isExecutable()) {
                runnableAlternatives.add(a);
            }
        }
        return runnableAlternatives;
    }

    /**
     * Runs all experiments scheduled in experimentStatus.
     */
    public void startExperiments() {
        experimentRunner.startExperiments(plan, experimentStatus);
        log.info("Started experiments... ");
    }

    /**
     * Method responsible for uploading a result file.
     * 
     * @param resultFile
     *            File to upload.
     * @param alternative
     *            Alternative the file was uploaded for.
     * @param sampleObject
     *            Sample the file was uploaded for.
     * @throws StorageException
     *             is thrown if any error occurs at uploading the result file.
     */
    public void uploadResultFile(DigitalObject resultFile, Alternative alternative, SampleObject sampleObject)
        throws StorageException {
        digitalObjectManager.moveDataToStorage(resultFile);
        addedBytestreams.add(resultFile.getPid());

        characteriseFits(resultFile);

        alternative.getExperiment().getResults().put(sampleObject, resultFile);
    }

    /**
     * Removes a previously uploaded result file.
     * 
     * @param alternative
     *            Alternative the file was uploaded for.
     * @param sampleObject
     *            Sample the file was uploaded for.
     */
    public void removeResultFile(Alternative alternative, SampleObject sampleObject) {
        DigitalObject resultFile = alternative.getExperiment().getResults().put(sampleObject, new DigitalObject());
        bytestreamsToRemove.add(resultFile.getPid());

        alternative.getExperiment().getResults().put(sampleObject, new DigitalObject());
    }

    /**
     * Characterises results for all alternatives.
     */
    public void characteriseResults() {
        List<Alternative> runnableAlternatives = getRunnableAlternatives();
        List<SampleObject> allRecords = plan.getSampleRecordsDefinition().getRecords();
        for (Alternative alternative : runnableAlternatives) {
            Experiment exp = alternative.getExperiment();
            
            for (SampleObject record : allRecords) {
                DigitalObject u = exp.getResults().get(record);
                
                if (u.isDataExistent() && (u.getFitsXMLString() == null)) {
                    characteriseFits(u);
                }
            }
        }

    }

    @Override
    protected void saveStepSpecific() {
        prepareChangesForPersist.prepare(plan);
        characteriseResults();

        // init tree values for all considered alternatives
        plan.getTree().initValues(plan.getAlternativesDefinition().getConsideredAlternatives(),
            plan.getSampleRecordsDefinition().getRecords().size());

        plan.setAlternativesDefinition((AlternativesDefinition) saveEntity(plan.getAlternativesDefinition()));
        saveEntity(plan.getTree());
    }
}
