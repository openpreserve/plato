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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import eu.scape_project.planning.manager.StorageException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.aggregators.WeightedMultiplication;
import eu.scape_project.planning.model.aggregators.WeightedSum;
import eu.scape_project.planning.model.beans.ResultNode;
import eu.scape_project.planning.model.tree.TreeNode;
import eu.scape_project.planning.plato.bean.TreeHelperBean;
import eu.scape_project.planning.plato.fte.FTAnalyseResults;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wfview.AbstractView;
import eu.scape_project.planning.plato.wfview.ViewWorkflowManager;
import eu.scape_project.planning.plato.wfview.beans.ReportLeaf;
import eu.scape_project.planning.utils.Downloader;

import org.slf4j.Logger;

@Named("ftAnalyseResults")
@ConversationScoped
public class FTAnalyseResultsView extends AbstractView {
    private static final long serialVersionUID = 1L;

    @Inject
    private Logger log;

    @Inject
    private Downloader downloader;

    @Inject
    private FTAnalyseResults ftAnalyseResults;

    @Inject
    private ViewWorkflowManager viewWorkflowManager;

    @Inject
    private TreeHelperBean treeHelper;

    /**
     * Variable encapsulating the RequirementsTree-Root in a list. This is
     * required, because <rich:treeModelRecursiveAdaptor> root variable requires
     * a list to work properly.
     */
    private List<TreeNode> requirementsRoots;

    /**
     * List of leaves containing result- and transformed-values.
     */
    private List<ReportLeaf> leafBeans;

    /**
     * Variable encapsulating the aggregated multiplication result tree-Root in
     * a list. This is required, because <rich:treeModelRecursiveAdaptor> root
     * variable requires a list to work properly.
     */
    private List<ResultNode> aggMultResultNodes;

    /**
     * Variable encapsulating the aggregated sum result tree-Root in a list.
     * This is required, because <rich:treeModelRecursiveAdaptor> root variable
     * requires a list to work properly.
     */
    private List<ResultNode> aggSumResultNodes;

    /**
     * Indicates whether there are knocked out alternatives present.
     */
    private boolean knockedoutAlternativePresent;

    /**
     * Indicates if all considered alternatives should be shown in the weighted
     * sum result tree.
     */
    private boolean showAllConsideredAlternativesForWeightedSum;

    /**
     * Alternatives showed in weighted sum result tree.
     */
    private List<Alternative> weightedSumResultTreeShownAlternatives;

    /**
     * Alternatives which did not produce a knock-out(=evaluate to 0) and
     * therefore can be choosen as recommened one.
     */
    private List<Alternative> acceptableAlternatives;

    /**
     * Recommended alternative selected by the user represented as String
     * (Because JSF-SelectOneMenu cannot handle Strings appropriately).
     */
    private String recommendedAlternativeAsString;

    public FTAnalyseResultsView() {
        currentPlanState = PlanState.RESULTS_CAPTURED;
        name = "Analyse Results";
        viewUrl = "/fte/FTanalyseresults.jsf";
        requirementsRoots = new ArrayList<TreeNode>();
        leafBeans = new ArrayList<ReportLeaf>();
        aggSumResultNodes = new ArrayList<ResultNode>();
        aggMultResultNodes = new ArrayList<ResultNode>();
        knockedoutAlternativePresent = true;
        showAllConsideredAlternativesForWeightedSum = false;
        weightedSumResultTreeShownAlternatives = new ArrayList<Alternative>();
        acceptableAlternatives = new ArrayList<Alternative>();
        recommendedAlternativeAsString = "";
    }

    @Override
    public void init(Plan plan) {
        super.init(plan);

        requirementsRoots.clear();
        if (plan.getTree() != null) {
            requirementsRoots.add(plan.getTree().getRoot());
        }

        leafBeans = ftAnalyseResults.constructPlanReportLeaves();

        aggMultResultNodes.clear();
        aggMultResultNodes.add(new ResultNode(plan.getTree().getRoot(), new WeightedMultiplication(), plan
            .getAlternativesDefinition().getConsideredAlternatives()));

        aggSumResultNodes.clear();
        ResultNode sumResultNode = new ResultNode(plan.getTree().getRoot(), new WeightedSum(), plan
            .getAlternativesDefinition().getConsideredAlternatives());
        aggSumResultNodes.add(sumResultNode);

        acceptableAlternatives = ftAnalyseResults.getAcceptableAlternatives();
        knockedoutAlternativePresent = acceptableAlternatives.size() != plan.getAlternativesDefinition()
            .getConsideredAlternatives().size();

        showAllConsideredAlternativesForWeightedSum = false;
        weightedSumResultTreeShownAlternatives = acceptableAlternatives;

        if (plan.getRecommendation().getAlternative() != null) {
            recommendedAlternativeAsString = plan.getRecommendation().getAlternative().getName();
        } else {
            recommendedAlternativeAsString = "";
        }
        // all leaves are shown, unless the users decided to change this.
        if (treeHelper.getExpandedNodes().isEmpty()) {
            treeHelper.expandAll(plan.getTree().getRoot());
        }

    }

