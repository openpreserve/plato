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

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.transform.NumericTransformer;
import eu.scape_project.planning.model.transform.Transformer;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.tree.Node;
import eu.scape_project.planning.model.tree.TreeNode;
import eu.scape_project.planning.plato.bean.TreeHelperBean;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.TransformMeasuredValues;
import eu.scape_project.planning.plato.wfview.AbstractView;
import eu.scape_project.planning.validation.ValidationError;

/**
 * Class responsible as backing bean for the view transformmeasuredvalues.xhtml.
 * 
 * @author Markus Hamm
 */
@Named("transform")
@ConversationScoped
public class TransformMeasuredValuesView extends AbstractView {
    private static final long serialVersionUID = 1L;

    @Inject
    private Logger log;

    @Inject
    private TransformMeasuredValues transformMeasuredValues;

    /**
     * Leaves displayed to the user (for editing).
     */
    private List<Leaf> leaves;


    @Inject
    private TreeHelperBean treeHelper;
    
    /**
     * Variable encapsulating the ObjectiveTree-Root in a list. This is
     * required, because <rich:treeModelRecursiveAdaptor> root variable requires
     * a list to work properly.
     */
    private List<TreeNode> treeRoots;

    public TransformMeasuredValuesView() {
        currentPlanState = PlanState.RESULTS_CAPTURED;
        name = "Transform Measured Values";
        viewUrl = "/plan/transformmeasuredvalues.jsf";
        group = "menu.analyseResults";

        leaves = new ArrayList<Leaf>();
        treeRoots = new ArrayList<TreeNode>();
    }

    public void init(Plan plan) {
        super.init(plan);

        // init treeRoots for view
        treeRoots.clear();
        treeRoots.add(plan.getTree().getRoot());

        // reset leaf lists
        leaves.clear();
        
        treeHelper.resetAllNodes();

        // This is not needed any more because this part is already done in the
        // previous step (Evaluate Experiments)
        // Initialising the values for free text transformers
        // for (Leaf l:plan.getTree().getRoot().getAllLeaves()) {
        // l.initTransformer();
        // }
    }

    /**
     * Method responsible for setting the leaves to display regarding to the
     * TreeNode (Node/Leaf) user selection in the objective tree. All leaves
     * under that selected node will be subsequently displayed to the user for
     * input reasons.
     * 
     * @param node
     *            Clicked treeNode in the objective tree.
     */
    public void selectTreeNode(Object node) {
        leaves.clear();

        if (node instanceof Node) {
            log.debug("Setting all Leaves under Node: " + node.toString());
            leaves = ((Node) node).getAllLeaves();
        } else if (node instanceof Leaf) {
            log.debug("Setting Leaf: " + node.toString());
            leaves.add((Leaf) node);
        }

        logTransformers();
    }

    /**
     * Method responsible for approving the transformer settings of the
     * currently displayed leaves.
     */
    public void approve() {
        transformMeasuredValues.approve(leaves);
        logTransformers();
        facesMessages.addInfo("confirmTransformationSettings", "Confirmed transformation settings");
    }

    /**
     * sets (primitive) default values for all numeric and boolean transformers.
     */
    public void calculateDefaultTransformers() {
        transformMeasuredValues.calculateDefaultTransformers();
        facesMessages.addInfo("calculateDefaultTransfomationSettings", "Applied default transformation settings");
    }

    @Override
    protected boolean tryProceed(List<ValidationError> errors) {
        boolean success = getWfStep().proceed(errors);

        // If validation was not successful - display all error nodes for easy
        // editing
        if (!success) {
            leaves.clear();

            for (ValidationError error : errors) {
                if (error.getInvalidObject() instanceof Leaf) {
                    Leaf errorLeaf = (Leaf) error.getInvalidObject();
                    leaves.add(errorLeaf);
                    treeHelper.expandNode(errorLeaf);                    
                }
            }
        }

        return success;
    }

    @Override
    protected AbstractWorkflowStep getWfStep() {
        return transformMeasuredValues;
    }

    /**
     * Method responsible for logging transformer settings for all displayed
     * leaves.
     */
    private void logTransformers() {
        String id = "";
        try {
            id = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
                .getSession().getId();
        } catch (RuntimeException e) {
            log.debug("Couldn't get SessionID");
        }
        for (Leaf leaf : leaves) {
            logTransformer(id, leaf.getName(), leaf.getTransformer());
        }
    }

    /**
     * Method responsible for logging specific transformer settings.
     */
    private void logTransformer(String sessionID, String leafName, Transformer t) {
        if (t == null) {
            log.error("TRANSFORMER NULL at " + leafName + " IN SESSION " + sessionID);
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(sessionID).append(":").append(leafName);
        if (t instanceof NumericTransformer) {
            NumericTransformer nt = (NumericTransformer) t;
            sb.append("::NUMERICTRANSFORMER:: ");
            sb.append(nt.getThreshold1()).append(" ").append(nt.getThreshold2()).append(" ").append(nt.getThreshold3())
                .append(" ").append(nt.getThreshold4()).append(" ").append(nt.getThreshold5());
            log.debug(sb.toString());
        }
    }

    // --------------- getter/setter ---------------

    public List<TreeNode> getTreeRoots() {
        return treeRoots;
    }

    public void setTreeRoots(List<TreeNode> treeRoots) {
        this.treeRoots = treeRoots;
    }

    public List<Leaf> getLeaves() {
        return leaves;
    }

    public void setLeaves(List<Leaf> leaves) {
        this.leaves = leaves;
    }

    public TreeHelperBean getTreeHelper() {
        return treeHelper;
    }
}
