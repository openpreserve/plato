package eu.scape_project.pw.planning.plato.wfview.full;

import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.PlanState;
import eu.scape_project.pw.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.pw.planning.plato.wf.DevelopExperiments;
import eu.scape_project.pw.planning.plato.wfview.AbstractView;

/**
 * Class used as backing-bean for the view developexperiments.xhtml
 * 
 * @author Markus Hamm
 */
@Named("developExperiments")
@ConversationScoped
public class DevelopExperimentsView extends AbstractView {
	private static final long serialVersionUID = 1L;

	@Inject private Logger log;
	
	@Inject private DevelopExperiments developExperiments;
	
	private List<Alternative> alternatives;
	
	public DevelopExperimentsView() {
    	currentPlanState = PlanState.GO_CHOSEN;
    	name = "Develop Experiments";
    	viewUrl = "/plan/developexperiments.jsf";
    	group = "menu.evaluateAlternatives";    	
	}
	
	public void init(Plan plan) {
    	super.init(plan);
    	alternatives = plan.getAlternativesDefinition().getAlternatives();
	}
	
	public List<Alternative> getAlternatives() {
		return alternatives;
	}

	public void setAlternatives(List<Alternative> alternatives) {
		this.alternatives = alternatives;
	}

	@Override
	protected AbstractWorkflowStep getWfStep() {
		return developExperiments;
	}
}
