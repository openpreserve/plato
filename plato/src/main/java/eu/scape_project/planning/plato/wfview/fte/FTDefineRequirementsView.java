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
package eu.scape_project.planning.plato.wfview.fte;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import org.slf4j.Logger;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.manager.StorageException;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.tree.TreeNode;
import eu.scape_project.planning.plato.bean.TreeHelperBean;
import eu.scape_project.planning.plato.fte.FTDefineRequirements;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.beans.FastTrackTemplate;
import eu.scape_project.planning.plato.wfview.AbstractView;
import eu.scape_project.planning.utils.CharacterisationReportGenerator;
import eu.scape_project.planning.utils.Downloader;

@Named("defineRequirementsFTE")
@ConversationScoped
public class FTDefineRequirementsView extends AbstractView {
    private static final long serialVersionUID = 1L;

    @Inject
    private FTDefineRequirements ftDefineRequirements;

    @Inject
    private Downloader downloader;

    @Inject
    private Logger log;

    @Inject
    private TreeHelperBean treeHelper;

    private List<FastTrackTemplate> ftTemplates;

    private FastTrackTemplate selectedFTTemplate;

    /**
     * Contains the ObjectiveTree-Root in a list. 
     * This is necessary, because <rich:treeModelRecursiveAdaptor> root variable requires
     * a list to work properly.
     */
    private List<TreeNode> treeRoots;
    
    private String sampleCharacterisationReportAsHTML;
    

    public FTDefineRequirementsView() {
        currentPlanState = PlanState.CREATED;
        name = "Define Requirements";
        viewUrl = "/fte/FTdefinerequirements.jsf";
        ftTemplates = new ArrayList<FastTrackTemplate>();
        selectedFTTemplate = null;
        treeRoots = new ArrayList<TreeNode>();
    }

    public void init(Plan p) {
        super.init(p);
        ftTemplates = ftDefineRequirements.getAvailableFTTemplates();
        treeRoots.clear();
        treeRoots.add(plan.getTree().getRoot());

        // all leaves are shown, unless the users decided to change this.
        if (treeHelper.getExpandedNodes().isEmpty()) {
            treeHelper.expandAll(plan.getTree().getRoot());
        }
    }

    /**
     * Method responsible for fetching all plan sample objects
     * 
     * @return List of all plan sample objects.
     */
    public List<SampleObject> getSamples() {
        List<SampleObject> samples = plan.getSampleRecordsDefinition().getRecords();
        if (samples.size() == 0) {
            return null;
        } else {
            return samples;
        }
    }

    /**
     * Method responsible for removing a sample record.
     */
    public void removeSample(SampleObject sample) {
        ftDefineRequirements.removeSample(sample);
    }

    /**
     * Starts a download for the given digital object. Uses
     * {@link eu.scape_project.planning.util.Downloader} to perform the
     * download.
     */
    public void download(DigitalObject object) {
        try {
            downloader.download(ftDefineRequirements.fetchDigitalObject(object));
        } catch (StorageException e) {
            facesMessages.addError("Error at downloading file");
            log.error("FAiled to download file.", e);
        }
    }

    /**
     * Method responsible for uploading/attaching a file.
     * 
     * @param event
     *            Richfaces FileUploadEvent data.
     */
    public void uploadFile(FileUploadEvent event) {
        UploadedFile file = event.getUploadedFile();

        // Put file-data into a digital object
        DigitalObject digitalObject = new DigitalObject();
        digitalObject.setFullname(file.getName());
        digitalObject.getData().setData(file.getData());
        digitalObject.setContentType(file.getContentType());

        try {
            ftDefineRequirements.addSample(digitalObject);
        } catch (PlanningException e) {
            log.error("Failed to upload a file", e);
            facesMessages.addError("Failed to upload file.");
        }
    }

    /**
     * Method responsible for applying the currently selected fast-track
     * template (which is {@link selectedFTTemplate}).
     */
    public void useSelectedFastTrackTemplate() {
        try {
            ftDefineRequirements.useFastTrackTemplate(selectedFTTemplate);
            // reset the tree-root for the view - it has changed
            treeRoots.clear();
            treeRoots.add(plan.getTree().getRoot());
        } catch (PlanningException e) {
            log.error(e.getMessage(), e);
            facesMessages.addError(e.getMessage());
        }
    }

    @Override
    protected AbstractWorkflowStep getWfStep() {
        return ftDefineRequirements;
    }

    /**
     * Generates characterisation report for selected sample object
     * 
     * @param sampleObj
     *            Sample object to select.
     */
    public void generateCharacterisationReport(final SampleObject sampleObj) {
        CharacterisationReportGenerator reportGen = new CharacterisationReportGenerator();
        this.sampleCharacterisationReportAsHTML = reportGen.generateHTMLReport(sampleObj);
    }
    
    // --------------- getter/setter ---------------

    public List<FastTrackTemplate> getFtTemplates() {
        return ftTemplates;
    }

    public void setFtTemplates(List<FastTrackTemplate> ftTemplates) {
        this.ftTemplates = ftTemplates;
    }

    public FastTrackTemplate getSelectedFTTemplate() {
        return selectedFTTemplate;
    }

    public void setSelectedFTTemplate(FastTrackTemplate selectedFTTemplate) {
        this.selectedFTTemplate = selectedFTTemplate;
    }

    public List<TreeNode> getTreeRoots() {
        return treeRoots;
    }

    public void setTreeRoots(List<TreeNode> treeRoots) {
        this.treeRoots = treeRoots;
    }

    public TreeHelperBean getTreeHelper() {
        return treeHelper;
    }

    public String getSampleCharacterisationReportAsHTML() {
        return sampleCharacterisationReportAsHTML;
    }
}
