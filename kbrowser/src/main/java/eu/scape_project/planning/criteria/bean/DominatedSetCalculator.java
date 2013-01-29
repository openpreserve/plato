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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.scape_project.planning.model.kbrowser.VPlanLeaf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calculator for dominates measures and sets of measures.
 * 
 * @see "Improving decision support for software component selection throughsystematic cross-referencing and analysis of multiple decision criteria, Christoph Becker et al."
 */
public class DominatedSetCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(DominatedSetCalculator.class);

    private List<PlanInfo> selectedPlans = new ArrayList<PlanInfo>();
    private HashMap<String, Map<PlanInfo, Set<VPlanLeaf>>> leavesOfMeasures = new HashMap<String, Map<PlanInfo, Set<VPlanLeaf>>>();

    /**
     * Creates a new DominatedSetCalculator.
     * 
     * @param selectedPlans
     *            PlanInfos to use
     * @param selectedLeaves
     *            leaves to use
     */
    public DominatedSetCalculator(final List<PlanInfo> selectedPlans, final List<VPlanLeaf> selectedLeaves) {
        this.selectedPlans = selectedPlans;
        fillCriterionVPlanLeaves(selectedLeaves);
    }

    /**
     * Fills the map of used VPlanLeaves per criterion.
     * 
     * @param selectedLeaves
     *            the leaves to store
     */
    private void fillCriterionVPlanLeaves(final List<VPlanLeaf> selectedLeaves) {
        for (VPlanLeaf leaf : selectedLeaves) {
            if (leaf.isMapped()) {
                Map<PlanInfo, Set<VPlanLeaf>> leavesOfPlans = leavesOfMeasures.get(leaf.getMeasure().getUri());
                if (leavesOfPlans == null) {
                    leavesOfPlans = new HashMap<PlanInfo, Set<VPlanLeaf>>();
                    leavesOfMeasures.put(leaf.getMeasure().getUri(), leavesOfPlans);
                }

                PlanInfo planInfo = getSelectedPlanInfo(leaf.getPlanId());
                Set<VPlanLeaf> leavesOfPlan = leavesOfPlans.get(planInfo);
                if (leavesOfPlan == null) {
                    leavesOfPlan = new HashSet<VPlanLeaf>();
                    leavesOfPlans.put(planInfo, leavesOfPlan);
                }

                leavesOfPlan.add(leaf);
            }
        }
    }

    /**
     * Calculates a set of dominated sets of measures.
     * 
     * @return a set of dominated sets
     */
    public Set<Set<String>> getDominatedPowerSet() {
        return getDominatedSubsets(new ArrayList<String>(leavesOfMeasures.keySet()), new ArrayList<String>(
            leavesOfMeasures.keySet().size()), 0);
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
    private Set<Set<String>> getDominatedSubsets(final List<String> allMeasureUris, List<String> measures,
        final int index) {
        Set<Set<String>> dominatedSets = new HashSet<Set<String>>();

        for (int i = index; i < allMeasureUris.size(); i++) {
            measures.add(allMeasureUris.get(i));
            if (isLeafSetDominated(getLeavesOfMeasureUris(measures))) {
                Set<Set<String>> dominatedSubsets = getDominatedSubsets(allMeasureUris, measures, i + 1);
                if (dominatedSubsets.size() > 0) {
                    dominatedSets.addAll(dominatedSubsets);
                } else {
                    dominatedSets.add(new HashSet<String>(measures));
                }
            }
            measures.remove(measures.size() - 1);
        }

        return dominatedSets;
    }

    /**
     * Checks if the measure is dominated.
     * 
     * @param measureUri
     *            the measure to check
     * @return true if dominated, false otherwise
     */
    public boolean isCriterionDominated(final String measureUri) {
        Map<PlanInfo, Set<VPlanLeaf>> leaves = leavesOfMeasures.get(measureUri);

        if (leaves == null) {
            return true;
        }

        return isLeafSetDominated(leaves);
    }

    /**
     * Checks if this set of measures is dominated.
     * 
     * @param measureUris
     *            measure URIs to check
     * @return true if dominated, false otherwise
     */
    public boolean isCriterionSetDominated(final Set<String> measureUris) {
        return isLeafSetDominated(getLeavesOfMeasureUris(measureUris));
    }

    /**
     * Returns plans that are dominated for this set of leaves.
     * 
     * @param measureUris
     *            measures URIs to check
     * @return a set of dominated plans
     */
    public Set<PlanInfo> getDominatedPlans(final Set<String> measureUris) {
        Set<PlanInfo> dominatedPlans = new HashSet<PlanInfo>();

        Map<PlanInfo, Set<VPlanLeaf>> planLeaves = getLeavesOfMeasureUris(measureUris);

        // Check if leaf set is dominated for the plans
        for (Map.Entry<PlanInfo, Set<VPlanLeaf>> entry : planLeaves.entrySet()) {
            if (isLeafSetDominated(entry.getKey(), entry.getValue())) {
                dominatedPlans.add(entry.getKey());
            }
        }

        return dominatedPlans;
    }

    /**
     * Checks if the leaves are dominated for the plans.
     * 
     * @param planLeaves
     *            plans and corresponding leaves
     * @return true if dominated, false otherwise
     */
    private boolean isLeafSetDominated(final Map<PlanInfo, Set<VPlanLeaf>> planLeaves) {
        for (Map.Entry<PlanInfo, Set<VPlanLeaf>> entry : planLeaves.entrySet()) {
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
     * @return true if dominated, false otherwise
     */
    private boolean isLeafSetDominated(final PlanInfo planInfo, final Set<VPlanLeaf> leafSet) {

        for (VPlanLeaf leaf : leafSet) {
            if (leaf.hasKOPotential()) {
                return false;
            }
        }

        // Overall result of each alternative
        Map<String, Double> alternativePlanResults = planInfo.getOverallResults().getResults();

        // Maximum change
        double maxChange = calculateMaxWinningChange(planInfo, leafSet);

        // Get smallest difference
        List<Double> sortResults = new ArrayList<Double>(alternativePlanResults.values());
        Collections.sort(sortResults);

        double last = Double.NEGATIVE_INFINITY;
        double minDifference = Double.POSITIVE_INFINITY;
        for (double d : sortResults) {
            double diff = d - last;
            if (diff < minDifference) {
                minDifference = diff;
            }
            last = d;
        }

        // Is it dominated?
        return maxChange < minDifference;
    }

    /**
     * Calculates the maximum weighted change the provided leaves could have if
     * the winning alternative would be set to the potential minimum and the
     * other alternatives would be set to the potential maximum of the leaf.
     * 
     * @param planInfo
     *            plan info
     * @param leafSet
     *            set of leaves
     * @return the maximum change the set of leaves can have
     */
    private double calculateMaxWinningChange(final PlanInfo planInfo, final Set<VPlanLeaf> leafSet) {
        double maximumChange = 0.0d;

        for (VPlanLeaf vPlanLeaf : leafSet) {
            maximumChange += calculateMaxWinningChange(planInfo, vPlanLeaf);
        }

        return maximumChange;
    }

    /**
     * Calculates the maximum weighted change the provided leaf could have if
     * the winning alternative would be set to the potential minimum and the
     * other alternatives would be set to the potential maximum of the leaf.
     * 
     * @param planInfo
     *            plan info
     * @param leaf
     *            leaf to calculate
     * @return the maximum change this leaf can have
     */
    private double calculateMaxWinningChange(final PlanInfo planInfo, final VPlanLeaf leaf) {

        if (leaf.hasKOPotential()) {
            return Double.POSITIVE_INFINITY;
        }

        // Criterion result of each alternative
        Map<String, Double> alternativeResults = leaf.getAlternativeResultsAsMap();

        double winningResult = alternativeResults.get(planInfo.getWinningAlternative());
        double minResult = 0.0d;

        // Get maximum value of non-winning alternative
        for (Map.Entry<String, Double> entry : alternativeResults.entrySet()) {
            if (!entry.getKey().equals(planInfo.getWinningAlternative()) && entry.getValue() < minResult) {
                minResult = entry.getValue();
            }
        }

        return ((leaf.getPotentialMaximum() - minResult) + (winningResult - leaf.getPotentialMinimum()))
            * leaf.getTotalWeight();
    }

    /**
     * Checks the powerset of measures for potential to change the ranking of
     * plans of this calculator.
     * 
     * @return the largest sets of measures
     */
    public List<List<String>> getRankingChangedPowerSet() {
        return getRankChangingSupersets(new ArrayList<String>(leavesOfMeasures.keySet()), 0,
            new ArrayList<List<String>>(0), new ArrayList<String>(leavesOfMeasures.keySet().size()));
    }

    /**
     * Checks subsets of all measures for potential to change the ranking for
     * plans of this calculator. Recursively checks all measures, starting from
     * index.
     * 
     * Note: For performance reasons the sets are stored in lists. Thus
     * allMeasureUris should not duplicates.
     * 
     * @param allMeasureUris
     *            all measures that should be considered
     * @param index
     *            the beginning index of all measures
     * @param currentRankChangingSets
     *            rank-changing sets already found for current measures
     * @param currentMeasures
     *            the current list of measures used as starting point
     * @return
     */
    private List<List<String>> getRankChangingSupersets(final List<String> allMeasureUris, final int index,
        final List<List<String>> currentRankChangingSets, List<String> currentMeasures) {
        List<List<String>> rankChangingSets = new ArrayList<List<String>>(currentRankChangingSets);

        for (int i = index; i < allMeasureUris.size(); i++) {
            currentMeasures.add(allMeasureUris.get(i));
            // Check if current measures are already in rank-changing sets
            List<List<String>> setsOfCurrentMeasures = getSetsContainingMeasure(rankChangingSets, allMeasureUris.get(i));
            if (setsOfCurrentMeasures.size() > 0) {
                // Current measures is already in rank-changing sets, check
                // supersets
                List<List<String>> setsOfSuperMeasures = getRankChangingSupersets(allMeasureUris, i + 1,
                    setsOfCurrentMeasures, currentMeasures);
                // Add rank-changing supersets
                addNewSets(setsOfCurrentMeasures, setsOfSuperMeasures, rankChangingSets);
            } else {
                // Current measures not yet in rank-changing sets, check if
                // dominated
                if (!doesLeafSetChangeRanking(getLeavesOfMeasureUris(currentMeasures))) {
                    // Rank-changing, check supersets
                    List<List<String>> setsOfSuperMeasures = getRankChangingSupersets(allMeasureUris, i + 1,
                        setsOfCurrentMeasures, currentMeasures);
                    if (setsOfSuperMeasures.size() > setsOfCurrentMeasures.size()) {
                        // Rank-changing supersets, add them
                        addNewSets(setsOfCurrentMeasures, setsOfSuperMeasures, rankChangingSets);
                    } else {
                        // No dominated supersets, add current measures
                        rankChangingSets.add(new ArrayList<String>(currentMeasures));
                    }
                }
            }
            currentMeasures.remove(currentMeasures.size() - 1);
        }

        return rankChangingSets;
    }

    /**
     * Returns a list of sets that contain the provided measureUri.
     * 
     * @param sets
     *            the list of sets to search
     * @param measureUri
     *            measure URI to search for
     * @return a list of sets
     */
    private List<List<String>> getSetsContainingMeasure(final List<List<String>> sets, final String measureUri) {
        List<List<String>> foundSets = new ArrayList<List<String>>(sets.size());
        for (List<String> set : sets) {
            if (set.contains(measureUri)) {
                foundSets.add(set);
            }
        }
        return foundSets;
    }

    /**
     * Takes an old set and the corresponding superset and adds all elements of
     * the superset not contained in the old set to target.
     * 
     * Note: The sets are stored as lists, the superset must contain the old set
     * at the beginning.
     * 
     * @param oldSet
     *            the old set
     * @param superSet
     *            superset of the old set
     * @param target
     *            target list
     */
    private void addNewSets(final List<List<String>> oldSet, final List<List<String>> superSet,
        List<List<String>> target) {
        for (int i = oldSet.size(); i < superSet.size(); i++) {
            target.add(superSet.get(i));
        }
    }

    /**
     * Checks if the provided measure could change the ranking of alternatives.
     * 
     * @param measureUri
     *            URI of measure to check
     * @return true if the leaves could change the ranking, false otherwise
     */
    public boolean doesCriterionChangeRanking(final String measureUri) {
        Map<PlanInfo, Set<VPlanLeaf>> leavesOfPlans = leavesOfMeasures.get(measureUri);

        if (leavesOfPlans == null) {
            return true;
        }

        return doesLeafSetChangeRanking(leavesOfPlans);
    }

    /**
     * Checks if the provided measures could change the ranking of alternatives.
     * 
     * @param measureUris
     *            URIs of measures to check
     * @return true if the leaves could change the ranking, false otherwise
     */
    public boolean doesCriterionSetChangeRanking(Set<String> measureUris) {
        return doesLeafSetChangeRanking(getLeavesOfMeasureUris(measureUris));
    }

    /**
     * Checks if the provided leaves could influence the ranking of alternatives
     * for any plan.
     * 
     * @param leavesOfPlans
     *            plans with corresponding leaves
     * @return true if the leaves could change the ranking, false otherwise
     */
    private boolean doesLeafSetChangeRanking(final Map<PlanInfo, Set<VPlanLeaf>> leavesOfPlans) {
        for (Map.Entry<PlanInfo, Set<VPlanLeaf>> leavesOfPlan : leavesOfPlans.entrySet()) {
            if (doesLeafSetChangeRanking(leavesOfPlan.getKey(), leavesOfPlan.getValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the provided leaves could influence the ranking of
     * alternatives.
     * 
     * @param planInfo
     *            plan info
     * @param leafSet
     *            leaves to check
     * @return true if the leaves could change the ranking, false otherwise
     */
    private boolean doesLeafSetChangeRanking(final PlanInfo planInfo, final Set<VPlanLeaf> leafSet) {
        Map<String, Double> results = planInfo.getOverallResults().getResults();
        Map<String, Double> lowerBounds = new HashMap<String, Double>(results);
        Map<String, Double> upperBounds = new HashMap<String, Double>(results);

        for (VPlanLeaf leaf : leafSet) {
            if (leaf.hasKOPotential()) {
                return true;
            }
        }

        updateBounds(planInfo, leafSet, lowerBounds, upperBounds);

        return checkRangesOverlapping(new ArrayList<Double>(lowerBounds.values()),
            new ArrayList<Double>(upperBounds.values()));
    }

    /**
     * Updates the lower and upper bounds for each alternative to reflect
     * potential minimum and maximum weighted results in case the provided
     * leaves are set to their potential minimum and maximum values
     * respectively.
     * 
     * @param planInfo
     *            plan info
     * @param leafSet
     *            leaves for the plan to consider
     * @param alternativeLowerBounds
     *            the lower bounds for each alternative
     * @param alternativeUpperBounds
     *            the upper bounds for each alternative
     */
    private void updateBounds(final PlanInfo planInfo, final Set<VPlanLeaf> leafSet,
        Map<String, Double> alternativeLowerBounds, Map<String, Double> alternativeUpperBounds) {
        for (VPlanLeaf leaf : leafSet) {
            Map<String, Double> leafResults = leaf.getAlternativeResultsAsMap();

            double leafMinimum = leaf.getPotentialMinimum();
            double leafMaximum = leaf.getPotentialMaximum();

            for (Map.Entry<String, Double> leafResult : leafResults.entrySet()) {
                Double lowerBound = alternativeLowerBounds.get(leafResult.getKey());
                Double upperBound = alternativeUpperBounds.get(leafResult.getKey());

                alternativeLowerBounds.put(leafResult.getKey(), lowerBound
                    - ((leafResult.getValue() - leafMinimum) * leaf.getTotalWeight()));
                alternativeUpperBounds.put(leafResult.getKey(), upperBound
                    + ((leafMaximum - leafResult.getValue()) * leaf.getTotalWeight()));
            }

        }
    }

    /**
     * Takes a list of lower bounds and a list of corresponding upper bounds of
     * ranges and checks if there are overlapping ranges.
     * 
     * Note: lowerBounds and upperBounds must have the same length
     * 
     * @param lowerBounds
     *            the lower bounds of the ranges
     * @param upperBounds
     *            the upper bounds of the ranges
     * @return true if at least one range overlaps another, false otherwise
     */
    private boolean checkRangesOverlapping(final List<Double> lowerBounds, final List<Double> upperBounds) {
        Collections.sort(lowerBounds);
        Collections.sort(upperBounds);

        for (int i = 0; i < lowerBounds.size() - 1; i++) {
            if (lowerBounds.get(i + 1) < upperBounds.get(i)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a set of leaves that are mapped to the provided measures.
     * 
     * @param measureUris
     *            collection of measure URIs
     * @return a set of leaves
     */
    private Map<PlanInfo, Set<VPlanLeaf>> getLeavesOfMeasureUris(final Collection<String> measureUris) {
        Map<PlanInfo, Set<VPlanLeaf>> allLeaves = new HashMap<PlanInfo, Set<VPlanLeaf>>();

        for (String measure : measureUris) {
            Map<PlanInfo, Set<VPlanLeaf>> leavesOfPlans = leavesOfMeasures.get(measure);
            if (leavesOfPlans != null) {
                for (Map.Entry<PlanInfo, Set<VPlanLeaf>> entry : leavesOfPlans.entrySet()) {
                    Set<VPlanLeaf> leavesOfPlan = allLeaves.get(entry.getKey());
                    if (leavesOfPlan == null) {
                        leavesOfPlan = new HashSet<VPlanLeaf>();
                        allLeaves.put(entry.getKey(), leavesOfPlan);
                    }
                    leavesOfPlan.addAll(entry.getValue());
                }
            }
        }

        return allLeaves;
    }

    /**
     * Returns the plan info with the given ID.
     * 
     * @param id
     *            plan id
     * @return the plan info of the plan
     */
    private PlanInfo getSelectedPlanInfo(final int id) {
        for (PlanInfo planInfo : selectedPlans) {
            if (planInfo.getId() == id) {
                return planInfo;
            }
        }

        return null;
    }

}
