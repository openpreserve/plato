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

/**
 * Calculator for dominates measures and sets of measures.
 * 
 * @see "Improving decision support for software component selection throughsystematic cross-referencing and analysis of multiple decision criteria, Christoph Becker et al."
 */
public class DominatedSetCalculator {

    private static final Logger log = LoggerFactory.getLogger(DominatedSetCalculator.class);

    private List<PlanInfo> selectedPlans = new ArrayList<PlanInfo>();
    private HashMap<String, Set<VPlanLeaf>> leavesOfMeasures = new HashMap<String, Set<VPlanLeaf>>();

    /**
     * Creates a new DominatedSetCalculator.
     * 
     * @param selectedPlans
     *            PlanInfos to use
     * @param selectedLeaves
     *            leaves to use
     */
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

    /**
     * Calculates a set of dominated sets of measures.
     * 
     * @return a set of dominated sets
     */
    public Set<Set<String>> calculateDominatedPowerSet() {
        return isSubsetDominated(new ArrayList<String>(leavesOfMeasures.keySet()), new ArrayList<String>(), 0);
    }

    /**
     * Starting from the provided list of measures, recursively appends measures
     * beginning from position index of allMeasureUris. If a list is dominated,
     * creates a new Set and adds it to the return set.
     * 
     * @param allMeasureUris
     *            all measure URIs that should be considered
     * @param measures
     *            the current list of measures used as starting point
     * @param index
     *            the beginning index of the all measures URIs list that should
     *            be considered
     * @return a set of dominated sets
     */
    private Set<Set<String>> isSubsetDominated(final List<String> allMeasureUris, List<String> measures, final int index) {
        Set<Set<String>> dominatedSets = new HashSet<Set<String>>();

        for (int i = index; i < allMeasureUris.size(); i++) {
            measures.add(allMeasureUris.get(i));
            if (isMeasureUriListDominated(measures)) {
                dominatedSets.add(new HashSet<String>(measures));
                dominatedSets.addAll(isSubsetDominated(allMeasureUris, measures, i + 1));
            }

            measures.remove(measures.size() - 1);
        }

        return dominatedSets;
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

        for (Measure measure : measures) {
            Set<VPlanLeaf> leaves = leavesOfMeasures.get(measure.getUri());
            if (leaves != null) {
                allLeaves.addAll(leaves);
            }
        }

        return isLeafSetDominated(allLeaves);
    }

    /**
     * Checks if this set of measures is dominated.
     * 
     * @param measures
     *            measure to check
     * @return true if it is dominated, false otherwise
     */
    private boolean isMeasureUriListDominated(List<String> measureUris) {
        Set<VPlanLeaf> allLeaves = new HashSet<VPlanLeaf>();

        for (String measureUri : measureUris) {
            Set<VPlanLeaf> leaves = leavesOfMeasures.get(measureUri);
            if (leaves != null) {
                allLeaves.addAll(leaves);
            }
        }

        return isLeafSetDominated(allLeaves);
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
