package eu.scape_project.planning.policies;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.PolicyNode;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.plato.bean.TreeHelperBean;
import eu.scape_project.planning.utils.FacesMessages;

@Named("organisationalPolicies")
@SessionScoped
public class OrganisationalPoliciesView implements Serializable {
	private static final long serialVersionUID = 1949891454912441259L;
	
	@Inject
	private FacesMessages facesMessages;
	
	@Inject
	private OrganisationalPolicies organisationalPolicies;
	
	@Inject
	private User user;
	
	@Inject 
	private TreeHelperBean treeHelper;
	
	private DigitalObject importFile;
	
	/**
	 * Variable encapsulating the PoliyTree-Root in a list.
	 * This is required, because <rich:treeModelRecursiveAdaptor> root variable requires a list to work properly. 
	 */
	private List<PolicyNode> policyTreeRoots;
	
	public OrganisationalPoliciesView() {
		importFile = null;
		policyTreeRoots = new ArrayList<PolicyNode>();
	}
	
	/**
	 * Method responsible for initializing all properties with proper values - so the page can be displayed correctly.
	 * @return OutcomeString which navigates to this page.
	 */
	public String init() {
		policyTreeRoots = new ArrayList<PolicyNode>();
		
		if ((user.getOrganisation() != null) && (user.getOrganisation().getPolicyTree() != null) && (user.getOrganisation().getPolicyTree().isPolicyTreeDefined())) {
			policyTreeRoots.add(user.getOrganisation().getPolicyTree().getRoot());
	        // expand all nodes of the displayed policy-tree (if existent)
	        treeHelper.expandAll(user.getOrganisation().getPolicyTree().getRoot());
		}
		
		return "/user/organisationalpolicies.jsf";
	}

	/**
	 * Method responsible for importing the selected FreeMind-file as policy tree.
	 */
	public void importPolicyTree() {
		boolean importSuccessful = organisationalPolicies.importPolicyTreeFromFreemind(importFile);
		
		if (importSuccessful) {
			facesMessages.addInfo("importPanel", "Policy tree imported successfully");
			importFile = null;
			init();
		}
		else {
			facesMessages.addError("importPanel", "The uploaded file is not a valid Freemind mindmap. Maybe it is corrupted?");
		}
	}
	
    /**
     * Method responsible for selecting/setting a file for a later import.
     * 
     * @param event Richfaces FileUploadEvent data.
     */
    public void selectImportFile(FileUploadEvent event) {
    	UploadedFile file = event.getUploadedFile();
    	
    	// Do some input checks
    	if (!file.getName().endsWith("mm")) {
    		facesMessages.addError("importPanel", "Please select a FreeMind file.");
    		importFile = null;
    		return;
    	}
    	
		// Put file-data into a digital object
		importFile = new DigitalObject();
		importFile.setFullname(file.getName());
		importFile.getData().setData(file.getData());
		importFile.setContentType(file.getContentType());
    }
    
    /**
     * Method responsible for removing the current set policy tree.
     */
    public void removePolicTree() {
    	organisationalPolicies.removePolicyTree();
    	init();
    }
    
    /**
     * Method responsible for saving the made changes
     * @return Outcome String redirecting to start page.
     */
    public String save() {
    	organisationalPolicies.save();
    	return "/index.jsp";
    }
    
    /**
     * Method responsible for discarding the made changes
     * @return Outcome String redirecting to start page.
     */
    public String discard() {
    	organisationalPolicies.discard();
    	init();
    	return "/index.jsp";
    }
    
	// --------------- getter/setter ---------------
	
	public DigitalObject getImportFile() {
		return importFile;
	}

	public void setImportFile(DigitalObject importFile) {
		this.importFile = importFile;
	}

	public OrganisationalPolicies getOrganisationalPolicies() {
		return organisationalPolicies;
	}

	public void setOrganisationalPolicies(OrganisationalPolicies organisationalPolicies) {
		this.organisationalPolicies = organisationalPolicies;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<PolicyNode> getPolicyTreeRoots() {
		return policyTreeRoots;
	}

	public void setPolicyTreeRoots(List<PolicyNode> policyTreeRoots) {
		this.policyTreeRoots = policyTreeRoots;
	}

	public TreeHelperBean getTreeHelper() {
		return treeHelper;
	}
}
