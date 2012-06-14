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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.model.kbrowser.CriteriaLeaf;
import eu.scape_project.planning.model.kbrowser.VPlanLeaf;
import eu.scape_project.planning.model.measurement.MeasurableProperty;
import eu.scape_project.planning.model.measurement.Metric;

public class ImportanceAnalysis implements Serializable {
    private static final long serialVersionUID = -814054847584527659L;
    private static final Logger log = LoggerFactory.getLogger(ImportanceAnalysis.class);
    
    private List<VPlanLeaf> planLeaves = new ArrayList<VPlanLeaf>();
    private List<VPlanLeaf> mappedPlanLeaves = new ArrayList<VPlanLeaf>();
    
    private List<PlanInfo> selectedPlans;
    private long nrRelevantPlans;
    
    private List<ImportanceAnalysisProperty> tableRows;
    private int tableRowsCount = 0;
        
    
    public ImportanceAnalysis(final Collection<MeasurableProperty> allMeasurableProperties, final List<VPlanLeaf> planLeaves, final List<PlanInfo> selectedPlans) {
        this.planLeaves = planLeaves;
        filterMappedLeaves();
        
        this.nrRelevantPlans = selectedPlans.size();
        this.selectedPlans = selectedPlans;
        
        buildTable(allMeasurableProperties);
    }
    
    /**
     * Method responsible for filtering mapped leaves out of all given leaves.
     */
    private void filterMappedLeaves() {
        // filter mapped PlanLeaves
        mappedPlanLeaves = new ArrayList<VPlanLeaf>();
        for (VPlanLeaf pl : planLeaves) {
            if (pl.getCriterion() != null) {
                mappedPlanLeaves.add(pl);
            }
        }
    }

    private void buildTable(Collection<MeasurableProperty> allMeasurableProperties) {
        tableRows = new ArrayList<ImportanceAnalysisProperty>();
        
        // each possible criterion of a property must be taken into account (valid combination of metric and property)
        for (MeasurableProperty measurableProperty : allMeasurableProperties) {
            // if a scale is attached to the property it is a valid criterion without any metric attached
            if (measurableProperty.getScale() != null) {
                evaluateCriterion(measurableProperty, null);
            }
            // any metric is always a valid criterion
            for (Metric metric : measurableProperty.getPossibleMetrics()) {
                evaluateCriterion(measurableProperty, metric);
            }
        }
        
        tableRowsCount = tableRows.size();
    }
    
