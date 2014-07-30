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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.manager.StorageException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.tree.Node;
import eu.scape_project.planning.model.tree.TreeNode;
import eu.scape_project.planning.plato.bean.TreeHelperBean;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.EvaluateExperiments;
import eu.scape_project.planning.plato.wfview.AbstractView;
import eu.scape_project.planning.utils.CharacterisationReportGenerator;
import eu.scape_project.planning.utils.Downloader;
import eu.scape_project.planning.utils.FacesMessages;
import eu.scape_project.planning.validation.ValidationError;

/**
 * Class used as backing bean for view step Evaluate Experiments. i.e. enable
 * the user to enter the evaluation result per alternative and sample object.
 * The user has to enter evaluation result for all leaves in the objective tree.
 * 
 * @author Michael Kraxner, Markus Hamm
 */
@Named("evalexperiments")
@ConversationScoped
public class EvaluateExperimentsView extends AbstractView {
    private static final long serialVersionUID = 2879369643201412418L;

    @Inject
    private Logger log;

    @Inject
    private EvaluateExperiments evaluateExperiments;

    @Inject
    private FacesMessages facesMessages;

    @Inject
    private Downloader downloader;

    @Inject
    private TreeHelperBean treeHelper;

    /**
     * List of all leaves for which the evaluation settings shall be displayed
     * to the user.
     */
    private List<Leaf> leaves;

    /**
     * This is a pseudo list which only contains the tree's root node.
     */
    private List<TreeNode> treeRoot;

    /**
     * We want to display the measurableProperties of the plan as these are
     * aggregated from the requirements tree, we have to cache them.
     */
    private List<Measure> measures;

    private boolean autoEvaluationAvailable;

    private String sampleCharacterisationReportAsHTML;
    private String resultCharacterisationReportAsHTML;

    /**
     * Constructs a new object.
     */
    public EvaluateExperimentsView() {
        currentPlanState = PlanState.EXPERIMENT_PERFORMED;
        name = "Evaluate Experiments";
        viewUrl = "/plan/evalexperiments.jsf";
        group = "menu.evaluateAlternatives";

        leaves = new ArrayList<Leaf>();
        measures = new ArrayList<Measure>();
    }

    @Override
    public void init(Plan plan) {
        super.init(plan);

        initLeafLists();

        autoEvaluationAvailable = evaluateExperiments.isAutoEvaluationAvailable();
        refreshMeasures();

        treeRoot = new ArrayList<TreeNode>();
        treeRoot.add(plan.getTree().getRoot());

        treeHelper.resetAllNodes();
    }

    @Override
    public boolean tryProceed(List<ValidationError> errors) {
        if (!super.tryProceed(errors)) {
            // this is from the legacy code - why do we clear this list only,
            // when there are errors?
            leaves.clear();
            for (ValidationError error : errors) {
                if (error.getInvalidObject() instanceof Leaf) {
                    Leaf leaf = (Leaf) error.getInvalidObject();
                    leaves.add(leaf);
                    treeHelper.expandNode(leaf);
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Evaluates all leaves.
     */
    public void evaluateAll() {
        try {
            evaluateExperiments.evaluateLeaves(plan.getTree().getRoot().getAllLeaves());
        } catch (PlanningException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Evaluates the provided {@code leaf}.
     * 
     * @param leaf
     *            the leaf to evaluate
     */
    public void evaluate(Leaf leaf) {
        try {
            evaluateExperiments.evaluateLeaves(Arrays.asList(leaf));
        } catch (PlanningException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Select a node or leaf from the tree. If a node is selected, all its
     * children are selected too.
     * 
     * @param node
     *            the node to select
     */
    public void select(TreeNode node) {
        initLeafLists();
        if (node instanceof Node) {
            leaves = node.getAllLeaves();
        } else if (node instanceof Leaf) {
            leaves.add((Leaf) node);
        }
    }

    /**
     * @see {@link EvaluateExperiments#approveAllValues() }
     */
    public void approve() {
        evaluateExperiments.approveAllValues();
    }

    /**
     * Initialises the leaf lists.
     */
    private void initLeafLists() {
        leaves.clear();
    }

    private void refreshMeasures() {
        measures.clear();
        // FIXME see Plan.getMeasuredMeasures

        // measures.addAll(plan.getMeasuredMeasures());
        for (Measure m : measures) {
            log.debug("prop:: " + m.getName());
        }
    }

    public List<Measure> getMeasurableProperties() {
        return measures;
    }

    /**
     * Starts the download of a result file.
     * 
     * @param alternative
     *            Alternative of the requested result file
     * @param sampleObject
     *            SampleObject of the requested result file
     */
    public void downloadResultFile(Alternative alternative, SampleObject sampleObject) {
        try {
            DigitalObject resultFile = evaluateExperiments.fetchResultFile(alternative, sampleObject);
            if (resultFile != null) {
                downloader.download(resultFile);
            } else {
                log.debug("No result file exists for alternative " + alternative.getName() + " and sample "
                    + sampleObject.getFullname());
            }
        } catch (StorageException e) {
            log.error("Exception at trying to fetch result file for alternative " + alternative.getName()
                + "and sample " + sampleObject.getFullname() + ": " + e.getMessage(), e);
            facesMessages.addError("Unable to fetch result-file");
        }
    }

    /**
     * Starts the download for the given sample object.
     * 
     * @param object
     *            the object to download
     */
    public void downloadSampleObject(SampleObject object) {
        if (object == null) {
            log.debug("No sample object provided.");
            return;
        }
        try {
            DigitalObject sampleObject = evaluateExperiments.fetchDigitalObject(object);
            if (sampleObject != null) {
                downloader.download(sampleObject);
            } else {
                log.debug("Sample object not found");
            }
        } catch (StorageException e) {
            log.error("Failed to fetch sample object " + object.getFullname(), e);
            facesMessages.addError("Unable to fetch sample object");
        }
    }

    /**
     * Generates characterisation reports of the provided {@code sample} and the
     * result of the {@code alternative}.
     * 
     * @param sample
     *            the sample to use
     * @param alternative
     *            alternative to use
     */
    public void generateCharacterisationReports(SampleObject sample, Alternative alternative) {
        CharacterisationReportGenerator reportGen = new CharacterisationReportGenerator();

        sampleCharacterisationReportAsHTML = reportGen.generateHTMLReport(sample);

        DigitalObject resultObject = alternative.getExperiment().getResults().get(sample);
        resultCharacterisationReportAsHTML = reportGen.generateHTMLReport(resultObject);
    }

    @Override
    protected AbstractWorkflowStep getWfStep() {
        return evaluateExperiments;
    }

    // ---------- getter/setter ----------
    public TreeHelperBean getTreeHelper() {
        return treeHelper;
    }

    public List<Leaf> getLeaves() {
        return leaves;
    }

    public List<TreeNode> getTreeRoot() {
        return treeRoot;
    }

    public boolean isAutoEvaluationAvailable() {
        return autoEvaluationAvailable;
    }

    public String getSampleCharacterisationReportAsHTML() {
        return sampleCharacterisationReportAsHTML;
    }

    public String getResultCharacterisationReportAsHTML() {
        return resultCharacterisationReportAsHTML;
    }
}
