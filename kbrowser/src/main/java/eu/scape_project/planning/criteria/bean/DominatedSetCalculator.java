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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.scape_project.planning.model.kbrowser.VPlanLeaf;
import eu.scape_project.planning.model.measurement.Measure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DominatedSetCalculator {

    private static final Logger log = LoggerFactory.getLogger(DominatedSetCalculator.class);

    private List<PlanInfo> selectedPlans = new ArrayList<PlanInfo>();
    private HashMap<String, Set<VPlanLeaf>> leavesOfMeasures = new HashMap<String, Set<VPlanLeaf>>();

    public DominatedSetCalculator(List<PlanInfo> selectedPlans, List<VPlanLeaf> selectedLeaves) {
        this.selectedPlans = selectedPlans;
        fillCriterionVPlanLeaves(selectedLeaves);
    }

    /**
     * Fills the map of used VPlanLeaves per criterion.
     */
    private void fillCriterionVPlanLeaves(List<VPlanLeaf> selectedLeaves) {
        for (VPlanLeaf leaf : selectedLeaves) {
            if (leaf.isMapped()) {
                Set<VPlanLeaf> leaves = leavesOfMeasures.get(leaf.getMeasure().getUri());

                if (leaves == null) {
                    leaves = new HashSet<VPlanLeaf>();
                    leavesOfMeasures.put(leaf.getMeasure().getUri(), leaves);
                }
                leaves.add(leaf);
            }
        }
    }

    // public void calculateDominatedPowerSet() {
    // isSubsetDominated(new
    // ArrayList<Criterion>(criterionVPlanLeaves.keySet()), new
    // ArrayList<Criterion>(), 0);
    // }

    public boolean isSubsetDominated(List<Measure> allMeasures, List<Measure> measures, int index) {

        boolean isSubsetDominated = false;

        for (int i = index; i < allMeasures.size(); i++) {
            Set<Measure> tmp = new HashSet<Measure>(measures);
            tmp.add(allMeasures.get(i));

            if (isCriterionSetDominated(tmp)) {
                isSubsetDominated = true;
                List<Measure> tmp2 = new ArrayList<Measure>(tmp);
                if (!isSubsetDominated(allMeasures, tmp2, i + 1)) {

                    logDominated(tmp);

                }
            }
        }

        return isSubsetDominated;
    }

    private void logDominated(Set<Measure> measures) {
        String logString = "Set [";
        for (Measure measure : measures) {
            logString += measure.getUri() + ", ";
        }
        log.info(logString + "]: dominated");
    }

    /**
     * Checks if the measure is dominated.
     * 
     * @param measure
     *            the measure to check
     * @return true if it is dominated, false otherwise
     */
    public boolean isCriterionDominated(Measure measure) {
        Set<VPlanLeaf> leaves = leavesOfMeasures.get(measure.getUri());

        if (leaves == null) {
            return false;
        }

        return isLeafSetDominated(leaves);
    }

    /**
     * Checks if this set of measures is dominated.
     * 
     * @param measures
     *            measure to check
     * @return true if it is dominated, false otherwise
     */
    public boolean isCriterionSetDominated(Set<Measure> measures) {
        Set<VPlanLeaf> allLeaves = new HashSet<VPlanLeaf>();

        String logString = "Set [";
        for (Measure measure : measures) {
            logString += measure.getUri() + ", ";
            Set<VPlanLeaf> leaves = leavesOfMeasures.get(measure.getUri());
            if (leaves != null) {
                allLeaves.addAll(leaves);
            }
        }

        boolean isCriterionSetDominated = isLeafSetDominated(allLeaves);

        if (isCriterionSetDominated) {
            log.info(logString + "]: dominated");
        }

        return isCriterionSetDominated;
    }

    /**
     * Checks if the provided leaves are dominated. This is true if for every
     * plan, the leaves are dominated. (@see isLeafSetDominated(PlanInfo,
     * Set<VPlanLeaf>)).
     * 
     * @param leafSet
     *            set of leaves
     * @return true if the leaves are dominated, false otherwise
     */
    private boolean isLeafSetDominated(Set<VPlanLeaf> leafSet) {

        Map<PlanInfo, Set<VPlanLeaf>> leavesOfPlans = new HashMap<PlanInfo, Set<VPlanLeaf>>();

        for (VPlanLeaf leaf : leafSet) {
            if (leaf.hasKOPotential()) {
                return false;
            }

            // Add leaves to plan map
            PlanInfo planInfo = getSelectedPlanInfo(leaf.getPlanId());
            Set<VPlanLeaf> leavesOfPlan = leavesOfPlans.get(planInfo);
            if (leavesOfPlan == null) {
                leavesOfPlan = new HashSet<VPlanLeaf>();
                leavesOfPlans.put(planInfo, leavesOfPlan);
            }
            leavesOfPlan.add(leaf);
        }

        // Check if leaf set is dominated for the plans
        for (Map.Entry<PlanInfo, Set<VPlanLeaf>> entry : leavesOfPlans.entrySet()) {
            if (!isLeafSetDominated(entry.getKey(), entry.getValue())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the set of leaves is dominated for this plan. This is true if
     * the sum of all changes this set of leaves can have in the worst case for
     * this plan would have an influence on the ranking.
     * 
     * @param planInfo
     *            plan info for the plan
     * @param leafSet
     *            leaves of the plan
     * @return true if the leaves are dominated, false otherwise
     */
    private boolean isLeafSetDominated(PlanInfo planInfo, Set<VPlanLeaf> leafSet) {
        // Overall result of each alternative
        Map<String, Double> alternativePlanResults = planInfo.getOverallResults().getResults();

        // Maximum change
        double maximumChange = calculateMaximumChange(planInfo, leafSet);

        // Get smallest difference
        List<Double> sortResults = new ArrayList<Double>(alternativePlanResults.values());
        Collections.sort(sortResults);

        double last = Double.NEGATIVE_INFINITY;
        double minimumDifference = Double.POSITIVE_INFINITY;
        for (double d : sortResults) {
            double diff = d - last;
            if (diff < minimumDifference) {
                minimumDifference = diff;
            }
            last = d;
        }

        String logString = "Plan " + planInfo.getId() + ", Set [";
        for (VPlanLeaf vPlanLeaf : leafSet) {
            logString += vPlanLeaf.getMeasure().getUri() + ", ";
        }

        boolean isLeafSetDominated = maximumChange < minimumDifference;
        if (isLeafSetDominated) {
            log.info(logString + "]: dominated");
        }

        // Is it dominated?
        return maximumChange < minimumDifference;
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

        // FIXME: Needed? Probably KO leafs are filtered earlyer but might still
        // make sense.
        if (vPlanLeaf.hasKOPotential()) {
            return Double.POSITIVE_INFINITY;
        }

        // Criterion result of each alternative
        Map<String, Double> alternativeCriterionResults = vPlanLeaf.getAlternativeResultsAsMap();

        double maximumDecrease = 0.0d;
        double maximumIncrease = 0.0d;

        // Loop over alternatives and their values for this criterion
        for (Map.Entry<String, Double> entry : alternativeCriterionResults.entrySet()) {
            if (entry.getKey().equals(planInfo.getWinningAlternative())) {
                maximumDecrease = (entry.getValue() - vPlanLeaf.getPotentialMinimum()) * vPlanLeaf.getTotalWeight();
            } else {
                double increase = (vPlanLeaf.getPotentialMaximum() - entry.getValue()) * vPlanLeaf.getTotalWeight();
                if (increase > maximumIncrease) {
                    maximumIncrease = increase;
                }
            }
        }

        return maximumIncrease + maximumDecrease;
    }

    /**
     * Returns the plan with the given ID.
     * 
     * @param id
     * @return
     */
    private PlanInfo getSelectedPlanInfo(int id) {
        for (PlanInfo planInfo : selectedPlans) {
            if (planInfo.getId() == id) {
                return planInfo;
            }
        }

        return null;
    }

}
