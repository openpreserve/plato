package eu.scape_project.planning.criteria.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.scape_project.planning.model.kbrowser.VPlanLeaf;

/**
 * Calculator for dominates measures and sets of measures.
 */
public abstract class DominatedSets {

    /**
     * Aggregation mode of calculations.
     */
    public enum Aggregation {
        /**
         * Must be true for ALL plans.
         */
        ALL,

        /**
         * Must be true for ANY plans.
         */
        ANY
    };

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
    public DominatedSets(final List<PlanInfo> selectedPlans, final List<VPlanLeaf> selectedLeaves) {
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

    /**
     * Checks the powerset of measures for potential to change the ranking of
     * plans of this calculator.
     * 
     * @param aggregation
     *            aggregation mode over plans
     * @return the largest sets of measures
     */
    public List<List<String>> getDominatedSets(Aggregation aggregation) {
        return getDominatedSupersets(new ArrayList<String>(leavesOfMeasures.keySet()), 0,
            new ArrayList<List<String>>(0), new ArrayList<String>(leavesOfMeasures.keySet().size()), aggregation);
    }

    /**
     * Checks subsets of all measures for potential to change the ranking for
     * plans of this calculator. Recursively checks all measures, starting from
     * index.
     * 
     * Note: For performance reasons the sets are stored in lists. Thus
     * allMeasureUris should not contain duplicates.
     * 
     * @param allMeasureUris
     *            all measures that should be considered
     * @param index
     *            the beginning index of all measures
     * @param currentRankChangingSets
     *            rank-changing sets already found for current measures
     * @param currentMeasures
     *            the current list of measures used as starting point
     * @param aggregation
     *            aggregation mode over plans
     * @return a list of dominated supersets of the currentMeasures
     */
    private List<List<String>> getDominatedSupersets(final List<String> allMeasureUris, final int index,
        final List<List<String>> currentRankChangingSets, List<String> currentMeasures, Aggregation aggregation) {
        List<List<String>> rankChangingSets = new ArrayList<List<String>>(currentRankChangingSets);

        for (int i = index; i < allMeasureUris.size(); i++) {
            currentMeasures.add(allMeasureUris.get(i));
            // Check if current measures are already in rank-changing sets
            List<List<String>> setsOfCurrentMeasures = getSetsContainingMeasure(rankChangingSets, allMeasureUris.get(i));
            if (setsOfCurrentMeasures.size() > 0) {
                // Current measures is already in rank-changing sets, check
                // supersets
                List<List<String>> setsOfSuperMeasures = getDominatedSupersets(allMeasureUris, i + 1,
                    setsOfCurrentMeasures, currentMeasures, aggregation);
                // Add rank-changing supersets
                addNewSets(setsOfCurrentMeasures, setsOfSuperMeasures, rankChangingSets);
            } else {
                // Current measures not yet in rank-changing sets, check if
                // dominated
                if (isLeafSetDominated(getLeavesOfMeasureUris(currentMeasures), aggregation)) {
                    // Rank-changing, check supersets
                    List<List<String>> setsOfSuperMeasures = getDominatedSupersets(allMeasureUris, i + 1,
                        setsOfCurrentMeasures, currentMeasures, aggregation);
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
     * Returns a set of leaves that are mapped to the provided measures.
     * 
     * Note: When a single measures URI is provided, the returned map is not a
     * copy but the actual map from leavesOfMesaures. Thus you should not modify
     * the map.
     * 
     * @param measureUris
     *            collection of measure URIs
     * @return a map of plans with a set of leaves for each plan
     */
    protected Map<PlanInfo, Set<VPlanLeaf>> getLeavesOfMeasureUris(final Collection<String> measureUris) {

        // Single measure
        if (measureUris.size() == 1) {
            Map<PlanInfo, Set<VPlanLeaf>> leavesOfPlans = leavesOfMeasures.get(measureUris.iterator().next());
            if (leavesOfPlans != null) {
                return leavesOfPlans;
            } else {
                return new HashMap<PlanInfo, Set<VPlanLeaf>>();
            }
        }

        // Multiple measures
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
     * Checks if the provided measure could change the ranking of alternatives.
     * 
     * @param measureUri
     *            URI of measure to check
     * @param aggregation
     *            aggregation mode over plans
     * @return true if the measure are dominated, false otherwise
     */
    public boolean isMeasureDominated(final String measureUri, Aggregation aggregation) {
        Map<PlanInfo, Set<VPlanLeaf>> leavesOfPlans = leavesOfMeasures.get(measureUri);

        if (leavesOfPlans == null) {
            return true;
        }

        return isLeafSetDominated(leavesOfPlans, aggregation);
    }

    /**
     * Checks if the provided set of measures are dominated.
     * 
     * @param measureUris
     *            URIs of measures to check
     * @param aggregation
     *            aggregation mode over plans
     * @return true if the measures are dominated, false otherwise
     */
    public boolean isMeasureSetDominated(Collection<String> measureUris, Aggregation aggregation) {
        return isLeafSetDominated(getLeavesOfMeasureUris(measureUris), aggregation);
    }

    /**
     * Checks if the leaves are dominated for all plans.
     * 
     * @param planLeaves
     *            plans and corresponding leaves
     * @param aggregation
     *            aggregation mode over plans
     * @return true if dominated, false otherwise
     */
    private boolean isLeafSetDominated(final Map<PlanInfo, Set<VPlanLeaf>> planLeaves, Aggregation aggregation) {
        if (aggregation == Aggregation.ALL) {
            for (Map.Entry<PlanInfo, Set<VPlanLeaf>> entry : planLeaves.entrySet()) {
                if (!isLeafSetDominated(entry.getKey(), entry.getValue())) {
                    return false;
                }
            }
            return true;
        } else {
            for (Map.Entry<PlanInfo, Set<VPlanLeaf>> entry : planLeaves.entrySet()) {
                if (isLeafSetDominated(entry.getKey(), entry.getValue())) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Returns plans that are dominated for this measure.
     * 
     * @param measureUri
     *            measure URI to check
     * @return a set of dominated plans
     */
    public Set<PlanInfo> getDominatedPlans(final String measureUri) {
        Set<PlanInfo> dominatedPlans = new HashSet<PlanInfo>();

        Map<PlanInfo, Set<VPlanLeaf>> leavesOfPlans = leavesOfMeasures.get(measureUri);

        // Check if leaf set is dominated for the plans
        for (Map.Entry<PlanInfo, Set<VPlanLeaf>> entry : leavesOfPlans.entrySet()) {
            if (isLeafSetDominated(entry.getKey(), entry.getValue())) {
                dominatedPlans.add(entry.getKey());
            }
        }

        return dominatedPlans;
    }

    /**
     * Returns plans that are dominated for this set of measures.
     * 
     * @param measureUris
     *            measure URIs to check
     * @return a set of dominated plans
     */
    public Set<PlanInfo> getDominatedPlans(final Collection<String> measureUris) {
        Set<PlanInfo> dominatedPlans = new HashSet<PlanInfo>();

        Map<PlanInfo, Set<VPlanLeaf>> leavesOfPlans = getLeavesOfMeasureUris(measureUris);

        // Check if leaf set is dominated for the plans
        for (Map.Entry<PlanInfo, Set<VPlanLeaf>> entry : leavesOfPlans.entrySet()) {
            if (isLeafSetDominated(entry.getKey(), entry.getValue())) {
                dominatedPlans.add(entry.getKey());
            }
        }

        return dominatedPlans;
    }

    /**
     * Checks if the leafSet of the provided planInfo is dominated.
     * 
     * @param planInfo
     *            the plan to check
     * @param leafSet
     *            leaves of the plan
     * @return true if the leaves are dominated, false otherwise
     */
    protected abstract boolean isLeafSetDominated(final PlanInfo planInfo, final Set<VPlanLeaf> leafSet);

}
