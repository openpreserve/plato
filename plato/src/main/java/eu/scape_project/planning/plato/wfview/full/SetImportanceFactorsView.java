package eu.scape_project.planning.plato.wfview.full;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.tree.TreeNode;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.SetImportanceFactors;
import eu.scape_project.planning.plato.wfview.AbstractView;

/**
 * Backing bean for workflow step Set Importance Factors
 * 
 * The objective tree is shown to the user, who can enter a weight for each node and leaf. 
 * When the user enters a weight factor all other siblings are automatically adjusted so that the
 * sum equals to 1.0
 * 
 * @author Michael Kraxner, Markus Hamm
 *
 */
@Named("setImportanceFactors")
@ConversationScoped
public class SetImportanceFactorsView extends AbstractView {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Helper class mapping alternative names to transformed and aggregated result values.
	 * - this way we can easily provide a {@link SetImportanceFactorsView#criterionResultMap mapping} from criteria to these values   
	 *   which simplifies displaying these values beside each leaf.
	 * 
	 * @author Michael Kraxner
	 *
	 */
	public class ResultMap implements Serializable{
	    private static final long serialVersionUID = 1L;
	    
	    private HashMap<String,Double> results = new HashMap<String,Double>();
	    
	    public HashMap<String, Double> getResults() {
	        return results;
	    }

	    public void setResults(HashMap<String, Double> results) {
	        this.results = results;
	    }
	}	
	
	@Inject private SetImportanceFactors setImportanceFactors;
	
	/**
	 * The currently selected root node 
	 */
	private List<TreeNode> focusedNode;
	
	/**
	 * Determines if weights are balanced automatically.
	 */
	private boolean autoBalancingEnabled;
	
	/**
	 * Maps leafs to transformed and aggregated results per alternative.
	 */
    private HashMap<Leaf, ResultMap> criterionResultMap;


	public SetImportanceFactorsView() {
    	currentPlanState = PlanState.TRANSFORMATION_DEFINED;
    	name = "Set Importance Factors";
    	viewUrl = "/plan/setimportancefactors.jsf";
    	group = "menu.analyseResults";
    	
    	focusedNode = new ArrayList<TreeNode>();
    	criterionResultMap = new HashMap<Leaf,ResultMap>();
	}
	
	public void init(Plan plan){
		super.init(plan);
		autoBalancingEnabled = true;
		
        
        // fill our temporary result map with all the result values
        criterionResultMap.clear();
        for (Leaf l : plan.getTree().getRoot().getAllLeaves()) {
            HashMap<String,Double> map = new HashMap<String,Double>();
            for (Alternative a: plan.getAlternativesDefinition().getConsideredAlternatives()) {
                map.put(a.getName(), l.getResult(a));
            }
            ResultMap m = new ResultMap();
            m.setResults(map);
            criterionResultMap.put(l,m);
        }
		
		
		resetFocus();
		
	}

	@Override
	protected AbstractWorkflowStep getWfStep() {
		return setImportanceFactors;
	}

	public boolean isAutoBalancingEnabled() {
		return autoBalancingEnabled;
	}

	public void setAutoBalancingEnabled(boolean autoBalancingEnabled) {
		this.autoBalancingEnabled = autoBalancingEnabled;
	}
	
	/**
	 * Resets the focused node to the root of the objective tree.
	 */
	public void resetFocus(){
		focusedNode.clear();
		focusedNode.add(plan.getTree().getRoot());
	}
	
	public void focusOn(TreeNode node) {
		//focusedNode.clear();
		focusedNode = new ArrayList<TreeNode>();
		focusedNode.add(node);
	}

	public List<TreeNode> getFocusedNode() {
		return focusedNode;
	}

	public HashMap<Leaf, ResultMap> getCriterionResultMap() {
		return criterionResultMap;
	}
}
