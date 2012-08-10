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
/**
 * 
 */
package eu.scape_project.planning.plato.wfview.full;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import org.slf4j.Logger;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.manager.ByteStreamManager;
import eu.scape_project.planning.manager.PlanManager;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.SampleRecordsDefinition;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.model.tree.ObjectiveTree;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.DefineSampleObjects;
import eu.scape_project.planning.plato.wfview.AbstractView;
import eu.scape_project.planning.utils.Downloader;
import eu.scape_project.planning.utils.FacesMessages;
import eu.scape_project.planning.utils.FileUtils;
import eu.scape_project.planning.utils.ParserException;

/**
 * @author kraxner
 * 
 */
@Named("defineSampleRecords")
@ConversationScoped
public class DefineSampleRecordsView extends AbstractView {
	private static final long serialVersionUID = 1982741879942387660L;

	@Inject
	private Logger log;

	@Inject
	private User user;

	@Inject
	private DefineSampleObjects defineSamples;

	@Inject
	private ByteStreamManager bytestreamManager;

	@Inject
	private PlanManager planManager;

	@Inject
	private FacesMessages facesMessages;

	@Inject
	private Downloader downloader;

	/**
	 * Currently selected sample object. It determines which object is used for
	 * fits-xml-popup, etc.
	 */
	private SampleObject selectedSampleObject;

	/**
	 * this determines the behaviour of the remove-buttons on the pag (see
	 * there) - to remove sample samples from the list
	 */
	private int allowRemove = -1;

	public DefineSampleRecordsView() {
		currentPlanState = PlanState.BASIS_DEFINED;
		name = "Define Sample Objects";
		viewUrl = "/plan/definesamples.jsf";
		group = "menu.defineRequirements";
		selectedSampleObject = null;
	}

	@Override
	public void init(Plan plan) {
		super.init(plan);
		allowRemove = -1;
	}

	protected boolean needsClearEm() {
		return true;
	}

	public List<SampleObject> getSamples() {
		List<SampleObject> samples = plan.getSampleRecordsDefinition().getRecords();
		if (samples.size() == 0) {
			return null;
		} else {
			return samples;
		}
	}

	public SampleRecordsDefinition getSampleRecordsDefintion() {
		return plan.getSampleRecordsDefinition();
	}

	/**
	 * Uploads a file into a newly created sample sample and adds this sample
	 * sample to the list in the project.
	 * 
	 * @return
	 */
	public void listener(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getUploadedFile();
		String fileName = item.getName();

		try {

			defineSamples.addSample(fileName, item.getContentType(),
			        FileUtils.inputStreamToBytes(item.getInputStream()));

		} catch (Exception e) {
			log.error("failed to add sample object.", e);
			facesMessages.addError("Failed to add sample object.");
		}
		System.gc();
	}

	public void uploadCollectionProfile(FileUploadEvent event) {
		UploadedFile item = event.getUploadedFile();
		String fileName = item.getName();
		log.debug("Collection Profile file [{}] uploaded", fileName);

		if (!fileName.endsWith(".xml")) {
			log.warn("The uploaded file [{}] is not an xml file", fileName);
			facesMessages.addError("The uploaded file is not an xml");
			return;
		}

		try {
			this.defineSamples.readProfile(item.getInputStream());
			
		} catch (ParserException e) {
			log.warn("An error occurred during parsing: {}", e.getMessage());
			this.facesMessages.addError("An error occurred, while reading in the uploaded profile: " + e.getMessage());
		} catch (PlanningException e) {
			log.warn("An error occurred furing parsing: {}", e.getMessage());
			this.facesMessages.addError("An error occurred, while reading in the uploaded profile: " + e.getMessage());
		} catch (IOException e) {
			log.warn("An error occurred while opening the input stream: {}", e.getMessage());
			this.facesMessages.addError("An error occurred, while reading the file. Please try again");
		}
	}

	/**
	 * Adds a new sample to the list of sample samples in the project. This is a
	 * sample sample without data.
	 */
	public String newSample() {
		SampleObject newSample = new SampleObject();

		plan.getSampleRecordsDefinition().addRecord(newSample);
		// this SampleRecordsDefinition has been changed
		plan.getSampleRecordsDefinition().touch();

		return null;
	}

	/**
	 * Removes a sample from the list of samplerecords in the project AND also
	 * removes all associated:
	 * <ul>
	 * <li>evaluation values contained in the tree</li>
	 * <li>experiment results and their xcdl-files</li>
	 * </ul>
	 * - if there are any.
	 */
	public String removeSample(SampleObject sample) {
		log.info("Removing SampleObject from Plan: " + sample.getFullname());
		defineSamples.removeSample(sample);
		allowRemove = -1;
		return null;
	}

	public void characteriseFits(SampleObject object) {
		defineSamples.characteriseFits(object);
	}

	/**
	 * Method responsible for setting the selected SampleObject.
	 * 
	 * @param sampleObj
	 *            Sample object to select.
	 */
	public void selectSampleObject(SampleObject sampleObj) {
		this.selectedSampleObject = sampleObj;
		log.debug("Selected sample object " + sampleObj.getFullname());
	}

	/**
	 * checks if the sample contains evaluation values. If yes, the user should
	 * be asked for confirmation before removing it. If not, the sample is
	 * removed. *
	 * 
	 * @see ObjectiveTree#hasValues(int[],Alternative)
	 * 
	 * @return always returns null
	 */
	public String askRemoveSample(SampleObject sample) {
		if (defineSamples.hasDependetValues(sample)) {
			allowRemove = sample.getId();
		} else {
			removeSample(sample);
		}

		return null;
	}

	public int getAllowRemove() {
		return allowRemove;
	}

	/**
	 * Starts a download for the given digital object. Uses
	 * {@link eu.scape_project.planning.util.Downloader} to perform the
	 * download.
	 */
	public void download(DigitalObject object) {
		File file = bytestreamManager.getTempFile(object.getPid());
		downloader.download(object, file);
	}

	@Override
	protected AbstractWorkflowStep getWfStep() {
		return defineSamples;
	}

	public SampleObject getSelectedSampleObject() {
		return selectedSampleObject;
	}

	public void setSelectedSampleObject(SampleObject selectedSampleObject) {
		this.selectedSampleObject = selectedSampleObject;
	}

}
