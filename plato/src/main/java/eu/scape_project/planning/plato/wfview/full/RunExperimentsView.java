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
package eu.scape_project.planning.plato.wfview.full;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import org.slf4j.Logger;

import eu.scape_project.planning.manager.StorageException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DetailedExperimentInfo;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.plato.bean.ExperimentStatus;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.RunExperiments;
import eu.scape_project.planning.plato.wfview.AbstractView;
import eu.scape_project.planning.utils.Downloader;

/**
 * Bean for the viewWorkflow step 'Run Experiments'.
 */
@Named("runExperiments")
@ConversationScoped
public class RunExperimentsView extends AbstractView {
    private static final long serialVersionUID = 1L;

    @Inject
    private Logger log;

    @Inject
    private RunExperiments runExperiments;

    @Inject
    private Downloader downloader;

    /**
     * DetailedExperimentInfo selected in the UI for editing purposes.
     */
    private DetailedExperimentInfo selectedDetailedExperimentInfo;

    private boolean hasExecutableExperiments;

    /**
     * Alternative for the next upload.
     */
    private Alternative alternativeForNextUpload;

    /**
     * Sample Object for the next upload.
     */
    private SampleObject sampleObjectForNextUpload;

    private ExperimentStatus experimentStatus = new ExperimentStatus();

    /**
     * Construct a new object.
     */
    public RunExperimentsView() {
        currentPlanState = PlanState.EXPERIMENT_DEFINED;
        name = "Run Experiments";
        viewUrl = "/plan/runexperiments.jsf";
        group = "menu.evaluateAlternatives";
    }

    @Override
    public void init(Plan plan) {
        super.init(plan);

        // TODO: Maybe it is better to have this method in some previous BL-step
        // (after Alternatives and Sample definition)?
        // Here it seems a little bit too symptomatic to me.
        plan.initializeExperimentInfos();

        hasExecutableExperiments = false;
        for (Alternative alt : plan.getAlternativesDefinition().getAlternatives()) {
            if (alt.isExecutable()) {
                hasExecutableExperiments = true;
            }
        }
    }

    @Override
    protected AbstractWorkflowStep getWfStep() {
        return runExperiments;
    }

    /**
     * Sets up experiments for the provided {@code alternative} for later
     * execution.
     * 
     * @param alternative
     *            The alternative to run the experiment for.
     * 
     * @see #startExperiments()
     */
    public void setupExperiment(Alternative alternative) {
        experimentStatus = runExperiments.setupExperiment(alternative);
    }

    /**
     * Set up experiments for all alternatives for later execution.
     * 
     * @see #startExperiments()
     */
    public void setupAllExperiments() {
        experimentStatus = runExperiments.setupAllExperiments();
    }

    /**
     * Starts set up experiments.
     * 
     * @see #setupExperiment(Alternative)
     * @see #setupAllExperiments()
     */
    public void startExperiments() {
        experimentStatus.setStarted(true);
        runExperiments.startExperiments();
        log.info("Experiment started...");
    }

    public ExperimentStatus getExperimentStatus() {
        return experimentStatus;
    }

    /**
     * Method responsible for updating the selected experiment info based on
     * user interaction.
     * 
     * @param alt
     *            Experiment alternative.
     * @param sampleObj
     *            Experiment sample object.
     */
    public void updateSelectedDetailedExperimentInfo(Object alt, Object sampleObj) {
        Alternative alternative = (Alternative) alt;
        SampleObject sampleObject = (SampleObject) sampleObj;

        selectedDetailedExperimentInfo = alternative.getExperiment().getDetailedInfo().get(sampleObject);
    }

    /**
     * Method responsible for setting the appropriate Alternative and
     * SampleObject for the next file-upload.
     * 
     * @param alt
     *            Alternative corresponding to the next file-upload.
     * @param sampleObj
     *            SampleObject corresponding to the next file-upload.
     */
    public void updateDataForNextUpload(Object alt, Object sampleObj) {
        Alternative alternative = (Alternative) alt;
        SampleObject sampleObject = (SampleObject) sampleObj;

        alternativeForNextUpload = alternative;
        sampleObjectForNextUpload = sampleObject;
    }

    /**
     * Method responsible for uploading result files for the set up alternative
     * and sample object.
     * 
     * @param event
     *            Richfaces FileUploadEvent class.
     * @see #updateDataForNextUpload(Object, Object)
     */
    public void uploadResultFile(FileUploadEvent event) {
        UploadedFile file = event.getUploadedFile();

        // Put file-data into a digital object
        DigitalObject digitalObject = new DigitalObject();
        digitalObject.setFullname(file.getName());
        digitalObject.getData().setData(file.getData());
        digitalObject.setContentType(file.getContentType());

        try {
            runExperiments.uploadResultFile(digitalObject, alternativeForNextUpload, sampleObjectForNextUpload);
        } catch (StorageException e) {
            log.error("Exception at trying to upload result file.", e);
            facesMessages.addError("Unable to upload result file for alternative. Please try again.");
        }
    }

    /**
     * Method responsible for starting the download of a given result file.
     * 
     * @param alt
     *            Alternative of the wanted result file.
     * @param sampleObj
     *            SampleObject of the wanted result file.
     */
    public void downloadResultFile(Object alt, Object sampleObj) {
        Alternative alternative = (Alternative) alt;
        SampleObject sampleObject = (SampleObject) sampleObj;

        DigitalObject resultFile = null;

        try {
            resultFile = runExperiments.fetchResultFile(alternative, sampleObject);
        } catch (StorageException e) {
            log.error("Exception at trying to fetch result file for alternative " + alternative.getName()
                + "and sample " + sampleObject.getFullname(), e);
            facesMessages.addError("Unable to fetch result file. Please try again.");
        }

        if (resultFile != null) {
            downloader.download(resultFile);
        } else {
            log.debug("No result file exists for alternative " + alternative.getName() + " and sample "
                + sampleObject.getFullname() + ".");
        }
    }

    /**
     * Removes a previously uploaded result file.
     * 
     * @param alt
     *            Alternative the file was uploaded for.
     * @param sampleObj
     *            Sample the file was uploaded for.
     */
    public void removeResultFile(Object alt, Object sampleObj) {
        Alternative alternative = (Alternative) alt;
        SampleObject sampleObject = (SampleObject) sampleObj;

        runExperiments.removeResultFile(alternative, sampleObject);
    }

    // --------------- getter/setter ---------------

    public DetailedExperimentInfo getSelectedDetailedExperimentInfo() {
        return selectedDetailedExperimentInfo;
    }

    public void setSelectedDetailedExperimentInfo(DetailedExperimentInfo selectedDetailedExperimentInfo) {
        this.selectedDetailedExperimentInfo = selectedDetailedExperimentInfo;
    }

    public boolean isHasExecutableExperiments() {
        return hasExecutableExperiments;
    }

    public void setHasExecutableExperiments(boolean hasExecutableExperiments) {
        this.hasExecutableExperiments = hasExecutableExperiments;
    }

    public Alternative getAlternativeForNextUpload() {
        return alternativeForNextUpload;
    }

    public void setAlternativeForNextUpload(Alternative alternativeForNextUpload) {
        this.alternativeForNextUpload = alternativeForNextUpload;
    }

    public SampleObject getSampleObjectForNextUpload() {
        return sampleObjectForNextUpload;
    }

    public void setSampleObjectForNextUpload(SampleObject sampleObjectForNextUpload) {
        this.sampleObjectForNextUpload = sampleObjectForNextUpload;
    }
}
