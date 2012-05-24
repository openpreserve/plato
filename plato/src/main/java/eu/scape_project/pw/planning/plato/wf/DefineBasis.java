package eu.scape_project.pw.planning.plato.wf;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import eu.planets_project.pp.plato.xml.TreeLoader;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.PolicyNode;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.model.tree.PolicyTree;

/**
 * Business logic for workflow step Define Basis
 *  
 * @author Michael Kraxner, Markus Hamm
 */
@Stateful
@ConversationScoped
public class DefineBasis extends AbstractWorkflowStep {
	private static final long serialVersionUID = -2269973220973705568L;

	@Inject private Logger log;
	
	@Inject private TreeLoader treeLoader;
	
	@Inject private User user;
	
	
	public DefineBasis(){
		this.requiredPlanState = PlanState.INITIALISED;
		this.correspondingPlanState = PlanState.BASIS_DEFINED;
	}
	
	@Override
	protected void saveStepSpecific() {
		prepareChangesForPersist.prepare(plan);
		
		saveEntity(plan.getProjectBasis());
	}
	
	/**
	 * Method responsible for adopting organisational policies if appropriate (if existent and if it is a new project).
	 * If the user removes the policy tree, proceeds and comes back, policies should not be adopted again. We ensure
	 * this by checking the plan state.
	 */
	public void adoptOrganisationalPoliciesIfAppropriate() {
        if (plan.getPlanProperties().getState()  == PlanState.INITIALISED
                && plan.getProjectBasis().getPolicyTree().getRoot() == null) {
            
            if (user.getOrganisation() != null 
                    && user.getOrganisation().getPolicyTree() != null) {
                
            	PolicyNode organisationPolicyRoot = user.getOrganisation().getPolicyTree().getRoot();
            	
            	if (organisationPolicyRoot != null) {
            		plan.getProjectBasis().getPolicyTree().setRoot(organisationPolicyRoot.clone());
            		log.debug("Assigned organisational policies to plan with id " + plan.getId());
            	} else {
            		plan.getProjectBasis().getPolicyTree().setRoot(null);
            		log.debug("No organisational policies to add to plan with id " + plan.getId());
            	}
            }
        }
	}

	/**
	 * Method responsible for importing a policy tree from a given FreeMind file.
	 * 
	 * @param file FreeMind file to import
	 * @return True if import was successful. False otherwise
	 */
	public boolean importPolicyTreeFromFreeMind(DigitalObject file) {
	    log.debug("Start FreeMind import of policy tree.");
		log.debug("FileName: " + file.getFullname());
	    
		InputStream istream = new ByteArrayInputStream(file.getData().getData());
		PolicyTree newtree = treeLoader.loadFreeMindPolicyTree(istream);
		
		if (newtree == null) {
		    log.error("File is corrupted and new policy-tree cannot be built out of it.");
		    return false;
		}
		
		plan.getProjectBasis().getPolicyTree().setRoot(newtree.getRoot());
		plan.getProjectBasis().touch();
		
		log.debug("Policy-tree imported successful");
		
		return true;
	}
	
	/**
	 * Method responsible for removing a policy-tree from a  plan.
	 */
	public void removePolicyTree() {
        plan.getProjectBasis().getPolicyTree().setRoot(null);
        plan.getProjectBasis().touch();
    }
}
