package eu.scape_project.planning.plato.wfview.full;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import org.slf4j.Logger;

import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.PolicyNode;
import eu.scape_project.planning.plato.bean.TreeHelperBean;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.DefineBasis;
import eu.scape_project.planning.plato.wfview.AbstractView;
import eu.scape_project.pw.planning.utils.FacesMessages;

/**
 * Bean for the viewWorkflow step 'Define Basis'.
 */
@Named("defineBasis")
@ConversationScoped
public class DefineBasisView extends AbstractView implements Serializable {
	private static final long serialVersionUID = 8237053627553012469L;

	@Inject private Logger log;
	
	@Inject private FacesMessages facesMessages;
	
	@Inject private DefineBasis defineBasis;
	
	@Inject private TreeHelperBean treeHelper;
	
	
	public DefineBasisView(){
    	currentPlanState = PlanState.INITIALISED;
    	name = "Define Basis";
    	viewUrl = "/plan/definebasis.jsf";
    	group = "menu.defineRequirements";
	}
	
    /**
     * Initializes 'Define Basis' viewWorkflow step, at the moment just the triggers.
     *
     * @see AbstractView#init()
     */
	public void init(Plan plan) {
    	super.init(plan);

    	// adopt organisational policies (if existent) if it's a new project
    	defineBasis.adoptOrganisationalPoliciesIfAppropriate();
        
        // expand all nodes of the displayed policy-tree (if existent)
        treeHelper.expandAll(plan.getProjectBasis().getPolicyTree().getRoot());
    }
	
	@Override
	protected AbstractWorkflowStep getWfStep() {
		return defineBasis;
	}

	/**
	 * Method responsible for triggering removal of the current assigned policy-tree.
	 */
	public void removePolicyTree() {
        defineBasis.removePolicyTree();
    }
	
    /**
     * Method responsible for starting a policy-tree import.
     * 
     * @param event Richfaces FileUploadEvent data.
     */
    public void importPolicyTree(FileUploadEvent event) {
    	UploadedFile file = event.getUploadedFile();
    	
    	// Do some input checks
    	if (!file.getName().endsWith("mm")) {
    		facesMessages.addError("importPoliciesButton", "Please select a FreeMind file.");
    		return;
    	}
    	
		// Put file-data into a digital object
		DigitalObject importFile = new DigitalObject();
		importFile.setFullname(file.getName());
		importFile.getData().setData(file.getData());
		importFile.setContentType(file.getContentType());
		
		boolean success = defineBasis.importPolicyTreeFromFreeMind(importFile);
		
		if (success) {
			// expand all nodes of the displayed policy-tree
			treeHelper.expandAll(plan.getProjectBasis().getPolicyTree().getRoot());
		}
		else {
			facesMessages.addError("importPoliciesButton", "Error at importing policy tree. Please check the input file for correctness.");
		}
    }
    
    /**
     * Method responsible for returning the policy-tree appropriate for displaying with rich:treeModelRecursiveAdaptor.
     * (This richfaces component requires a list of nodes to be returned.)
     * 
     * @return Policy-tree in list representation (for use in rich:treeModelRecursiveAdaptor).
     */
	public List<PolicyNode> getPolicyRoot() {
		List<PolicyNode> l = new ArrayList<PolicyNode>();
		if (plan.getProjectBasis().getPolicyTree() != null) {
			l.add(plan.getProjectBasis().getPolicyTree().getRoot());
		}
		return l;
	}

    // ---------- getter/setter ----------
    
	public TreeHelperBean getTreeHelper() {
		return treeHelper;
	}    
}
