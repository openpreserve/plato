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
package eu.scape_project.planning.criteria.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.scape_project.planning.model.kbrowser.VPlanLeaf;

/**
 * Class responsible for encapsulating and manage the plans (and its corresnponding VPlanLeaves) to analyse.
 * 
 * @author Markus Hamm
 */
@SessionScoped
public class PlanSelection implements Serializable {
	private static final long serialVersionUID = -7377213145258191845L;

	@Inject
	private EntityManager em;
	
	/**
	 * Ids of plans selected (to analyse).
	 */
	private List<Integer> selectedPlans;
	
	/**
	 * VPlanLeaves corresponding to plan selection (to analyse).
	 */
	private List<VPlanLeaf> selectionPlanLeaves;
	
	public PlanSelection() {
		selectedPlans = new ArrayList<Integer>();
		selectionPlanLeaves = new ArrayList<VPlanLeaf>();
	}
		
	/**
	 * Method responsible for selecting a range of plans for analysing.
	 * All corresponding instance variables are updated based on this selection.
	 * 
	 * @param planIds List of selected plan-ids.
	 */
	@SuppressWarnings("unchecked")
	public void selectPlans(List<Integer> planIds) {
		selectedPlans = planIds;
		selectionPlanLeaves = new ArrayList<VPlanLeaf>();
		
		if (selectedPlans.size() > 0) {
			selectionPlanLeaves = (List<VPlanLeaf>) em.createQuery("SELECT l from VPlanLeaf l WHERE l.planId IN (:selectedPlans)")
					.setParameter("selectedPlans", selectedPlans)
					.getResultList();
		}
	}
	
	public List<Integer> getSelectedPlans() {
		return selectedPlans;
	}

	public List<VPlanLeaf> getSelectionPlanLeaves() {
		return selectionPlanLeaves;
	}
}
