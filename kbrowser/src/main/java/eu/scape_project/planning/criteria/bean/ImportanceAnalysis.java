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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.scape_project.planning.model.TargetValueObject;
import eu.scape_project.planning.model.kbrowser.CriteriaLeaf;
import eu.scape_project.planning.model.kbrowser.VPlanLeaf;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.transform.NumericTransformer;
import eu.scape_project.planning.model.transform.OrdinalTransformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportanceAnalysis implements Serializable {
    private static final long serialVersionUID = -814054847584527659L;
    private static final Logger log = LoggerFactory.getLogger(ImportanceAnalysis.class);

    private List<VPlanLeaf> planLeaves = new ArrayList<VPlanLeaf>();
    private List<VPlanLeaf> mappedPlanLeaves = new ArrayList<VPlanLeaf>();

    private List<PlanInfo> selectedPlans;
    private long nrRelevantPlans;

    private List<ImportanceAnalysisProperty> tableRows;
    private int tableRowsCount = 0;

    public ImportanceAnalysis(final Collection<Measure> measures, final List<VPlanLeaf> planLeaves,
        final List<PlanInfo> selectedPlans) {
        this.planLeaves = planLeaves;
        filterMappedLeaves();

        this.nrRelevantPlans = selectedPlans.size();
        this.selectedPlans = selectedPlans;

        buildTable(measures);
    }

    /**
     * Method responsible for filtering mapped leaves out of all given leaves.
     */
    private void filterMappedLeaves() {
        // Filter mapped PlanLeaves
        mappedPlanLeaves = new ArrayList<VPlanLeaf>(planLeaves.size());
        for (VPlanLeaf pl : planLeaves) {
            if (pl.isMapped()) {
                mappedPlanLeaves.add(pl);
            }
        }
    }

    private void buildTable(Collection<Measure> measures) {
        tableRows = new ArrayList<ImportanceAnalysisProperty>(measures.size());

        for (Measure measure : measures) {
            evaluateCriterion(measure);
        }

        tableRowsCount = tableRows.size();
    }

    private void evaluateCriterion(Measure measure) {
        List<VPlanLeaf> criterionPlanLeaves = getCriterionPlanLeaves(measure);

        if (criterionPlanLeaves.size() > 0) {
            CriteriaLeaf criteriaLeaf = new CriteriaLeaf(nrRelevantPlans);
            criteriaLeaf.setPlanLeaves(criterionPlanLeaves);

            ImportanceAnalysisProperty importanceAnalysisProperty = new ImportanceAnalysisProperty();
            importanceAnalysisProperty.setCategory(measure.getAttribute().getCategory().getName());
            importanceAnalysisProperty.setAttribute(measure.getAttribute().getName());
            importanceAnalysisProperty.setMeasure(measure.getName());

            importanceAnalysisProperty.setIf1(criteriaLeaf.getImportanceFactorIF1());
            importanceAnalysisProperty.setIf2(criteriaLeaf.getImportanceFactorIF2());
            importanceAnalysisProperty.setIf3(criteriaLeaf.getImportanceFactorIF3());
            importanceAnalysisProperty.setIf4(criteriaLeaf.getImportanceFactorIF4());
            importanceAnalysisProperty.setIf5(criteriaLeaf.getImportanceFactorIF5());
            importanceAnalysisProperty.setIf6(criteriaLeaf.getImportanceFactorIF6());
            importanceAnalysisProperty.setIf7(criteriaLeaf.getImportanceFactorIF7());
            importanceAnalysisProperty.setIf8(criteriaLeaf.getImportanceFactorIF8());
            importanceAnalysisProperty.setIf9(criteriaLeaf.getImportanceFactorIF9());
            importanceAnalysisProperty.setIf10(criteriaLeaf.getImportanceFactorIF10());
            importanceAnalysisProperty.setIf11(criteriaLeaf.getImportanceFactorIF11());
            importanceAnalysisProperty.setIf12(criteriaLeaf.getImportanceFactorIF12());
            importanceAnalysisProperty.setIf13(criteriaLeaf.getImportanceFactorIF13());
            importanceAnalysisProperty.setIf14(criteriaLeaf.getImportanceFactorIF14());
            importanceAnalysisProperty.setIf15(criteriaLeaf.getImportanceFactorIF15());
            importanceAnalysisProperty.setIf16(criteriaLeaf.getImportanceFactorIF16());
            importanceAnalysisProperty.setIf17(criteriaLeaf.getImportanceFactorIF17());
            importanceAnalysisProperty.setIf18(criteriaLeaf.getImportanceFactorIF18());

            importanceAnalysisProperty.setIf19(calculateImportanceFactorIF19(selectedPlans, criteriaLeaf));

            tableRows.add(importanceAnalysisProperty);
        }
    }

    /**
     * calculates importance factor "robustness" - the extend to which the
     * measured value of a criterion can change, without impact on the winning
     * alternative - CriteriaLeaf is an entity bean and part of plato-model for
     * the calculations we need PlanInfo and I don't want to pollute plato-model
     * (further) with kbrowser related classes. for this reason the calculation
     * is done here.
     * 
     * @param selectedPlans
     * @param criteriaLeaf
     * @return
     */
    private double calculateImportanceFactorIF19(List<PlanInfo> selectedPlans, CriteriaLeaf criteriaLeaf) {
        double sum = 0.0;
        for (VPlanLeaf pLeaf : criteriaLeaf.getPlanLeaves()) {
            String prefix = "IF19 - " + pLeaf.getMeasure().getUri() + ": ";
            double leafFactor = 0.0;
            PlanInfo planInfo = null;
            // retrieve the PlanInfo for this plan
            for (PlanInfo p : selectedPlans) {
                if (p.getId() == pLeaf.getPlanId()) {
                    planInfo = p;
                    break;
                }
            }
            Set<String> competitors = new HashSet<String>(planInfo.getOverallResults().getResults().keySet());
            competitors.remove(planInfo.getWinningAlternative());
            if (competitors.isEmpty()) {
                // sum += leafFactor;
                log.info(prefix + "No competitors left beside " + planInfo.getWinningAlternative() + " in plan: "
                    + planInfo.getId());
                continue;
            }

            double winnerOverallResult = planInfo.getWinningResult();

            // determine result of the nearest non-winning alternative
            String sndAlternative = "";
            double sndOverallResult = Double.MIN_VALUE;
            for (String a : competitors) {
                double result = planInfo.getOverallResults().getResults().get(a);
                if (result > sndOverallResult) {
                    sndOverallResult = result;
                    sndAlternative = a;
                }
            }
            double overallDiffToNext = winnerOverallResult - sndOverallResult;
            // map this difference back as target result for this criterion
            double overallDiffToNextAsTargetValue = overallDiffToNext / pLeaf.getTotalWeight();

            // get the transformed, but not weighted, result of the winning
            // alternative for this criterion
            double winnerTargetValue = pLeaf.getAlternativeResultsAsMap().get(planInfo.getWinningAlternative());

            // calculate the minimal target value, which would cause a change of
            // the winning alternative
            double minTargetValue = winnerTargetValue - overallDiffToNextAsTargetValue;

            // this will be negative, if the difference between 1st and 2nd is
            // too big,
            // and depending on the transformer setting, the minimal possible
            // value could be > 0
            String intermediaryResults = "; overallDiffToNext = " + overallDiffToNext + " = as target value = "
                + overallDiffToNextAsTargetValue + "; minTargetValue = " + minTargetValue
                + "; pLeaf.getPotentialMinimum() = " + pLeaf.getPotentialMinimum();
            if (minTargetValue > pLeaf.getPotentialMinimum()) {
                log.info(prefix + "1st " + planInfo.getWinningAlternative() + ", 2nd : " + sndAlternative
                    + " in plan: " + planInfo.getId());
                // this criterion could be a game changer - map it back to the
                // measurement scale
                if (pLeaf.getTransformer() instanceof NumericTransformer) {
                    NumericTransformer numTransformer = (NumericTransformer) pLeaf.getTransformer();
                    // For numeric values: The percentage that we can change the
                    // value on the (overally!) winning candidate
                    // without the output range changing so much as to make the
                    // winning candidate lose its winning rank

                    // TODO: CHECK: Do we have a problem here? the values of an
                    // alternative are first transformed, then aggregated
                    // therefore we have to transform this winning value also
                    // back ...
                    double winnerMeasuredValue = numTransformer.transformBack(winnerTargetValue);
                    double minMeasuredValue = numTransformer.transformBack(minTargetValue);

                    // TODO: what if the measured value was 0.0?
                    if (winnerMeasuredValue != 0.0) {
                        leafFactor = (winnerMeasuredValue - minMeasuredValue) / winnerMeasuredValue;
                        if (!numTransformer.hasIncreasingOrder()) {
                            leafFactor *= -1.0;
                        }
                    }
                    log.info(prefix + " - numeric = " + leafFactor + intermediaryResults + ";  winnerMeasuredValue = "
                        + winnerMeasuredValue + "; minMeasuredValue = " + minMeasuredValue + "; plan id "
                        + planInfo.getId());
                } else {
                    // it's an ordinal transformer
                    // For ordinals: Percentage of possible alternative values
                    // for the (overally!) winning candidate
                    // that would make the winning candidate lose its winning
                    // rank.
                    // E.g: “good, bad, ugly” > value is good; bad doesn’t
                    // change rank; ugly changes rank: robustness = 1/2 = 0.5
                    int numPossible = 0;
                    Collection<TargetValueObject> targetValueObjects = ((OrdinalTransformer) pLeaf.getTransformer())
                        .getMapping().values();
                    for (TargetValueObject value : targetValueObjects) {
                        if (value.getValue() >= minTargetValue) {
                            numPossible++;
                        }
                    }
                    leafFactor = (double) numPossible / (double) (targetValueObjects.size());
                    log.info(prefix + " - ordinal = " + leafFactor + intermediaryResults + ";  numPossible = "
                        + numPossible + "; numTotal = " + targetValueObjects.size() + "; plan id " + planInfo.getId());
                }

            } else {
                // it's not possible to reduce the value of the winner to this
                // amount
                log.info(prefix + "1st " + planInfo.getWinningAlternative() + ", 2nd : " + sndAlternative
                    + intermediaryResults + " in plan: " + planInfo.getId());
                // leafFactor = 0.0
            }
            sum += leafFactor;
        }
        // TODO: CHECK: How to aggregate - just average values?
        return sum / (double) criteriaLeaf.getPlanLeaves().size();
    }

    /**
     * Method responsible for filtering all leaves which match the given
     * criterion (Property [+ Metric]). Validity check of the input parameters
     * (e.g. property + metric must be measurable) is done in calling code!!
     * 
     * @param cMeasurableProperty
     *            property to filter.
     * @param cMetric
     *            metric to filter.
     */
    public List<VPlanLeaf> getCriterionPlanLeaves(Measure measure) {
        List<VPlanLeaf> measurePlanLeaves = new ArrayList<VPlanLeaf>();

        // filter PlanLeaves
        for (VPlanLeaf l : mappedPlanLeaves) {
            // property and metric must match
            if (l.getMeasure().getUri().equals(measure.getUri())) {
                measurePlanLeaves.add(l);
            }
        }

        return measurePlanLeaves;
    }

    /**
     * Exports the current importance table to a csv file.
     */
    public void exportTableToCsv() {

    }

    /**
     * Calculates the average potential output range based on the CPL
     * occurrence.
     * 
     * @param criterionPlanLeaves
     *            corresponding plan leaves.
     * @return average potential output range based on the CPL occurrence, if
     *         calculation is possible. If calculation is not possible -1 is
     *         returned.
     */
    /*
     * public double
     * getCPLAvgPotentialOutputRangeOccurrenceBased(List<VPlanLeaf>
     * criterionPlanLeaves) { int appropriateLeaves = 0; double porSum = 0;
     * 
     * for (VPlanLeaf l : criterionPlanLeaves) { double por =
     * l.getPotentialOutputRange();
     * 
     * // only leaves for which the por can be calculated can be used if (por !=
     * -1) { porSum = porSum + por; appropriateLeaves++; } }
     * 
     * if (appropriateLeaves == 0) { return -1; }
     * 
     * double avgPor = porSum / appropriateLeaves; return avgPor; }
     */

    /**
     * Calculates the average actual output range based on the CPL occurrence.
     * 
     * @param criterionPlanLeaves
     *            corresponding plan leaves.
     * @return average actual output range based on the CPL occurrence, if
     *         calculation is possible. If calculation is not possible -1 is
     *         returned.
     */
    /*
     * public double getCPLAvgActualOutputRangeOccurrenceBased(List<VPlanLeaf>
     * criterionPlanLeaves) { int appropriateLeaves = 0; double aorSum = 0;
     * 
     * for (VPlanLeaf l : criterionPlanLeaves) { double aor =
     * l.getActualOutputRange();
     * 
     * // only leaves for which the aor can be calculated can be used if (aor !=
     * -1) { aorSum = aorSum + aor; appropriateLeaves++; } }
     * 
     * if (appropriateLeaves == 0) { return -1; }
     * 
     * double avgAor = aorSum / appropriateLeaves; return avgAor; }
     */

    public void setTableRows(List<ImportanceAnalysisProperty> tableRows) {
        this.tableRows = tableRows;
    }

    public List<ImportanceAnalysisProperty> getTableRows() {
        return tableRows;
    }

    public void setTableRowsCount(int tableRowsCount) {
        this.tableRowsCount = tableRowsCount;
    }

    public int getTableRowsCount() {
        return tableRowsCount;
    }

    /* ----------------- Init ----------------- */

    public String init() {
        log.debug("init finally called");
        return "success";
    }
}
