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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import eu.scape_project.planning.manager.PlanManager;
import eu.scape_project.planning.manager.PlanManager.WhichProjects;
import eu.scape_project.planning.model.PlanProperties;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.User;

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

    @Inject
    private User user;

    @Inject
    PlanManager planManager;

    private List<PlanProperties> selectablePlanProperties;

    private Map<Integer, Boolean> checkedPlanProperties;

    private boolean selectAll;

    public PlanSelector() {
        selectablePlanProperties = new ArrayList<PlanProperties>();
        checkedPlanProperties = new HashMap<Integer, Boolean>();
    }

    /**
     * Method refreshing the list of selectable plans.
     * 
     * @return Outcome-string of the planselector-page.
     */
    public String init() {

        PlanManager.PlanQuery pq = planManager.createQuery();
        pq.filterMinState(PlanState.WEIGHTS_SET).filterNameUnlike("MY DEMO PLAN%")
            .filterMapped();

        if (user.isAdmin()) {
            pq.addVisibility(WhichProjects.ALLPROJECTS);
        } else {
            pq.addVisibility(WhichProjects.MYPROJECTS).addVisibility(WhichProjects.PUBLICPROJECTS);
        }

        selectablePlanProperties = planManager.list(pq);

        // make sure that for each PlanProperties entry exists a HashMap entry
        for (PlanProperties pp : selectablePlanProperties) {
            if (!checkedPlanProperties.containsKey(pp.getId())) {
                checkedPlanProperties.put(pp.getId(), false);
            }
        }

        return "planselector.jsf";
    }

    /**
     * Method responsible for applying the plan selection / setting the plans to
     * analyse.
     * 
     * @return Outcome-string of the page to view after excution.
     */
    @SuppressWarnings("unchecked")
    public String applySelection() {
        // collect selected PlanProperty-ids of the selected plans
        List<Integer> selectedPlanPropertiesIds = new ArrayList<Integer>();

        for (PlanProperties pp : selectablePlanProperties) {
            if (checkedPlanProperties.get(pp.getId())) {
                selectedPlanPropertiesIds.add(pp.getId());
            }
        }

        // fetching Plan-ids of the selecting plans
        List<Integer> selectedPlanIds = new ArrayList<Integer>();

        if (selectedPlanPropertiesIds.size() > 0) {
            selectedPlanIds = (List<Integer>) em
                .createQuery(
                    "SELECT p.id FROM Plan p JOIN p.planProperties pp WHERE pp.id IN :selectedPlanPropertiesIds")
                .setParameter("selectedPlanPropertiesIds", selectedPlanPropertiesIds).getResultList();
        }

        planSelection.selectPlans(selectedPlanIds);

        return "index.jsf";
    }

    /**
     * Updates the select all flag according to the selected plans.
     */
    public void planSelectionChanged() {
        boolean allPlansSelected = true;
        for (Entry<Integer, Boolean> checkedPlanProperty : checkedPlanProperties.entrySet()) {
            if (!checkedPlanProperty.getValue()) {
                allPlansSelected = false;
            }
        }
        selectAll = allPlansSelected;
    }

    /**
     * Updates the selected plans according to the status of select all.
     */
    public void selectAllChanged() {
        selectAll = !selectAll;

        for (Entry<Integer, Boolean> checkedPlanProperty : checkedPlanProperties.entrySet()) {
            checkedPlanProperty.setValue(selectAll);
        }
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

    public boolean isSelectAll() {
        return selectAll;
    }

    public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
    }
}
