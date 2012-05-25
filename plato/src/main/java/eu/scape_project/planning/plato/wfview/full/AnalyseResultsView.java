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
package eu.scape_project.planning.plato.wfview.full;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import eu.scape_project.planning.manager.StorageException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.PolicyNode;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.beans.ResultNode;
import eu.scape_project.planning.model.tree.TreeNode;
import eu.scape_project.planning.plato.bean.TreeHelperBean;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.AnalyseResults;
import eu.scape_project.planning.plato.wfview.AbstractView;
import eu.scape_project.planning.plato.wfview.beans.ReportLeaf;
import eu.scape_project.planning.utils.Downloader;

@Named("analyseResults")
@ConversationScoped
public class AnalyseResultsView extends AbstractView {
	private static final long serialVersionUID = 1L;

	@Inject	private AnalyseResults analyseResults;

	@Inject private Downloader downloader;
	
	@Inject private Logger log;
	
	@Inject private TreeHelperBean policytreeHelper;
	@Inject private TreeHelperBean requirementstreeHelper;
	@Inject private TreeHelperBean resultstreeHelper;
	
	private boolean displayChangelogs;
	
	/**
	 * Variable encapsulating the PolicyTree-Root in a list.
	 * This is required, because <rich:treeModelRecursiveAdaptor> root variable requires a list to work properly. 
	 */
	private List<PolicyNode> policyRoots;
	
	/**
	 * Variable encapsulating the RequirementsTree-Root in a list.
	 * This is required, because <rich:treeModelRecursiveAdaptor> root variable requires a list to work properly. 
	 */
	private List<TreeNode> requirementsRoots;
	
	/**
	 * List of leaves containing result- and transformed-values
	 */
	private List<ReportLeaf> leafBeans;
		
	/**
	 * Alternatives which did not produce a knock-out(=evaluate to 0) and therefore can be choosen as recommened one. 
	 */
	private List<Alternative> acceptableAlternatives;
	
	/**
	 * Variable encapsulating the aggregated sum result tree-Root in a list.
	 * This is required, because <rich:treeModelRecursiveAdaptor> root variable requires a list to work properly.
	 */
	private List<ResultNode> aggSumResultNodes;
	
	/**
	 * Variable encapsulating the aggregated multiplication result tree-Root in a list.
	 * This is required, because <rich:treeModelRecursiveAdaptor> root variable requires a list to work properly.
	 */
	private List<ResultNode> aggMultResultNodes;
	
	/**
	 * Indicates if all considered alternatives should be shown in the weighted sum result tree.
	 */
	private boolean showAllConsideredAlternativesForWeightedSum;
	
	/**
	 * Alternatives showed in weighted sum result tree.
	 */
	private List<Alternative> weightedSumResultTreeShownAlternatives;
	
