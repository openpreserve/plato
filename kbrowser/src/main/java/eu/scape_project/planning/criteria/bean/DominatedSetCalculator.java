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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.scape_project.planning.model.kbrowser.CriteriaLeaf;
import eu.scape_project.planning.model.kbrowser.VPlanLeaf;
import eu.scape_project.planning.model.measurement.Criterion;

public class DominatedSetCalculator {

    private List<PlanInfo> selectedPlans = new ArrayList<PlanInfo>();

    public DominatedSetCalculator() {
    }

    public Set<Set<Criterion>> calculateDominated(Set<CriteriaLeaf> criteriaLeafs) {

        return null;
    }

    private void getTest(Set<CriteriaLeaf> criteriaLeafs) {

        // Map<PlanInfo, Set<VPlanLeaf>> planLeafs = new HashMap<PlanInfo,
        // Set<VPlanLeaf>>();
        //
        // for (CriteriaLeaf criteriaLeaf : criteriaLeafs) {
        // List<VPlanLeaf> vPlanLeaves = criteriaLeaf.getPlanLeaves();
        //
        // for (VPlanLeaf vPlanLeaf : vPlanLeaves) {
        // PlanInfo planInfo = getSelectedPlanInfo(vPlanLeaf.getPlanId());
        // Set<VPlanLeaf> vPlanLeaves = planLeafs.get();
        // }
        // }
    }

    private PlanInfo getSelectedPlanInfo(int id) {
        for (PlanInfo planInfo : selectedPlans) {
            if (planInfo.getId() == id) {
                return planInfo;
            }
        }

        return null;
    }

    /**
     * Calculates the maximum impact this set of criteria could have on the
     * overall output of the plan if the current value changes.
     * 
     * @param planInfo
     * @param vPlanLeafs
     * @return
     */
    private double calculateMaximumChange(PlanInfo planInfo, Set<VPlanLeaf> vPlanLeafs) {
        double maximumChange = 0.0d;

        for (VPlanLeaf vPlanLeaf : vPlanLeafs) {
            maximumChange += calculateMaximumChange(planInfo, vPlanLeaf);
        }

        return maximumChange;
    }

    /**
     * Calculates the maximum impact this criterion could have on the overall
     * output of the plan if the current value is changes.
     * 
     * @param planInfo
     * @param vPlanLeaf
     * @return
     */
    private double calculateMaximumChange(PlanInfo planInfo, VPlanLeaf vPlanLeaf) {

        // Criterion result of each alternative
        Map<String, Double> alternativeCriterionResults = vPlanLeaf.getAlternativeResultsAsMap();
        // Overall result of each alternative
        Map<String, Double> alternativePlanResults = planInfo.getOverallResults().getResults();

        Map.Entry<String, Double> winningAlternative = findWinningAlternative(alternativePlanResults);

        // Map<String, Double> worstCaseResults = new HashMap<String, Double>();

        double maximumDecrease = 0.0d;
        double maximumIncrease = 0.0d;

        // Loop over alternatives and their values for this criterion
        for (Map.Entry<String, Double> entry : alternativeCriterionResults.entrySet()) {
            if (entry.getKey().equals(winningAlternative.getKey())) {
                // worstCaseResults.put(entry.getKey(),
                // vPlanLeaf.getPotentialMinimum());
                maximumDecrease = (entry.getValue() - vPlanLeaf.getPotentialMinimum()) * vPlanLeaf.getTotalWeight();
            } else {
                // worstCaseResults.put(entry.getKey(),
                // vPlanLeaf.getPotentialMaximum());
                double increase = (vPlanLeaf.getPotentialMaximum() - entry.getValue()) * vPlanLeaf.getTotalWeight();
                if (increase > maximumIncrease) {
                    maximumIncrease = increase;
                }
            }
        }

        return maximumIncrease + maximumDecrease;
    }

    /**
     * Finds the winning alternative
     * 
     * @param alternatives
     *            the alternatives with their values
     * @return the winning alternative
     */
    private Map.Entry<String, Double> findWinningAlternative(Map<String, Double> alternatives) {
        Map.Entry<String, Double> winningAlternative = null;
        Double minValue = Double.MIN_VALUE;
        for (Map.Entry<String, Double> entry : alternatives.entrySet()) {
            if (entry.getValue() > minValue) {
                minValue = entry.getValue();
                winningAlternative = entry;
            }
        }

        return winningAlternative;
    }

    public List<PlanInfo> getSelectedPlans() {
        return selectedPlans;
    }

    public void setSelectedPlans(List<PlanInfo> selectedPlans) {
        this.selectedPlans = selectedPlans;
    }
}
