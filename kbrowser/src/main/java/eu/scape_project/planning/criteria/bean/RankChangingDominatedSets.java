package eu.scape_project.planning.criteria.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.scape_project.planning.model.kbrowser.VPlanLeaf;

/**
 * Calculator for dominates measures and sets of measures.
 * 
 * A set of measures is dominated if changing the value of a measure could
 * potentially change the ranking of alternatives.
 * 
 * For all leaves, the lower and upper boundary of possible values weighted
 * according to the leaf is calculated. If these boundaries overlap, a change in
 * the values could potentially change the ranking of alternatives.
 * 
 * @see "Improving decision support for software component selection throughsystematic cross-referencing and analysis of multiple decision criteria, Christoph Becker et al."
 */
public class RankChangingDominatedSets extends DominatedSets {

    /**
     * Creates a new DominatedSetCalculator.
     * 
     * @param selectedPlans
     *            PlanInfos to use
     * @param selectedLeaves
     *            leaves to use
     */
    public RankChangingDominatedSets(final List<PlanInfo> selectedPlans, final List<VPlanLeaf> selectedLeaves) {
        super(selectedPlans, selectedLeaves);
    }

    /**
     * Returns the result ranges for each alternative of the plan. The ranges
     * describe the potential ranges of values for the provided measures.
     * 
     * @param planInfo
     *            the plan
     * @param measureUris
     *            measures to take into account
     * @return a list of result ranges
     */
    public List<AlternativeResultRange> getDominatedSetBounds(final PlanInfo planInfo, Collection<String> measureUris) {
        List<AlternativeResultRange> resultRanges = createAlternativeResultRanges(planInfo);

        Map<PlanInfo, Set<VPlanLeaf>> leavesOfMeasureUris = getLeavesOfMeasureUris(measureUris);
        Set<VPlanLeaf> leavesOfPlan = leavesOfMeasureUris.get(planInfo);

        updateBounds(leavesOfPlan, resultRanges);

        return resultRanges;
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
    @Override
    protected boolean isLeafSetDominated(final PlanInfo planInfo, final Set<VPlanLeaf> leafSet) {

        for (VPlanLeaf leaf : leafSet) {
            if (leaf.hasKOPotential()) {
                return false;
            }
        }

        List<AlternativeResultRange> resultRanges = createAlternativeResultRanges(planInfo);
        updateBounds(leafSet, resultRanges);

        return !rangesOverlapping(resultRanges);
    }

    /**
     * Creates a list of result ranges from the planinfo.
     * 
     * @param planInfo
     *            the planinfo
     * @return a list of result ranges
     */
    private List<AlternativeResultRange> createAlternativeResultRanges(final PlanInfo planInfo) {
        Map<String, Double> results = planInfo.getOverallResults().getResults();
        List<AlternativeResultRange> resultRanges = new ArrayList<AlternativeResultRange>(results.size());

        for (Map.Entry<String, Double> result : results.entrySet()) {
            resultRanges.add(new AlternativeResultRange(result.getKey(), result.getValue()));
        }

        return resultRanges;
    }

    /**
     * Updates the lower and upper bounds for each alternative to reflect
     * potential minimum and maximum weighted results in case the provided
     * leaves are set to their potential minimum and maximum values
     * respectively.
     * 
     * @param leafSet
     *            leaves for the plan to consider
     * @param resultRanges
     *            the result ranges to update
     */
    private void updateBounds(final Set<VPlanLeaf> leafSet, List<AlternativeResultRange> resultRanges) {
        for (VPlanLeaf leaf : leafSet) {
            double leafMinimum = leaf.getPotentialMinimum();
            double leafMaximum = leaf.getPotentialMaximum();

            for (AlternativeResultRange resultRange : resultRanges) {
                resultRange.setLowerBound(resultRange.getLowerBound()
                    - ((resultRange.getResult() - leafMinimum) * leaf.getTotalWeight()));
                resultRange.setUpperBound(resultRange.getUpperBound()
                    + ((leafMaximum - resultRange.getResult()) * leaf.getTotalWeight()));
            }

        }
    }

    /**
     * Takes a list of lower bounds and a list of corresponding upper bounds of
     * ranges and checks if there are overlapping ranges.
     * 
     * Note: lowerBounds and upperBounds must have the same length
     * 
     * @param resultRanges
     *            the result ranges to check
     * @return true if at least one range overlaps another, false otherwise
     */
    private boolean rangesOverlapping(List<AlternativeResultRange> resultRanges) {
        // Sort by upper bound
        Collections.sort(resultRanges, new Comparator<AlternativeResultRange>() {
            @Override
            public int compare(AlternativeResultRange o1, AlternativeResultRange o2) {
                return Double.compare(o1.getUpperBound(), o2.getUpperBound());
            }
        });

        double lastUpperBound = Double.NEGATIVE_INFINITY;
        for (AlternativeResultRange resultRange : resultRanges) {
            if (resultRange.getLowerBound() < lastUpperBound) {
                return true;
            }
            lastUpperBound = resultRange.getUpperBound();
        }

        return false;
    }
}
