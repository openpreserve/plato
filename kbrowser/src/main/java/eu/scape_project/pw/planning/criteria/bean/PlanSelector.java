package eu.scape_project.pw.planning.criteria.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanProperties;
import eu.scape_project.planning.model.kbrowser.VPlanLeaf;

/**
 * Class responsible for selecting and setting the plans to analyse. 
 * 
 * @author Markus Hamm
 */
@Named("planSelector")
@SessionScoped
public class PlanSelector implements Serializable {
	private static final long serialVersionUID = 5583551676744611587L;

	@Inject
	private EntityManager em;
	
	@Inject 
	PlanSelection planSelection;
	
	private List<PlanProperties> selectablePlanProperties;
	
	private Map<Integer, Boolean> checkedPlanProperties;
		
	public PlanSelector() {
		selectablePlanProperties = new ArrayList<PlanProperties>();
		checkedPlanProperties = new HashMap<Integer, Boolean>();
	}
	
	/**
	 * Method refreshing the list of selectable plans.
	 * 
	 * @return Outcome-string of the planselector-page.
	 */
	@SuppressWarnings("unchecked")
	public String init() {
		// TODO: Display only public plans (this was deactivated for all-staff/review presentations)
		// Add to WHERE clause: WHERE pp.privateProject=false AND ...
		selectablePlanProperties = (List<PlanProperties>) em.createQuery(
				"SELECT pp FROM PlanProperties pp WHERE pp.state in ('WEIGHTS_SET', 'ANALYSED', 'EXECUTEABLE_PLAN_CREATED','PLAN_DEFINED', 'PLAN_VALIDATED')  AND pp.name NOT LIKE 'MY DEMO PLAN%'")
				.getResultList();
		
		// make sure that for each PlanProperties entry exists a HashMap entry
		for (PlanProperties pp : selectablePlanProperties) {
			if (!checkedPlanProperties.containsKey(pp.getId())) {
				checkedPlanProperties.put(pp.getId(), false);
			}
		}
		
		return "planselector.jsf";
	}

	/**
	 * Method responsible for applying the plan selection / setting the plans to analyse.
	 * 
	 * @return Outcome-string of the page to view after excution.
	 */
	@SuppressWarnings("unchecked")
	public String applySelection() {
		// collect selected PlanProperty-ids of the selected plans
		List<Integer> selectedPlanPropertiesIds = new ArrayList<Integer>();
		
		for(PlanProperties pp : selectablePlanProperties) {
			if (checkedPlanProperties.get(pp.getId())) {
				selectedPlanPropertiesIds.add(pp.getId());
			}
		}

		// fetching Plan-ids of the selecting plans
		List<Integer> selectedPlanIds = new ArrayList<Integer>();
		
		if (selectedPlanPropertiesIds.size() > 0) {
			selectedPlanIds = (List<Integer>) em.createQuery(
					"SELECT p.id FROM Plan p JOIN p.planProperties pp WHERE pp.id IN :selectedPlanPropertiesIds")
					.setParameter("selectedPlanPropertiesIds", selectedPlanPropertiesIds)
					.getResultList();
		}
		
		planSelection.selectPlans(selectedPlanIds);
		
		return "index.jsf";
	}
		
	// --------------- getter/setter ---------------	

	public List<PlanProperties> getSelectablePlanProperties() {
		return selectablePlanProperties;
	}

	public void setSelectablePlanProperties(List<PlanProperties> selectablePlanProperties) {
		this.selectablePlanProperties = selectablePlanProperties;
	}

	public Map<Integer, Boolean> getCheckedPlanProperties() {
		return checkedPlanProperties;
	}

	public void setCheckedPlanProperties(Map<Integer, Boolean> checkedPlanProperties) {
		this.checkedPlanProperties = checkedPlanProperties;
	}
}
