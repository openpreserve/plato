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

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.manager.StorageException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.tree.Node;
import eu.scape_project.planning.model.tree.TreeNode;
import eu.scape_project.planning.plato.bean.ExperimentStatus;
import eu.scape_project.planning.plato.bean.TreeHelperBean;
import eu.scape_project.planning.plato.fte.FTEvaluateAlternatives;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wfview.AbstractView;
import eu.scape_project.planning.utils.Downloader;
import eu.scape_project.planning.validation.ValidationError;

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
		log.info("Experiment started...");
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