	/**
	 * Recommended alternative selected by the user represented as String (Because JSF-SelectOneMenu cannot handle Strings appropriately).
	 */
	private String recommendedAlternativeAsString;


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
		showAllConsideredAlternativesForWeightedSum = false;
		weightedSumResultTreeShownAlternatives = new ArrayList<Alternative>();
		recommendedAlternativeAsString = "";
	}

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
    	
    	acceptableAlternatives = analyseResults.getAcceptableAlternatives();
    	
    	aggMultResultNodes.clear();
    	aggMultResultNodes.add(analyseResults.getAggregatedMultiplicationResultNode());
    	
    	aggSumResultNodes.clear();
    	ResultNode sumResultNode = analyseResults.getAggregatedSumResultNode();
    	aggSumResultNodes.add(sumResultNode);
    	
    	showAllConsideredAlternativesForWeightedSum = false;
    	weightedSumResultTreeShownAlternatives = acceptableAlternatives;
    	
    	analyseResults.analyseSensitivity(sumResultNode, acceptableAlternatives);
    	
    	if (plan.getRecommendation().getAlternative() != null) {
    		recommendedAlternativeAsString = plan.getRecommendation().getAlternative().getName();
    	}
    	else {
    		recommendedAlternativeAsString = "";
    	}
	}
	
	/**
	 * Method responsible for starting the download for the given sample object.
	 * 
	 * @param sample SampleObject to download.
	 */
	public void downloadSample(Object object) {
		SampleObject sample = (SampleObject) object;
		try {
			downloader.download(analyseResults.fetchSampleObject(sample));
		} catch (StorageException e) {
			log.error("Exception at trying to fetch sample file with pid " + sample.getPid(), e);
			facesMessages.addError("Unable to fetch attached file");
			return;
		}
	}
	
	/**
	 * Method responsible for turning changelog-display on/off.
	 */
	public void switchDisplayChangelogs() {
		displayChangelogs = !displayChangelogs;
	}
	
	/**
	 * Method responsible for switching listed weighted sum alternatives between all considered and all acceptable.
	 */
	public void switchShowAllConsideredAlternativesForWeightedSum() {
		if (showAllConsideredAlternativesForWeightedSum) {
			showAllConsideredAlternativesForWeightedSum = false;
			weightedSumResultTreeShownAlternatives = acceptableAlternatives;
		}
		else {
			showAllConsideredAlternativesForWeightedSum = true;
			weightedSumResultTreeShownAlternatives = plan.getAlternativesDefinition().getConsideredAlternatives();
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

	@Override
	protected AbstractWorkflowStep getWfStep() {
		return analyseResults;
	}
	
	/**
	 * Method responsible for updating the recommended alternative in the model based on the given alternative-name. 
	 * 
	 * @param alternativeName Alternative name of the recommended alternative
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

	public void setDisplayChangelogs(boolean displayChangelogs) {
		this.displayChangelogs = displayChangelogs;
	}

	public List<PolicyNode> getPolicyRoots() {
		return policyRoots;
	}

	public void setPolicyRoots(List<PolicyNode> policyRoots) {
		this.policyRoots = policyRoots;
	}

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

	public List<Alternative> getAcceptableAlternatives() {
		return acceptableAlternatives;
	}

	public void setAcceptableAlternatives(List<Alternative> acceptableAlternatives) {
		this.acceptableAlternatives = acceptableAlternatives;
	}

	public List<ResultNode> getAggSumResultNodes() {
		return aggSumResultNodes;
	}

	public void setAggSumResultNodes(List<ResultNode> aggSumResultNodes) {
		this.aggSumResultNodes = aggSumResultNodes;
	}

	public List<ResultNode> getAggMultResultNodes() {
		return aggMultResultNodes;
	}

	public void setAggMultResultNodes(List<ResultNode> aggMultResultNodes) {
		this.aggMultResultNodes = aggMultResultNodes;
	}

	public boolean isShowAllConsideredAlternativesForWeightedSum() {
		return showAllConsideredAlternativesForWeightedSum;
	}

	public void setShowAllConsideredAlternativesForWeightedSum(
			boolean showAllConsideredAlternativesForWeightedSum) {
		this.showAllConsideredAlternativesForWeightedSum = showAllConsideredAlternativesForWeightedSum;
	}

	public List<Alternative> getWeightedSumResultTreeShownAlternatives() {
		return weightedSumResultTreeShownAlternatives;
	}

	public void setWeightedSumResultTreeShownAlternatives(
			List<Alternative> weightedSumResultTreeShownAlternatives) {
		this.weightedSumResultTreeShownAlternatives = weightedSumResultTreeShownAlternatives;
	}

	public String getRecommendedAlternativeAsString() {
		return recommendedAlternativeAsString;
	}

	public void setRecommendedAlternativeAsString(
			String recommendedAlternativeAsString) {
		this.recommendedAlternativeAsString = recommendedAlternativeAsString;
		updateAlternativeRecommendation(recommendedAlternativeAsString);
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
