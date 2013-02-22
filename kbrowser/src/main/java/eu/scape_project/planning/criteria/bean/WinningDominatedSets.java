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

import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.scape_project.planning.model.kbrowser.VPlanLeaf;

/**
 * Calculator for dominates measures and sets of measures.
 * 
 * A set of measures is dominated if setting the winning alternative to the
 * smallest possible value and all other alternatives to the highest value, the
 * winner changes.
 * 
 * For each leaf, the winning alternative is set to the smallest possible value
 * and all other alternatives are set to the largest possible value. This
 * potential change, weighted according to the leaf, is compared to the distance
 * of the winning alternative to the second best alternative. If the change is
 * smaller than the distance to the winning alternative the winner could
 * potentially be changed by the set of measures.
 * 
 * @see "Improving decision support for software component selection throughsystematic cross-referencing and analysis of multiple decision criteria, Christoph Becker et al."
 */
public class WinningDominatedSets extends DominatedSets {

    /**
     * Creates a new DominatedSetCalculator.
     * 
     * @param selectedPlans
     *            PlanInfos to use
     * @param selectedLeaves
     *            leaves to use
     */
    public WinningDominatedSets(final List<PlanInfo> selectedPlans, final List<VPlanLeaf> selectedLeaves) {
        super(selectedPlans, selectedLeaves);
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
    @Override
    protected boolean isLeafSetDominated(final PlanInfo planInfo, final Set<VPlanLeaf> leafSet) {

        for (VPlanLeaf leaf : leafSet) {
            if (leaf.hasKOPotential()) {
                return false;
            }
        }

        // Maximum change
        double maxChange = calculateMaxWinningChange(planInfo, leafSet);

        // Get second best alternative
        String winningAlternative = planInfo.getWinningAlternative();
        double maxAlternative = Double.NEGATIVE_INFINITY;
        for (Map.Entry<String, Double> entry : planInfo.getOverallResults().getResults().entrySet()) {
            if (!entry.getKey().equals(winningAlternative) && entry.getValue() > maxAlternative) {
                maxAlternative = entry.getValue();
            }
        }

        // Dominated if maximum possible change < minimum difference to winner
        return maxChange < (planInfo.getWinningResult() - maxAlternative);

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

}
