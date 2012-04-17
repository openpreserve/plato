package eu.scape_project.pw.planning.plato.wfview.fte;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import eu.planets_project.pp.plato.exception.PlanningException;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.tree.Node;
import eu.planets_project.pp.plato.model.tree.TreeNode;
import eu.planets_project.pp.plato.validation.ValidationError;
import eu.scape_project.pw.planning.manager.StorageException;
import eu.scape_project.pw.planning.plato.bean.ExperimentStatus;
import eu.scape_project.pw.planning.plato.bean.TreeHelperBean;
import eu.scape_project.pw.planning.plato.fte.FTEvaluateAlternatives;
import eu.scape_project.pw.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.pw.planning.plato.wfview.AbstractView;
import eu.scape_project.pw.planning.utils.Downloader;

@Named("ftEvaluateAlternatives")
@ConversationScoped
public class FTEvaluateAlternativesView extends AbstractView {

	private static final long serialVersionUID = 1L;
	
	@Inject private Logger log;
	
	@Inject private FTEvaluateAlternatives evalAlternatives;
	@Inject private Downloader downloader;

	@Inject private TreeHelperBean treeHelper;

	
	private List<Alternative> alternatives;
	private List<Alternative> consideredAlternatives;
	
	private boolean autoEvaluationAvailable;
	
	private List<Leaf> leaves;
	
	/**
	 * This is a pseudo list which only contains the tree's root node 
	 */
	private List<TreeNode> treeRoot;
	

	
	private ExperimentStatus experimentStatus;

	
	public FTEvaluateAlternativesView() {
    	currentPlanState = PlanState.FTE_REQUIREMENTS_DEFINED;
    	name = "Evaluate Alternatives";
    	viewUrl = "/fte/FTevaluatealternatives.jsf";
    	
        leaves = new ArrayList<Leaf>();
	}
	
	@Override
	public void init(Plan p) {
		super.init(p);
		initLeafLists();
        // we need to show the user if there are automatically measurable criteria
        autoEvaluationAvailable = evalAlternatives.isAutoEvaluationAvailable();
		
		alternatives = p.getAlternativesDefinition().getAlternatives();
		consideredAlternatives = p.getAlternativesDefinition().getConsideredAlternatives();
		
		treeRoot = new ArrayList<TreeNode>();
		treeRoot.add(plan.getTree().getRoot());
		
		treeHelper.collapseAll();
	}

	@Override
	protected AbstractWorkflowStep getWfStep() {
		return evalAlternatives;
	}
	
	public List<Alternative> getAlternatives() {
		return alternatives;
	}
	
	public void removeAlternative(Alternative alternative) {
		evalAlternatives.removeAlternative(alternative);
		alternatives = plan.getAlternativesDefinition().getAlternatives();
		consideredAlternatives = plan.getAlternativesDefinition().getConsideredAlternatives();
	}
	
	/**
	 * @see {@link AbstractView#tryProceed(List)}
	 * 
	 * - All erroneous errorleaves are shown to the user.
	 */
	public boolean tryProceed(List<ValidationError> errors) {
		// forcing the user to approve values with "approve all" button  does not make sense
		// at least for fast track evaluation we auto-approve measured values. 
		approve();
		
		if (! super.tryProceed(errors)) { 
			leaves.clear();
			treeHelper.collapseAll();
			for (ValidationError error: errors) {
				if (error.getInvalidObject() instanceof Leaf) {
					Leaf leaf = (Leaf)error.getInvalidObject();
					treeHelper.expandNode(leaf);
					if (!leaves.contains(leaf)) {
						leaves.add(leaf);
					}
				}
			}
			return false;
		}
		return true;
	}
	
	public boolean getExecutableExperimentsAvailable(){
		// we have only added executable services as alternatives
		return consideredAlternatives.size() > 0;
	}
	
	public boolean isAutoEvaluationAvailable() {
		return autoEvaluationAvailable;
	}
	
    public void prepareExperiments() {
        experimentStatus = evalAlternatives.setupAllExperiments();
    }

    public void startExperiments(){
		experimentStatus.setStarted(true);
		evalAlternatives.startExperiments();
		log.error("Experiment started...");
	}

    public void clearExperiments() {
        experimentStatus.clear();
        evalAlternatives.clearExperiments();
    }

	/**
	 * Method responsible for starting the download of a given result file
	 * 
	 * @param alt Alternative of the wanted result file.
	 * @param sampleObj SampleObject of the wanted result file.
	 */
	public void downloadResultFile(Object alt, Object sampleObj) {
		Alternative alternative = (Alternative) alt;
		SampleObject sampleObject = (SampleObject) sampleObj;
		
		DigitalObject resultFile = null;
		
		try {
			resultFile = evalAlternatives.fetchResultFile(alternative, sampleObject);
		}
		catch (StorageException e) {
			log.error("Exception at trying to fetch result file for alternative " + alternative.getName() + "and sample " + sampleObject.getFullname(), e);
			facesMessages.addError("Unalble to fetch result-file");
		}
		
		if (resultFile != null) {
			downloader.download(resultFile);
			return;
		}
		else {
			log.debug("No result file exists for alternative " + alternative.getName() + " and sample " + sampleObject.getFullname());
		}
	}    
    
	/**
	 * Starts the download for the given sample object. 
	 * 
	 * @param object
	 */
    public void downloadSampleObject(SampleObject object) {
    	if (object == null) {
    		log.debug("No sample object provided.");
    		return;
    	}
    	try {
			DigitalObject sampleObject = evalAlternatives.fetchDigitalObject(object);
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
    public void approve() {
    	evalAlternatives.approveAllValues();
    }
    
    public void evaluateAll() {
        try {
			evalAlternatives.evaluateAll();
		} catch (PlanningException e) {
			log.error(e.getMessage(), e);
		}
    }	

    /**
     * Select a node or leaf from the tree.
     * - if a node is selected, all its children are selected too. 
     */
    public void select(TreeNode node) {
        initLeafLists();
        if (node instanceof Node) {
            leaves = node.getAllLeaves();
        } else if (node instanceof Leaf) {
            leaves.add((Leaf) node);
        }
    }    

    private void initLeafLists() {
        leaves.clear();
    }
    
	public List<Leaf> getLeaves() {
		return leaves;
	}

	public ExperimentStatus getExperimentStatus() {
		return experimentStatus;
	}
	
	public List<Alternative> getConsideredAlternatives(){
		return consideredAlternatives;
	}
	
	public List<TreeNode> getTreeRoot() {
		return treeRoot;
	}

	public TreeHelperBean getTreeHelper() {
		return treeHelper;
	}

}