    /**
     * Method responsible for returning the current time as String.
     * 
     * @return Current time as String.
     */
    public String getCurrentDate() {
        return SimpleDateFormat.getDateTimeInstance().format(new Date());
    }

    /**
     * Method responsible for starting the download for the given sample object.
     * 
     * @param object
     *            SampleObject to download.
     */
    public void downloadSample(Object object) {
        SampleObject sample = (SampleObject) object;

        try {
            downloader.download(ftAnalyseResults.fetchDigitalObject(sample));
        } catch (StorageException e) {
            log.error("Exception at trying to fetch sample file with pid " + sample.getPid(), e);
            return;
        }
    }

    /**
     * Method responsible for switching listed weighted sum alternatives between
     * all considered and all acceptable.
     */
    public void switchShowAllConsideredAlternativesForWeightedSum() {
        if (showAllConsideredAlternativesForWeightedSum) {
            weightedSumResultTreeShownAlternatives = plan.getAlternativesDefinition().getConsideredAlternatives();
        } else {
            weightedSumResultTreeShownAlternatives = acceptableAlternatives;
        }
    }

    /**
     * Method responsible for updating the recommended alternative in the model
     * based on the given alternative-name.
     * 
     * @param alternativeName
     *            Alternative name of the recommended alternative
     */
    private void updateAlternativeRecommendation(String alternativeName) {
        Alternative recommendedAlternative = null;

        for (Alternative a : plan.getAlternativesDefinition().getAlternatives()) {
            if (a.getName().equals(alternativeName)) {
                recommendedAlternative = a;
                break;
            }
        }

        plan.getRecommendation().setAlternative(recommendedAlternative);
    }

    /**
     * Method responsible for converting this fast track workflow into a
     * standard workflow and continuing it as those.
     * 
     * @return outcome-string of the first page (from standard workflow) to
     *         render
     */
    public String continueAsStandardPreservationPlan() {
        // transform the plan
        ftAnalyseResults.transformToStandardPreservationPlan();

        // restart the workflow to be assembled by standard workflow-steps
        viewWorkflowManager.endWorkflow();

        return viewWorkflowManager.startWorkflow(plan);
    }

    /**
     * This is the end of the fast track workflow - so no proceed is possible
     * here. End-status of the plan is set in
     * {@link FTAnalyseResults#saveStepSpecific()} if a recommendation is set.
     * This fast track plan can also be continued as standard plan after calling
     * {@link FTAnalyseResults#transformToStandardPreservationPlan()}
     * 
     * @return the navigation target
     */
    @Override
    public String proceed() {
        return null;
    }

    @Override
    protected AbstractWorkflowStep getWfStep() {
        return ftAnalyseResults;
    }

    // --------------- getter/setter ---------------

    public List<TreeNode> getRequirementsRoots() {
        return requirementsRoots;
    }

    public void setRequirementsRoots(List<TreeNode> requirementsRoots) {
        this.requirementsRoots = requirementsRoots;
    }

    public List<ReportLeaf> getLeafBeans() {
        return leafBeans;
    }

    public void setLeafBeans(List<ReportLeaf> leafBeans) {
        this.leafBeans = leafBeans;
    }

    public List<ResultNode> getAggMultResultNodes() {
        return aggMultResultNodes;
    }

    public void setAggMultResultNodes(List<ResultNode> aggMultResultNodes) {
        this.aggMultResultNodes = aggMultResultNodes;
    }

    public List<ResultNode> getAggSumResultNodes() {
        return aggSumResultNodes;
    }

    public void setAggSumResultNodes(List<ResultNode> aggSumResultNodes) {
        this.aggSumResultNodes = aggSumResultNodes;
    }

    public boolean isShowAllConsideredAlternativesForWeightedSum() {
        return showAllConsideredAlternativesForWeightedSum;
    }

    public void setShowAllConsideredAlternativesForWeightedSum(boolean showAllConsideredAlternativesForWeightedSum) {
        this.showAllConsideredAlternativesForWeightedSum = showAllConsideredAlternativesForWeightedSum;
    }

    public boolean isKnockedoutAlternativePresent() {
        return knockedoutAlternativePresent;
    }

    public List<Alternative> getWeightedSumResultTreeShownAlternatives() {
        return weightedSumResultTreeShownAlternatives;
    }

    public void setWeightedSumResultTreeShownAlternatives(List<Alternative> weightedSumResultTreeShownAlternatives) {
        this.weightedSumResultTreeShownAlternatives = weightedSumResultTreeShownAlternatives;
    }

    public List<Alternative> getAcceptableAlternatives() {
        return acceptableAlternatives;
    }

    public void setAcceptableAlternatives(List<Alternative> acceptableAlternatives) {
        this.acceptableAlternatives = acceptableAlternatives;
    }

    public String getRecommendedAlternativeAsString() {
        return recommendedAlternativeAsString;
    }

    public void setRecommendedAlternativeAsString(String recommendedAlternativeAsString) {
        this.recommendedAlternativeAsString = recommendedAlternativeAsString;
        updateAlternativeRecommendation(recommendedAlternativeAsString);
    }

    public TreeHelperBean getTreeHelper() {
        return treeHelper;
    }
}
