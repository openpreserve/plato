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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import eu.scape_project.planning.manager.ByteStreamManager;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.PolicyNode;
import eu.scape_project.planning.model.aggregators.WeightedMultiplication;
import eu.scape_project.planning.model.aggregators.WeightedSum;
import eu.scape_project.planning.model.beans.ResultNode;
import eu.scape_project.planning.model.tree.TreeNode;
import eu.scape_project.planning.plato.bean.TreeHelperBean;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.AnalyseResults;
import eu.scape_project.planning.plato.wfview.AbstractView;
import eu.scape_project.planning.plato.wfview.beans.ReportLeaf;
import eu.scape_project.planning.utils.Downloader;

/**
 * View bean for the workflow step analyse results.
 */
@Named("analyseResults")
@ConversationScoped
public class AnalyseResultsView extends AbstractView {
    private static final long serialVersionUID = 1L;

    @Inject
    private Logger log;

    @Inject
    private AnalyseResults analyseResults;

    @Inject
    private Downloader downloader;

    @Inject
    private ByteStreamManager bytestreamManager;

    @Inject
    private TreeHelperBean policytreeHelper;

    @Inject
    private TreeHelperBean requirementstreeHelper;

    @Inject
    private TreeHelperBean resultstreeHelper;

    /**
     * Variable encapsulating the PolicyTree-Root in a list. This is required,
     * because <rich:treeModelRecursiveAdaptor> root variable requires a list to
     * work properly.
     */
    private List<PolicyNode> policyRoots;

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
     * Alternatives which did not produce a knock-out(=evaluate to 0) and
     * therefore can be chosen as recommended one.
     */
    private List<Alternative> acceptableAlternatives;

    /**
     * Variable encapsulating the aggregated sum result tree-Root in a list.
     * This is required, because <rich:treeModelRecursiveAdaptor> root variable
     * requires a list to work properly.
     */
    private List<ResultNode> aggSumResultNodes;

    /**
     * Variable encapsulating the aggregated multiplication result tree-Root in
     * a list. This is required, because <rich:treeModelRecursiveAdaptor> root
     * variable requires a list to work properly.
     */
    private List<ResultNode> aggMultResultNodes;

    /**
     * Indicates whether there are knocked out alternatives present.
     */
    private boolean knockedoutAlternativePresent;

    /**
     * Indicates if all considered alternatives should be shown in the weighted
     * sum result tree.
     */
    private boolean showAllConsideredAlternativesForWeightedSum;

    private boolean displayChangelogs;
    /**
     * Alternatives showed in weighted sum result tree.
     */
    private List<Alternative> weightedSumResultTreeShownAlternatives;

    /**
     * Recommended alternative selected by the user represented as String
     * (Because JSF-SelectOneMenu cannot handle Strings appropriately).
     */
    private String recommendedAlternativeAsString;

    /**
     * Constructs a new view bean.
     */
    public AnalyseResultsView() {
        currentPlanState = PlanState.WEIGHTS_SET;
        name = "Analyse Results";
        viewUrl = "/plan/analyseresults.jsf";
        group = "menu.analyseResults";
        displayChangelogs = false;
        policyRoots = new ArrayList<PolicyNode>();
        requirementsRoots = new ArrayList<TreeNode>();
        leafBeans = new ArrayList<ReportLeaf>();
        acceptableAlternatives = new ArrayList<Alternative>();
        aggSumResultNodes = new ArrayList<ResultNode>();
        aggMultResultNodes = new ArrayList<ResultNode>();
        knockedoutAlternativePresent = true;
        showAllConsideredAlternativesForWeightedSum = false;
        weightedSumResultTreeShownAlternatives = new ArrayList<Alternative>();
        recommendedAlternativeAsString = "";
    }

