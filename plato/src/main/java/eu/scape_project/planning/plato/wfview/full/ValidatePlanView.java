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

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.manager.ByteStreamManager;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.PolicyNode;
import eu.scape_project.planning.model.Trigger;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.model.aggregators.WeightedMultiplication;
import eu.scape_project.planning.model.aggregators.WeightedSum;
import eu.scape_project.planning.model.beans.ResultNode;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.tree.TreeNode;
import eu.scape_project.planning.plato.bean.TreeHelperBean;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.ValidatePlan;
import eu.scape_project.planning.plato.wfview.AbstractView;
import eu.scape_project.planning.plato.wfview.beans.ReportLeaf;
import eu.scape_project.planning.utils.Downloader;

@Named("validatePlan")
@ConversationScoped
public class ValidatePlanView extends AbstractView {
    private static final long serialVersionUID = 8505584799409203390L;

    @Inject private Logger log;

    @Inject private User user;
    @Inject private ValidatePlan validatePlan;

    @Inject private Downloader downloader;
    @Inject  private ByteStreamManager bytestreamManager;
    
    @Inject private TreeHelperBean policytreeHelper;
    @Inject private TreeHelperBean requirementstreeHelper;
    @Inject private TreeHelperBean resultstreeHelper;
    
    
    
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


    private String repositoryUsername;
    private String repositoryPassword;

    private boolean displayChangelogs;
    private boolean displayEvalTransform;
    private boolean showAllAlternatives;
    /**
     * for display on the page.
     */
    private String planetsExecutablePlanPrettyFormat = "";
    
    private Map<Trigger, String> selectedTriggers;
    
    private Map<Trigger, String> reevalSelectedTriggers;
    
    private List<ReportLeaf> leafBeans;
    
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
     * Indicates if all considered alternatives should be shown in the weighted
     * sum result tree.
     */
    private boolean showAllConsideredAlternativesForWeightedSum;

    /**
     * Alternatives showed in weighted sum result tree.
     */
    private List<Alternative> weightedSumResultTreeShownAlternatives;
    

    public ValidatePlanView() {
        currentPlanState = PlanState.PLAN_DEFINED;
        name = "Validate Plan";
        viewUrl = "/plan/validateplan.jsf";
        group = "menu.buildPreservationPlan";
        policyRoots = new ArrayList<PolicyNode>();
        requirementsRoots = new ArrayList<TreeNode>();
        leafBeans = new ArrayList<ReportLeaf>();
        acceptableAlternatives = new ArrayList<Alternative>();
        aggSumResultNodes = new ArrayList<ResultNode>();
        aggMultResultNodes = new ArrayList<ResultNode>();
        showAllConsideredAlternativesForWeightedSum = false;
        weightedSumResultTreeShownAlternatives = new ArrayList<Alternative>();
    }

    public void init(Plan plan) {
        super.init(plan);

        log.debug("initialising validatePlan");

        planetsExecutablePlanPrettyFormat = "";

        leafBeans.clear();
        for (Leaf l : this.plan.getTree().getRoot().getAllLeaves()) {
            leafBeans.add(new ReportLeaf(l, plan.getAlternativesDefinition().getConsideredAlternatives()));
        }

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
        acceptableAlternatives = plan.getAcceptableAlternatives();

        aggMultResultNodes.clear();
        aggMultResultNodes.add(
            new ResultNode(plan.getTree().getRoot(), new WeightedMultiplication(), plan.getAlternativesDefinition().getConsideredAlternatives()));
        
        showAllConsideredAlternativesForWeightedSum = false;
        weightedSumResultTreeShownAlternatives = acceptableAlternatives;

        aggSumResultNodes.clear();
        // calculate result nodes for all considered alternatives
        ResultNode sumResultNode = new ResultNode(plan.getTree().getRoot(), new WeightedSum(), plan.getAlternativesDefinition().getConsideredAlternatives());
        aggSumResultNodes.add(sumResultNode);        

        
        planetsExecutablePlanPrettyFormat = formatExecutablePlan(plan.getExecutablePlanDefinition().getExecutablePlan());

        if (user.getUserGroup().getRepository() != null) {
            repositoryUsername = user.getUserGroup().getRepository().getUsername();
        }
    }

    public String getCurrentDate() {
        return SimpleDateFormat.getDateTimeInstance().format(new Date());
    }

    public boolean isDisplayChangelogs() {
        return displayChangelogs;
    }

    public void switchDisplayChangelogs() {
        displayChangelogs = !displayChangelogs;
    }

    public void switchShowAllAlternatives() {
        showAllAlternatives = !showAllAlternatives;
    }

