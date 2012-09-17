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
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.PolicyNode;
import eu.scape_project.planning.model.Trigger;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.model.aggregators.IAggregator;
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

@Named("validatePlan")
@ConversationScoped
public class ValidatePlanView extends AbstractView {
    private static final long serialVersionUID = 8505584799409203390L;

    @Inject
    private Logger log;

    private boolean displayChangeLogs = false;
    private boolean displayEvalTransform = false;
    private boolean showAllAlternatives = false;

    @Inject
    private ValidatePlan validatePlan;
    @Inject
    private TreeHelperBean policytreeHelper;
    @Inject
    private TreeHelperBean treeHelper;

    @Inject
    private User user;

    private String repositoryUsername;
    private String repositoryPassword;

    public ValidatePlanView() {
        currentPlanState = PlanState.PLAN_DEFINED;
        name = "Validate Plan";
        viewUrl = "/plan/validateplan.jsf";
        group = "menu.buildPreservationPlan";
    }

    public void init(Plan plan) {
        super.init(plan);

        log.debug("initialising validatePlan");

        planetsExecutablePlanPrettyFormat = "";
        eprintsExecutablePlanPrettyFormat = "";

        this.acceptableAlternatives.clear();

        policytreeHelper.expandAll(plan.getTree().getRoot());
        treeHelper.expandAll(plan.getTree().getRoot());

        if (leafBeans == null) {
            leafBeans = new ArrayList<ReportLeaf>();
        } else {
            leafBeans.clear();
        }

        for (Leaf l : this.plan.getTree().getRoot().getAllLeaves()) {
            leafBeans.add(new ReportLeaf(l, plan.getAlternativesDefinition().getConsideredAlternatives()));
        }

        /*
         * Set roots and fill result-beans of the Multiplication- and Sum-Trees.
         */
        if (this.plan.getPlanProperties().getState().getValue() >= PlanState.TRANSFORMATION_DEFINED.getValue()) {
            multNode = new ResultNode(plan.getTree().getRoot(), new WeightedMultiplication(), plan
                .getAlternativesDefinition().getConsideredAlternatives());

            acceptableAlternatives = plan.getAcceptableAlternatives();

            sumNode = new ResultNode(plan.getTree().getRoot(), sumAggregator, acceptableAlternatives);
        }

        planetsExecutablePlanPrettyFormat = formatExecutablePlan(plan.getExecutablePlanDefinition().getExecutablePlan());
        eprintsExecutablePlanPrettyFormat = formatExecutablePlan(plan.getExecutablePlanDefinition()
            .getEprintsExecutablePlan());

        repositoryUsername = user.getUserGroup().getRepository().getUsername();
    }

    public String getCurrentDate() {
        return SimpleDateFormat.getDateTimeInstance().format(new Date());
    }

    public boolean isDisplayChangeLogs() {
        return displayChangeLogs;
    }

    public void switchDisplayChangeLogs() {
        displayChangeLogs = !displayChangeLogs;
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

    /**
     * for display on the page.
     */
    private String planetsExecutablePlanPrettyFormat = "";

    /**
     * for display on the page.
     */
    private String eprintsExecutablePlanPrettyFormat = "";

    private IAggregator sumAggregator = new WeightedSum();

    private Map<Trigger, String> selectedTriggers;

    private Map<Trigger, String> reevalSelectedTriggers;

    private List<ReportLeaf> leafBeans = new ArrayList<ReportLeaf>();

    private ResultNode sumNode;

    private ResultNode multNode;

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

    public String getEprintsExecutablePlanPrettyFormat() {
        return eprintsExecutablePlanPrettyFormat;
    }

    public IAggregator getSumAggregator() {
        return sumAggregator;
    }

    public Map<Trigger, String> getSelectedTriggers() {
        return selectedTriggers;
    }

    public Map<Trigger, String> getReevalSelectedTriggers() {
        return reevalSelectedTriggers;
    }

    public List<TreeNode> getRootNode() {
        List<TreeNode> l = new ArrayList<TreeNode>();
        l.add(plan.getTree().getRoot());
        return l;
    }

    public List<PolicyNode> getPolicyRoot() {
        List<PolicyNode> l = new ArrayList<PolicyNode>();
        if (plan.getProjectBasis().getPolicyTree() != null) {
            l.add(plan.getProjectBasis().getPolicyTree().getRoot());
        }
        return l;
    }

    public List<ResultNode> getSumNode() {
        List<ResultNode> l = new ArrayList<ResultNode>();
        l.add(sumNode);
        return l;
    }

    public List<ResultNode> getMultNode() {
        List<ResultNode> l = new ArrayList<ResultNode>();
        l.add(multNode);
        return l;
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

    @Override
    protected AbstractWorkflowStep getWfStep() {
        return validatePlan;
    }

    public TreeHelperBean getPolicytreeHelper() {
        return policytreeHelper;
    }

    public TreeHelperBean getTreeHelper() {
        return treeHelper;
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

}