    @Override
    public void init(Plan plan) {
        super.init(plan);

        policyRoots.clear();
        if (plan.getProjectBasis().getPolicyTree() != null) {
            PolicyNode policyRoot = plan.getProjectBasis().getPolicyTree().getRoot();
            if (policyRoot != null) {
                policyRoots.add(policyRoot);
                policytreeHelper.expandAll(policyRoot);
            }
        }

        requirementsRoots.clear();
        if (plan.getTree() != null) {
            TreeNode requirementsRoot = plan.getTree().getRoot();
            if (requirementsRoot != null) {
                requirementsRoots.add(requirementsRoot);
                requirementstreeHelper.expandAll(requirementsRoot);
            }
        }

        leafBeans = analyseResults.constructPlanReportLeaves();

        acceptableAlternatives = plan.getAcceptableAlternatives();

        aggMultResultNodes.clear();
        aggMultResultNodes.add(new ResultNode(plan.getTree().getRoot(), new WeightedMultiplication(), plan
            .getAlternativesDefinition().getConsideredAlternatives()));

        aggSumResultNodes.clear();
        // calculate result nodes for all considered alternatives
        ResultNode sumResultNode = new ResultNode(plan.getTree().getRoot(), new WeightedSum(), plan
            .getAlternativesDefinition().getConsideredAlternatives());
        aggSumResultNodes.add(sumResultNode);

        knockedoutAlternativePresent = acceptableAlternatives.size() != plan.getAlternativesDefinition()
            .getConsideredAlternatives().size();
        showAllConsideredAlternativesForWeightedSum = false;
        weightedSumResultTreeShownAlternatives = acceptableAlternatives;

        analyseResults.analyseSensitivity(sumResultNode, acceptableAlternatives);

        if (plan.getRecommendation().getAlternative() != null) {
            recommendedAlternativeAsString = plan.getRecommendation().getAlternative().getName();
        } else {
            recommendedAlternativeAsString = "";
        }
    }

    @Override
    protected AbstractWorkflowStep getWfStep() {
        return analyseResults;
    }

    /**
     * Starts a download for the given digital object. Uses
     * {@link eu.scape_project.planning.util.Downloader} to perform the
     * download.
     * 
     * @param object
     *            the digital object to download
     */
    public void download(final DigitalObject object) {
        File file = bytestreamManager.getTempFile(object.getPid());
        if (file != null) {
            downloader.download(object, file);
        } else {
            log.error("Failed to retrieve object: " + object.getPid());
        }
    }

    /**
     * Method responsible for turning changelog-display on/off.
     */
    public void switchDisplayChangelogs() {
        displayChangelogs = !displayChangelogs;
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
     * Method responsible for returning the current time as String.
     * 
     * @return Current time as String.
     */
    public String getCurrentDate() {
        return SimpleDateFormat.getDateTimeInstance().format(new Date());
    }

    /**
     * Sets the recommended alternative identified by the alternative name.
     * 
     * @param recommendedAlternativeAsString
     *            the name of the recommended alternative
     */
    public void setRecommendedAlternativeAsString(String recommendedAlternativeAsString) {
        this.recommendedAlternativeAsString = recommendedAlternativeAsString;
        updateAlternativeRecommendation(recommendedAlternativeAsString);
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

        analyseResults.recommendAlternative(recommendedAlternative);
    }

    // --------------- getter/setter ---------------

    public boolean isDisplayChangelogs() {
        return displayChangelogs;
    }

    public List<PolicyNode> getPolicyRoots() {
        return policyRoots;
    }

    public List<TreeNode> getRequirementsRoots() {
        return requirementsRoots;
    }

    public List<ReportLeaf> getLeafBeans() {
        return leafBeans;
    }

    public List<Alternative> getAcceptableAlternatives() {
        return acceptableAlternatives;
    }

    public List<ResultNode> getAggSumResultNodes() {
        return aggSumResultNodes;
    }

    public List<ResultNode> getAggMultResultNodes() {
        return aggMultResultNodes;
    }

    public boolean isKnockedoutAlternativePresent() {
        return knockedoutAlternativePresent;
    }

    public boolean isShowAllConsideredAlternativesForWeightedSum() {
        return showAllConsideredAlternativesForWeightedSum;
    }

    public void setShowAllConsideredAlternativesForWeightedSum(boolean showAllConsideredAlternativesForWeightedSum) {
        this.showAllConsideredAlternativesForWeightedSum = showAllConsideredAlternativesForWeightedSum;
    }

    public List<Alternative> getWeightedSumResultTreeShownAlternatives() {
        return weightedSumResultTreeShownAlternatives;
    }

    public String getRecommendedAlternativeAsString() {
        return recommendedAlternativeAsString;
    }

    public TreeHelperBean getPolicytreeHelper() {
        return policytreeHelper;
    }

    public TreeHelperBean getRequirementstreeHelper() {
        return requirementstreeHelper;
    }

    public TreeHelperBean getResultstreeHelper() {
        return resultstreeHelper;
    }

}
