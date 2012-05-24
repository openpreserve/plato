package eu.scape_project.pw.planning.plato.wfview.full;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import org.slf4j.Logger;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DetailedExperimentInfo;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.pw.planning.manager.StorageException;
import eu.scape_project.pw.planning.plato.bean.ExperimentStatus;
import eu.scape_project.pw.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.pw.planning.plato.wf.RunExperiments;
import eu.scape_project.pw.planning.plato.wfview.AbstractView;
import eu.scape_project.pw.planning.utils.Downloader;

@Named("runExperiments")
@ConversationScoped
public class RunExperimentsView extends AbstractView {
	private static final long serialVersionUID = 1L;
	
	@Inject	private Logger log;
	
	@Inject private RunExperiments runExperiments;

	@Inject private Downloader downloader;

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
	
	public RunExperimentsView() {
    	currentPlanState = PlanState.EXPERIMENT_DEFINED;
    	name = "Run Experiments";
    	viewUrl = "/plan/runexperiments.jsf";
    	group = "menu.evaluateAlternatives";
	}
	
	/**
	 * Method responsible for initializing parameters used in this view 
	 */
	public void init(Plan plan) {
    	super.init(plan);

    	// TODO: Maybe it is better to have this method in some previous BL-step (after Alternatives and Sample definition)?
    	// 		 Here it seems a little bit too symptomatic to me.
    	plan.initializeExperimentInfos();
    	
    	hasExecutableExperiments = false;
    	for (Alternative alt : plan.getAlternativesDefinition().getAlternatives()) {
    		if (alt.isExecutable()) {
    			hasExecutableExperiments = true;
    		}
    	}
	}
	
	/**
	 * Seth
	 * @param alternative The alternative to run the experiment for.
	 */
	public void setupExperiment(Alternative alternative) {
		experimentStatus = runExperiments.setupExperiment(alternative);
	}
	
	/**
	 * Method responsible for running all executable experiments
	 */
	public void setupAllExperiments() {
		experimentStatus = runExperiments.setupAllExperiments();
	}
	
	public void startExperiments(){
		experimentStatus.setStarted(true);
		runExperiments.startExperiments();
		log.error("Experiment started...");
	}
	
	public ExperimentStatus getExperimentStatus(){
		return experimentStatus;
	}
	
	/**
	 * Method responsible for updating the selected experiment info based on user interaction.
	 * 
	 * @param alternative Experiment alternative.
	 * @param sampleObject Experiment sample object.
	 */
	public void updateSelectedDetailedExperimentInfo(Object alt, Object sampleObj) {
		Alternative alternative = (Alternative) alt;
		SampleObject sampleObject = (SampleObject) sampleObj;
		
		selectedDetailedExperimentInfo = alternative.getExperiment().getDetailedInfo().get(sampleObject);
	}
	
	/**
	 * Method responsible for setting the appropriate Alternative and SampleObject for the next file-upload
	 * 
	 * @param alternative Alternative corresponding to the next file-upload.
	 * @param sampleObject SampleObject corresponding to the next file-upload.
	 */
	public void updateDataForNextUpload(Object alt, Object sampleObj) {
		Alternative alternative = (Alternative) alt;
		SampleObject sampleObject = (SampleObject) sampleObj;
		
		alternativeForNextUpload = alternative;
		sampleObjectForNextUpload = sampleObject;
	}

	/**
	 * Method responsible for uploading result files.
	 * 
	 * @param event Richfaces FileUploadEvent class.
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
			facesMessages.addError("Unable to upload result-file for alternative");
		}
	}
	
	/**
	 * Method responsible for starting the download of a given result file
	 * 
	 * @param alt Alternative of the wanted result file.
	 * @param sampleObj SampleObject of the wanted result file.
	 */
	public void downloadResultFile(Object alt, Object sampleObj) {
		Alternative alternative = (Alternative) alt;
		SampleObject sampleObject = (SampleObject) sampleObj;
		
		DigitalObject resultFile = null;
		
		try {
			resultFile = runExperiments.fetchResultFile(alternative, sampleObject);
		}
		catch (StorageException e) {
			log.error("Exception at trying to fetch result file for alternative " + alternative.getName() + "and sample " + sampleObject.getFullname(), e);
			facesMessages.addError("Unable to fetch result-file");
		}
		
		if (resultFile != null) {
			downloader.download(resultFile);
			return;
		}
		else {
			log.debug("No result file exists for alternative " + alternative.getName() + " and sample " + sampleObject.getFullname());
		}
	}
	
	/**
	 * Removes a previously uploaded result file.
	 * 
	 * @param alternative Alternative the file was uploaded for.
	 * @param sampleObject Sample the file was uploaded for.
	 */
	public void removeResultFile(Object alt, Object sampleObj) {
		Alternative alternative = (Alternative) alt;
		SampleObject sampleObject = (SampleObject) sampleObj;
		
		runExperiments.removeResultFile(alternative, sampleObject);
	}
	
	@Override
	protected AbstractWorkflowStep getWfStep() {
		return runExperiments;
	}
	
	// --------------- getter/setter ---------------

	public DetailedExperimentInfo getSelectedDetailedExperimentInfo() {
		return selectedDetailedExperimentInfo;
	}

	public void setSelectedDetailedExperimentInfo(
			DetailedExperimentInfo selectedDetailedExperimentInfo) {
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