    private void evaluateCriterion(MeasurableProperty property, Metric metric) {
        List<VPlanLeaf> criterionPlanLeaves = getCriterionPlanLeaves(property, metric);
        
        if (criterionPlanLeaves.size() > 0) {
            CriteriaLeaf criteriaLeaf = new CriteriaLeaf(nrRelevantPlans);
            criteriaLeaf.setPlanLeaves(criterionPlanLeaves);
        
            ImportanceAnalysisProperty importanceAnalysisProperty = new ImportanceAnalysisProperty();
            importanceAnalysisProperty.setCategory(property.getCategory().toString());
            importanceAnalysisProperty.setProperty(property.getName());
            if (metric != null) {
                importanceAnalysisProperty.setMetric(metric.getName());
            }
            else {
                importanceAnalysisProperty.setMetric("");
            }
            
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
     * calculates importance factor "robustness"
     * - the extend to which the measured value of a criterion can change, without impact on the winning alternative
     * - CriteriaLeaf is an entity bean and part of plato-model for the calculations we need PlanInfo 
     *   and I don't want to pollute plato-model (further) with kbrowser related classes.
     *   for this reason the calculation is done here. 
     *   
     * @param selectedPlans
     * @param criteriaLeaf
     * @return
     */
    private double calculateImportanceFactorIF19(List<PlanInfo> selectedPlans, CriteriaLeaf criteriaLeaf) {
        Map<VPlanLeaf, Double> importanceFactors = new HashMap<VPlanLeaf, Double>();
        
        for (VPlanLeaf pLeaf : criteriaLeaf.getPlanLeaves()) {
            PlanInfo planInfo = null;
            // retrieve the PlanInfo for this plan
            for (PlanInfo p : selectedPlans) {
                if (p.getId() == pLeaf.getPlanId()) {
                    planInfo = p;
                    break;
                }
            }
            Set<String> competitors =  planInfo.getOverallResults().getResults().keySet();
            competitors.remove(planInfo.getWinningAlternative());
            if (competitors.isEmpty()) {
                importanceFactors.put(pLeaf, 0.0);
                continue;
            }
            
            double winnerOverallResult = planInfo.getWinningResult();
            
            // determine result of the nearest non-winning alternative
            String sndAlternative;
            double sndOverallResult = Double.MIN_VALUE;
            for (String a : competitors) {
                double result = planInfo.getOverallResults().getResults().get(a);
                if (result > sndOverallResult) {
                    sndOverallResult = result;
                    sndAlternative = a;
                }
            }
            double overallDiffToNext = winnerOverallResult - sndOverallResult;
            // get the transformed, but not weighted, result of the winning alternative for this criterion 
            double winnerResult = pLeaf.getAlternativeResultsAsMap().get(planInfo.getWinningAlternative());
            // calculate the impact of this criterion on the final result
            double overallImpact = winnerResult * pLeaf.getTotalWeight();
            // now we have to calculate the overall impact of the minimum possible value
            
            
            if (overallImpact > overallDiffToNext) {
                // this criterion could be a game changer - determine how much it can change, without impact on selecting the winner
                // and map it back to target value scale 
                double targetDiff = (overallImpact - overallDiffToNext) / pLeaf.getTotalWeight();
                double minWinnerTargetResult = winnerResult - targetDiff; 
                // this target result needs to be mapped back to an evaluation value 
                
            } else {
                // this criterion cannot change the winner
                importanceFactors.put(pLeaf, 0.0);
            }
            
            
        }
        
        return 0.0;
    }

    /**
     * Method responsible for filtering all leaves which match the given criterion (Property [+ Metric]).
     * Validity check of the input parameters (e.g. property + metric must be measurable) is done in calling code!!
     * 
     * @param cMeasurableProperty property to filter.
     * @param cMetric metric to filter.
     */
    public List<VPlanLeaf> getCriterionPlanLeaves(MeasurableProperty cMeasurableProperty, Metric cMetric) {      
        List<VPlanLeaf> criterionPlanLeaves = new ArrayList<VPlanLeaf>();
        
        // filter PlanLeaves
        for (VPlanLeaf l : mappedPlanLeaves) {
            MeasurableProperty lProperty = l.getCriterion().getProperty();
            Metric lMetric = l.getCriterion().getMetric();

            // property and metric must match
            if ((cMeasurableProperty.getPropertyId().equals(lProperty.getPropertyId())) &&
                ((cMetric == null && lMetric == null)
                        || (lMetric != null && cMetric != null && cMetric.getMetricId().equals(lMetric.getMetricId())))) {
                    criterionPlanLeaves.add(l);
                }
        }
        
        return criterionPlanLeaves;
    }
    
    /**
     * Exports the current importance table to a csv file.
     */
    public void exportTableToCsv() {
        
    }
    
    /**
     * Calculates the average potential output range based on the CPL occurrence.
     * 
     * @param criterionPlanLeaves corresponding plan leaves.
     * @return average potential output range based on the CPL occurrence, if calculation is possible.
     *         If calculation is not possible -1 is returned.
     */
    /*
    public double getCPLAvgPotentialOutputRangeOccurrenceBased(List<VPlanLeaf> criterionPlanLeaves) {
        int appropriateLeaves = 0;
        double porSum = 0;
        
        for (VPlanLeaf l : criterionPlanLeaves) {
            double por = l.getPotentialOutputRange();
            
            // only leaves for which the por can be calculated can be used
            if (por != -1) {
                porSum = porSum + por;
                appropriateLeaves++;
            }
        }
        
        if (appropriateLeaves == 0) {
            return -1;
        }
        
        double avgPor = porSum / appropriateLeaves;
        return avgPor;
    }
    */
    
    /**
     * Calculates the average actual output range based on the CPL occurrence.
     * 
     * @param criterionPlanLeaves corresponding plan leaves.
     * @return average actual output range based on the CPL occurrence, if calculation is possible.
     *         If calculation is not possible -1 is returned.
     */
    /*
    public double getCPLAvgActualOutputRangeOccurrenceBased(List<VPlanLeaf> criterionPlanLeaves) {
        int appropriateLeaves = 0;
        double aorSum = 0;
        
        for (VPlanLeaf l : criterionPlanLeaves) {
            double aor = l.getActualOutputRange();
            
            // only leaves for which the aor can be calculated can be used
            if (aor != -1) {
                aorSum = aorSum + aor;
                appropriateLeaves++;
            }
        }
        
        if (appropriateLeaves == 0) {
            return -1;
        }
        
        double avgAor = aorSum / appropriateLeaves;
        return avgAor;
    }
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
