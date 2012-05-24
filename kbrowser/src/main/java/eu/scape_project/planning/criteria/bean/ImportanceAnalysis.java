package eu.scape_project.planning.criteria.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.model.kbrowser.CriteriaLeaf;
import eu.scape_project.planning.model.kbrowser.VPlanLeaf;
import eu.scape_project.planning.model.measurement.MeasurableProperty;
import eu.scape_project.planning.model.measurement.Metric;

public class ImportanceAnalysis implements Serializable {
    private static final long serialVersionUID = -814054847584527659L;
    private static final Logger log = LoggerFactory.getLogger(ImportanceAnalysis.class);
    
    List<VPlanLeaf> planLeaves = new ArrayList<VPlanLeaf>();
    List<VPlanLeaf> mappedPlanLeaves = new ArrayList<VPlanLeaf>();
    
    private Long nrRelevantPlans;
    
    private List<ImportanceAnalysisProperty> tableRows;
    private int tableRowsCount = 0;
        
    
    public ImportanceAnalysis(Collection<MeasurableProperty> allMeasurableProperties, List<VPlanLeaf> planLeaves, Long nrRelevantPlans) {
        this.planLeaves = planLeaves;
        filterMappedLeaves();
        
        this.nrRelevantPlans = nrRelevantPlans;
        
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
            
            tableRows.add(importanceAnalysisProperty);
        }
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