    public void switchDisplayEvalTransform() {
        displayEvalTransform = !displayEvalTransform;
    }

    public boolean isDisplayEvalTransform() {
        return displayEvalTransform;
    }

    /**
     * reads the executable preservation plan and formats it.
     * 
     */
    private String formatExecutablePlan(String executablePlan) {

        if (executablePlan == null || "".equals(executablePlan)) {
            return "";
        }

        try {
            Document doc = DocumentHelper.parseText(executablePlan);

            StringWriter sw = new StringWriter();

            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setNewlines(true);
            format.setTrimText(true);
            format.setIndent("  ");
            format.setExpandEmptyElements(false);
            format.setNewLineAfterNTags(20);

            XMLWriter writer = new XMLWriter(sw, format);

            writer.write(doc);
            writer.close();

            return sw.toString();

        } catch (DocumentException e) { 
            return "";
        } catch (IOException e) {
            return "";
        }
    }

    private List<Alternative> acceptableAlternatives = new ArrayList<Alternative>();


    public boolean isShowAllAlternatives() {
        return showAllAlternatives;
    }

    public List<ReportLeaf> getLeafBeans() {
        return leafBeans;
    }

    public List<Alternative> getAcceptableAlternatives() {
        return acceptableAlternatives;
    }

    public String getPlanetsExecutablePlanPrettyFormat() {
        return planetsExecutablePlanPrettyFormat;
    }

    public Map<Trigger, String> getSelectedTriggers() {
        return selectedTriggers;
    }

    public Map<Trigger, String> getReevalSelectedTriggers() {
        return reevalSelectedTriggers;
    }

    public void approvePlan() {
        validatePlan.approvePlan();
    }

    public void revisePlan() {
        validatePlan.revisePlan();
    }

    public void deployPlan() {
        try {
            validatePlan.uploadPlanToRODA(user.getUserGroup().getRepository().getUrl(), repositoryUsername,
                repositoryPassword);
            facesMessages.addInfo("Plan sucessfully deployed.");
        } catch (PlanningException e) {
            facesMessages.addError("There was an error deploying the plan: " + e.getCause().getMessage());
        } finally {
            repositoryUsername = user.getUserGroup().getRepository().getUsername();
            repositoryPassword = "";
        }
    }

    /**
     * Starts a download for the given digital object. Uses
     * {@link eu.scape_project.planning.util.Downloader} to perform the
     * download.
     */
    public void download(final DigitalObject object) {
        File file = bytestreamManager.getTempFile(object.getPid());
        if (file != null) {
            downloader.download(object, file);
        } else {
            log.error("Failed to retrieve object: " + object.getPid());
        }
    }    
    
    
    @Override
    protected AbstractWorkflowStep getWfStep() {
        return validatePlan;
    }

    public TreeHelperBean getPolicytreeHelper() {
        return policytreeHelper;
    }

    public String getRepositoryUsername() {
        return repositoryUsername;
    }

    public void setRepositoryUsername(String repositoryUsername) {
        this.repositoryUsername = repositoryUsername;
    }

    public String getRepositoryPassword() {
        return repositoryPassword;
    }

    public void setRepositoryPassword(String repositoryPassword) {
        this.repositoryPassword = repositoryPassword;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    /**
     * Switches listed weighted sum alternatives between
     * all considered and all acceptable.
     */
    public void switchShowAllConsideredAlternativesForWeightedSum() {
        if (showAllConsideredAlternativesForWeightedSum) {
            showAllConsideredAlternativesForWeightedSum = false;
            weightedSumResultTreeShownAlternatives = acceptableAlternatives;
        } else {
            showAllConsideredAlternativesForWeightedSum = true;
            weightedSumResultTreeShownAlternatives = plan.getAlternativesDefinition().getConsideredAlternatives();
        }
    }

    public TreeHelperBean getRequirementstreeHelper() {
        return requirementstreeHelper;
    }

    public TreeHelperBean getResultstreeHelper() {
        return resultstreeHelper;
    }

    public List<TreeNode> getRequirementsRoots() {
        return requirementsRoots;
    }
    
    public List<PolicyNode> getPolicyRoots() {
        return policyRoots;
    }
    
    public List<ResultNode> getAggSumResultNodes() {
        return aggSumResultNodes;
    }

    public List<ResultNode> getAggMultResultNodes() {
        return aggMultResultNodes;
    }

    public boolean isShowAllConsideredAlternativesForWeightedSum() {
        return showAllConsideredAlternativesForWeightedSum;
    }
    public List<Alternative> getWeightedSumResultTreeShownAlternatives() {
        return weightedSumResultTreeShownAlternatives;
    }
    
}
