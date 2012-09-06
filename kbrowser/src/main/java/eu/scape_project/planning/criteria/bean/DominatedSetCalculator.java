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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.model.kbrowser.VPlanLeaf;
import eu.scape_project.planning.model.measurement.Measure;

public class DominatedSetCalculator {

    // @Inject
    // private Logger log;
    private static final Logger log = LoggerFactory.getLogger(DominatedSetCalculator.class);

    private List<PlanInfo> selectedPlans = new ArrayList<PlanInfo>();
    private List<VPlanLeaf> selectedVPlanLeaves = new ArrayList<VPlanLeaf>();
    private HashMap<Measure, Set<VPlanLeaf>> criterionVPlanLeaves = new HashMap<Measure, Set<VPlanLeaf>>();

    public DominatedSetCalculator(List<PlanInfo> selectedPlans, List<VPlanLeaf> selectedVPlanLeaves) {
        this.selectedPlans = selectedPlans;
        this.selectedVPlanLeaves = selectedVPlanLeaves;
        fillCriterionVPlanLeaves();
    }

    /**
     * Fills the map of used VPlanLeaves per criterion.
     */
    private void fillCriterionVPlanLeaves() {
        for (VPlanLeaf vPlanLeaf : selectedVPlanLeaves) {
            // Check if criteria mapped
            if (vPlanLeaf.getCriterion() != null) {
                Set<VPlanLeaf> vPlanLeaves = criterionVPlanLeaves.get(vPlanLeaf.getCriterion());

                if (vPlanLeaves == null) {
                    vPlanLeaves = new HashSet<VPlanLeaf>();
                    criterionVPlanLeaves.put(vPlanLeaf.getCriterion(), vPlanLeaves);
                }
                vPlanLeaves.add(vPlanLeaf);
            }
        }
    }

    public void calculateDominatedPowerSet() {
        isSubsetDominated(new ArrayList<Measure>(criterionVPlanLeaves.keySet()), new ArrayList<Measure>(), 0);
    }

    public boolean isSubsetDominated(List<Measure> allCriteria, List<Measure> criteriaList, int index) {

        boolean isSubsetDominated = false;

        for (int i = index; i < allCriteria.size(); i++) {
            List<Measure> tmp = new ArrayList<Measure>(criteriaList);
            tmp.add(allCriteria.get(i));
            if (isCriterionSetDominated(tmp)) {
                isSubsetDominated = true;
                List<Measure> tmp2 = new ArrayList<Measure>(tmp);
                if (!isSubsetDominated(allCriteria, tmp2, i + 1)) {
                    logDominated(tmp);
                }
            }
        }

        return isSubsetDominated;
    }

    private void logDominated(List<Measure> criteriaList) {
        String logString = "Set [";
        for (Measure measure : criteriaList) {
            logString += measure.getUri() + ", ";
        }
        log.info(logString + "]: dominated");
    }

    public boolean isCriterionSetDominated(List<Measure> criteria) {
        Set<VPlanLeaf> allVPlanLeaves = new HashSet<VPlanLeaf>();

        int maximumVPleanLeaves = Integer.MIN_VALUE;

        String logString = "Set [";
        for (Measure measure : criteria) {
            logString += measure.getUri() + ", ";
            Set<VPlanLeaf> vPlanLeaves = criterionVPlanLeaves.get(measure);
            if (vPlanLeaves != null) {

                if (maximumVPleanLeaves < vPlanLeaves.size()) {
                    maximumVPleanLeaves = vPlanLeaves.size();
                }

                allVPlanLeaves.addAll(vPlanLeaves);
            }
        }

        if (maximumVPleanLeaves < criteria.size()) {
            return false;
        }

        boolean isCriterionSetDominated = isVPlanSetDominated(allVPlanLeaves);

        if (isCriterionSetDominated) {
            // log.info(logString + "]: dominated");
        }

        return isCriterionSetDominated;
    }

    private boolean isVPlanSetDominated(List<VPlanLeaf> vPlanLeaves) {
        return isVPlanSetDominated(new HashSet<VPlanLeaf>(vPlanLeaves));
    }

    /**
     * Checks if the provided criteria leaves are dominated. This is true if for
     * every plan that uses all leaves, the leaf set is dominated (@see
     * isLeafSetDominated(PlanInfo, Set<VPlanLeaf>)).
     * 
     * @param criteriaLeaves
     * @return
     */
    private boolean isVPlanSetDominated(Set<VPlanLeaf> vPlanLeaves) {

        Map<PlanInfo, Set<VPlanLeaf>> planLeaves = new HashMap<PlanInfo, Set<VPlanLeaf>>();

        // The maximum number of leaves over all plans. This is used to ignore
        // plans that do not contain all criteria provided.
        int maximumLeafCount = Integer.MIN_VALUE;

        // Loop over criteria vPlanLeaves
        for (VPlanLeaf vPlanLeaf : vPlanLeaves) {

            if (vPlanLeaf.hasKOPotential()) {
                return false;
            }

            // Add vPlanLeaf to plan map
            PlanInfo planInfo = getSelectedPlanInfo(vPlanLeaf.getPlanId());
            Set<VPlanLeaf> planVPlanLeaves = planLeaves.get(planInfo);
            if (planVPlanLeaves == null) {
                planVPlanLeaves = new HashSet<VPlanLeaf>();
                planLeaves.put(planInfo, planVPlanLeaves);
            }
            planVPlanLeaves.add(vPlanLeaf);
            // Set maximum number of leaves of all plans
            if (planVPlanLeaves.size() > maximumLeafCount) {
                maximumLeafCount = planVPlanLeaves.size();
            }
        }

        // Check if leaves set is dominated for every plan that uses all leaves
        for (Map.Entry<PlanInfo, Set<VPlanLeaf>> entry : planLeaves.entrySet()) {
            // if (entry.getValue().size() == maximumLeafCount) {
            if (!isLeafSetDominated(entry.getKey(), entry.getValue())) {
                return false;
            }
            // }
        }

        return true;
    }

    /**
     * Checks if the set of leafs is dominated for this plan. This is true if
     * the sum of all changes this set of leaves can have in the worst case for
     * this plan would have an influence on the ranking.
     * 
     * @param planInfo
     * @param vPlanLeafs
     * @return
     */
    private boolean isLeafSetDominated(PlanInfo planInfo, Set<VPlanLeaf> vPlanLeafs) {
        // Overall result of each alternative
        Map<String, Double> alternativePlanResults = planInfo.getOverallResults().getResults();

        // Maximum change
        double maximumChange = calculateMaximumChange(planInfo, vPlanLeafs);

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
        for (VPlanLeaf vPlanLeaf : vPlanLeafs) {
            logString += vPlanLeaf.getCriterion().getUri() + ", ";
        }

        boolean isLeafSetDominated = maximumChange < minimumDifference;
        if (isLeafSetDominated) {
            // log.info(logString + "]: dominated");
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

    public List<PlanInfo> getSelectedPlans() {
        return selectedPlans;
    }

    public void setSelectedPlans(List<PlanInfo> selectedPlans) {
        this.selectedPlans = selectedPlans;
    }
}
