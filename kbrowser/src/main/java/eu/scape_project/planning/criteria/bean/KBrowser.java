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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import eu.scape_project.planning.criteria.bean.CriterionSelector.ChangeListener;
import eu.scape_project.planning.criteria.bean.data.DiagramData;
import eu.scape_project.planning.criteria.bean.data.PotentialToRangeMaxData;
import eu.scape_project.planning.manager.PlanManager;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.aggregators.WeightedSum;
import eu.scape_project.planning.model.beans.ResultNode;
import eu.scape_project.planning.model.kbrowser.VPlanLeaf;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.scales.FloatRangeScale;
import eu.scape_project.planning.model.scales.FloatScale;
import eu.scape_project.planning.model.scales.IntRangeScale;
import eu.scape_project.planning.model.scales.IntegerScale;
import eu.scape_project.planning.model.scales.PositiveFloatScale;
import eu.scape_project.planning.model.scales.PositiveIntegerScale;
import eu.scape_project.planning.model.scales.Scale;

import org.richfaces.component.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Backing bean responsible for supporting view/kbrowser.xhtml
 * 
 * Attention: This class requires additional database-objects to work: - stored
 * procedures: calculateRelativeWeight, calculateRootNode - stored functions:
 * relativeWeight, rootNode - views: VPlanLeaf These are defined in
 * "resources/META-INF/auxiliaryDbObjects.hbm.xml" and are created every time
 * persistence-unit creation-strategy is set to "create-drop" or "create".
 * 
 * @author Markus Hamm
 */
@SessionScoped
@Named("kBrowser")
public class KBrowser implements Serializable {

    private static final long serialVersionUID = 4713056518876377374L;
    private static final Logger log = LoggerFactory.getLogger(KBrowser.class);

    @Inject
    private PlanManager planManager;

    @Inject
    private PlanSelection planSelection;

    // ---- variables for selection ----
    private List<PlanInfo> selectedPlans = new ArrayList<PlanInfo>();

    @Inject
    private CriterionSelector criterionSelector;

    // ---- variables for view ----
    private KBrowserCalculator calculator;

    private Boolean isGeneralPartCalculated = false;

    /**
     * relevant plans are plans with planstate>=11(EXPERIMENT_DEFINED) AND name
     * NOT LIKE "MY DEMO PLAN%"
     */
    private Long nrRelevantPlans = -1l;
    private int nrPlanLeaves = -1;
    private int nrMappedPlanLeaves = -1;
    private int nrOverallCriteria;
    private int nrCriteriaUsedAtLeastOnce = -1;

    private Boolean isCategorySelected = false;
    private int nrPlanLeavesInCategory = -1;
    private Boolean isAttributeSelected = false;
    private int nrPlanLeavesUsingAttribute = -1;
    private Boolean isMeasureSelected = false;
    private int nrCriterionPlanLeaves = -1;
    private double cplAverageWeight = -1;
    private double cplAverageTotalWeight = -1;
    private int nrCPLPotentialKO = -1;
    private int nrCPLActualKO = -1;
    private int nrCPLMeasurementsObtained = -1;
    private int nrCPLEvaluations = -1;
    private Boolean hasCriterionNumericScale = false;
    private double cplNumericMeasurementsMin = -1d;
    private double cplNumericMeasurementsMax = -1d;
    private double cplNumericMeasurementsAvg = -1d;
    private Boolean hasCriterionOrdinalScale = false;
    private Map<String, Integer> cplOrdinalMeasurements = new HashMap<String, Integer>();
    private KBrowserTransformerTable transformerTable;

    /**
     * Sort orders for big criterion impact table
     */
    private SortOrder[] cifIfSortOrder = {SortOrder.unsorted, SortOrder.unsorted, SortOrder.descending,
        SortOrder.unsorted, SortOrder.unsorted, SortOrder.unsorted, SortOrder.unsorted, SortOrder.unsorted,
        SortOrder.unsorted, SortOrder.unsorted, SortOrder.unsorted, SortOrder.unsorted, SortOrder.unsorted,
        SortOrder.unsorted, SortOrder.unsorted, SortOrder.unsorted, SortOrder.unsorted, SortOrder.unsorted,
        SortOrder.unsorted, SortOrder.unsorted};

    /**
     * Sort orders for compact criterion impact table
     */
    private SortOrder[] cifIfSortOrderCompact = {SortOrder.unsorted, SortOrder.unsorted, SortOrder.descending,
        SortOrder.unsorted, SortOrder.unsorted, SortOrder.unsorted};

    private double cplAvgPotentialOutputRangeOccurrenceBased = -1;
    private double cplMaxPotentialOutputRange = -1;
    private double cplMinPotentialOutputRange = -1;
    private double cplAvgActualOutputRangeOccurrenceBased = -1;
    private double cplMaxActualOutputRange = -1;
    private double cplMinActualOutputRange = -1;

    private double cplIf1;
    private double cplIf2;
    private double cplIf3;
    private double cplIf4;
    private double cplIf5;
    private double cplIf6;
    private double cplIf7;
    private double cplIf8;
    private double cplIf9;
    private double cplIf10;
    private double cplIf11;
    private double cplIf12;
    private double cplIf13;
    private double cplIf14;
    private double cplIf15;
    private double cplIf16;
    private double cplIf17;
    private double cplIf18;

    private boolean measureDominated;

    // ---- chart ----
    private Boolean hasCriterionEvaluations = false;
    private int evaluatedValuesZero = 0;
    private int evaluatedValuesToOne = 0;
    private int evaluatedValuesToTwo = 0;
    private int evaluatedValuesToThree = 0;
    private int evaluatedValuesToFour = 0;
    private int evaluatedValuesToFive = 0;

    private double potentialToRangeScale = 2.0d;

    // ---- importance analysis ----
    private ImportanceAnalysis importanceAnalysis;

    private DominatedSetCalculator dominatedSetCalculator;

    public KBrowser() {
    }

    public void initBean() {
        criterionSelector.init();
        criterionSelector.addChangeListener(new ChangeListener() {
            @Override
            public void selected() {
                checkInputAndCalculateSelectiveStatistics();
            }
        });

        nrPlanLeaves = 0;
        nrMappedPlanLeaves = 0;
        nrCriteriaUsedAtLeastOnce = 0;
        isGeneralPartCalculated = false;

        checkInputAndCalculateSelectiveStatistics();
    }

    /**
     * Initializes data for calculations.
     */
    public void initData() {
        // Get data
        nrRelevantPlans = (long) planSelection.getSelectedPlans().size();
        List<VPlanLeaf> planLeaves = planSelection.getSelectionPlanLeaves();

        selectedPlans.clear();
        // We also need the scores of the alternatives - for each selected plan
        for (int pId : planSelection.getSelectedPlans()) {
            Plan plan = planManager.loadPlan(pId);
            ResultNode result = new ResultNode(plan.getTree().getRoot(), new WeightedSum(), plan
                .getAlternativesDefinition().getConsideredAlternatives());
            selectedPlans.add(new PlanInfo(pId, result));
        }
        // Init calculation classes
        this.calculator = new KBrowserCalculator(planLeaves, nrRelevantPlans);

        HashMap<String, Measure> usedMeasures = getUsedMeasures(planLeaves);
        criterionSelector.filterCriteria(usedMeasures.keySet());

        importanceAnalysis = new ImportanceAnalysis(usedMeasures.values(), planLeaves, selectedPlans);

        dominatedSetCalculator = new DominatedSetCalculator(selectedPlans, planLeaves);
        // dominatedSetCalculator.calculateDominatedPowerSet();

        // Do calculations
        // Calculate maximum scale for potential-to-range diagram
        calculatePotentialToRangeScale();
        calculateGeneralStatistics();
        checkInputAndCalculateSelectiveStatistics();
    }

    /**
     * Returns measures used in the provided plan leaves.
     * 
     * @param planLeaves
     *            Plan leaves to use as filter
     * @return a map of measure keys with the corresponding measure
     */
    private HashMap<String, Measure> getUsedMeasures(List<VPlanLeaf> planLeaves) {

        HashMap<String, Measure> usedMeasures = new HashMap<String, Measure>();

        for (VPlanLeaf l : planLeaves) {
            if (l.isMapped()) {
                usedMeasures.put(l.getMeasure().getUri(), l.getMeasure());
            }
        }

        return usedMeasures;
    }

    /**
     * Updates statistics for the currently selected measure.
     */
    public void measureChanged() {
        checkInputAndCalculateSelectiveStatistics();
    }

    /* ----------------- Calculator ----------------- */
    /**
     * Method responsible for calculating general statistics (independent from
     * user selection).
     */
    public void calculateGeneralStatistics() {
        nrPlanLeaves = calculator.getNrPlanLeaves();
        nrMappedPlanLeaves = calculator.getNrMappedPlanLeaves();
        nrCriteriaUsedAtLeastOnce = calculator.getNrCriteriaUsedAtLeastOnce();
        isGeneralPartCalculated = true;
    }

    /**
     * Method responsible for calculating selective statistics based on user
     * selection.
     */
    private void checkInputAndCalculateSelectiveStatistics() {
        // calculate category dependent values
        if (criterionSelector.getSelectedCategory() != null) {
            isCategorySelected = true;
            nrPlanLeavesInCategory = calculator.getNrPlanLeavesInCategory(criterionSelector.getSelectedCategory());
        } else {
            isCategorySelected = false;
        }

        // calculate property dependent values
        if (criterionSelector.getSelectedAttribute() != null) {
            isAttributeSelected = true;
            nrPlanLeavesUsingAttribute = calculator.getNrPlanLeavesUsingProperty(criterionSelector
                .getSelectedAttribute());
        } else {
            isAttributeSelected = false;
        }

        // FIXME: a criterion is ALWAYS measurable (else it wouldn't be a
        // criterion, right?)
        // but: a selected measurable property itself might not be related to a
        // criterion
        // so the following comment does not maḱe sense:
        // check if selected criterion is measurable - if so calculate values.
        // if the property has a metric assigned it is always measurable.
        // if the property has no metric assigned it is measurable if the
        // property has a Scale assigned.
        // if ((selectedMeasurableProperty != null && selectedMetric != null)
        // || (selectedMeasurableProperty != null &&
        // selectedMeasurableProperty.getScale() != null)) {
        if (criterionSelector.getSelectedMeasure() != null) {
            isMeasureSelected = true;
            calculator.filterMeasure(criterionSelector.getSelectedMeasure());
            nrCriterionPlanLeaves = calculator.getNrCriterionPlanLeaves();
            log.debug("nrCriterionPlanLeaves: " + nrCriterionPlanLeaves);
            cplAverageWeight = calculator.getCPLAverageWeight();
            nrCPLMeasurementsObtained = calculator.getNrCPLMeasurementsObtained();
            nrCPLEvaluations = calculator.getCPLEvaluations().size();
            transformerTable = calculator.getCPSTransformerTable();
            log.debug("TRANSFORMERTABLE-TargetValues: " + transformerTable.getTargetValues().size());

            // calculate impact factors
            cplIf1 = calculator.getCPL_IF1();
            cplIf2 = calculator.getCPL_IF2() * 100;
            cplIf3 = calculator.getCPL_IF3();
            cplIf4 = calculator.getCPL_IF4();
            cplIf5 = calculator.getCPL_IF5();
            cplIf6 = calculator.getCPL_IF6();
            cplIf7 = calculator.getCPL_IF7();
            cplIf8 = calculator.getCPL_IF8();
            cplIf9 = calculator.getCPL_IF9();
            cplIf10 = calculator.getCPL_IF10();
            cplIf11 = calculator.getCPL_IF11();
            cplIf12 = calculator.getCPL_IF12();
            cplIf13 = calculator.getCPL_IF13();
            cplIf14 = calculator.getCPL_IF14() * 100;
            cplIf15 = calculator.getCPL_IF15();
            cplIf16 = calculator.getCPL_IF16() * 100;
            cplIf17 = calculator.getCPL_IF17();
            cplIf18 = calculator.getCPL_IF18() * 100;

            // Get scale
            Scale scale = criterionSelector.getSelectedMeasure().getScale();

            // If scale of selected property is numeric - calculate numeric
            // measurement-statistics.
            if ((scale instanceof FloatRangeScale) || (scale instanceof FloatScale) || (scale instanceof IntegerScale)
                || (scale instanceof IntRangeScale) || (scale instanceof PositiveFloatScale)
                || (scale instanceof PositiveIntegerScale)) {
                hasCriterionNumericScale = true;
                hasCriterionOrdinalScale = false;
                cplNumericMeasurementsMin = calculator.getCPLNumericMeasurementsMin();
                cplNumericMeasurementsMax = calculator.getCPLNumericMeasurementsMax();
                cplNumericMeasurementsAvg = calculator.getCPLNumericMeasurementsAvg();
            }
            // Otherwise calculate ordinal measurement-statistics.
            else {
                hasCriterionNumericScale = false;
                hasCriterionOrdinalScale = true;
                cplOrdinalMeasurements = calculator.getCPLOrdinalMeasurements();
            }

            // Handle transformed values available
            List<Double> criterionEvaluations = calculator.getCPLEvaluations();
            if (criterionEvaluations.size() > 0) {
                hasCriterionEvaluations = true;
                groupTransformedValues(criterionEvaluations);
            } else {
                hasCriterionEvaluations = false;
            }

            // Dominated
            measureDominated = dominatedSetCalculator.isCriterionDominated(criterionSelector.getSelectedMeasure());
        } else {
            isMeasureSelected = false;
            hasCriterionEvaluations = false;
        }
    }

    /**
     * Method responsible for grouping the transformed values in 6 groups and
     * update the relevant class variables which are used by a chart to
     * visualize statistics.
     * 
     * @param transformedValues
     *            transformed values to separate.
     */
    private void groupTransformedValues(List<Double> transformedValues) {
        evaluatedValuesZero = 0;
        evaluatedValuesToOne = 0;
        evaluatedValuesToTwo = 0;
        evaluatedValuesToThree = 0;
        evaluatedValuesToFour = 0;
        evaluatedValuesToFive = 0;

        for (Double value : transformedValues) {
            if (value == 0) {
                evaluatedValuesZero++;
            }
            if (value > 0 && value <= 1) {
                evaluatedValuesToOne++;
            }
            if (value > 1 && value <= 2) {
                evaluatedValuesToTwo++;
            }
            if (value > 2 && value <= 3) {
                evaluatedValuesToThree++;
            }
            if (value > 3 && value <= 4) {
                evaluatedValuesToFour++;
            }
            if (value > 4 && value <= 5) {
                evaluatedValuesToFive++;
            }
        }
    }

    /* ----------------- Init ----------------- */

    public String init() {
        log.debug("init finally called");
        initBean();
        // initData();
        return "kbrowser.jsf";
    }

    /* ----------------- Getter/Setter ----------------- */

    public void setNrPlanLeaves(int nrPlanLeaves) {
        this.nrPlanLeaves = nrPlanLeaves;
    }

    public int getNrPlanLeaves() {
        return nrPlanLeaves;
    }

    public void setNrMappedPlanLeaves(int nrMappedPlanLeaves) {
        this.nrMappedPlanLeaves = nrMappedPlanLeaves;
    }

    public int getNrMappedPlanLeaves() {
        return nrMappedPlanLeaves;
    }

    public void setIsGeneralPartCalculated(Boolean isGeneralPartCalculated) {
        this.isGeneralPartCalculated = isGeneralPartCalculated;
    }

    public Boolean getIsGeneralPartCalculated() {
        return isGeneralPartCalculated;
    }

    public void setNrRelevantPlans(Long nrRelevantPlans) {
        this.nrRelevantPlans = nrRelevantPlans;
    }

    public Long getNrRelevantPlans() {
        return nrRelevantPlans;
    }

    public void setNrCriterionPlanLeaves(int nrCriterionPlanLeaves) {
        this.nrCriterionPlanLeaves = nrCriterionPlanLeaves;
    }

    public int getNrCriterionPlanLeaves() {
        return nrCriterionPlanLeaves;
    }

    public void setCplAverageWeight(double cplAverageWeight) {
        this.cplAverageWeight = cplAverageWeight;
    }

    public double getCplAverageWeight() {
        return cplAverageWeight;
    }

    public void setCplAverageTotalWeight(double cplAverageTotalWeight) {
        this.cplAverageTotalWeight = cplAverageTotalWeight;
    }

    public double getCplAverageTotalWeight() {
        return cplAverageTotalWeight;
    }

    public void setIsCategorySelected(Boolean isCategorySelected) {
        this.isCategorySelected = isCategorySelected;
    }

    public Boolean getIsCategorySelected() {
        return isCategorySelected;
    }

    public void setNrPlanLeavesInCategory(int nrPlanLeavesInCategory) {
        this.nrPlanLeavesInCategory = nrPlanLeavesInCategory;
    }

    public int getNrPlanLeavesInCategory() {
        return nrPlanLeavesInCategory;
    }

    public void setIsAttributeSelected(Boolean isAttributeSelected) {
        this.isAttributeSelected = isAttributeSelected;
    }

    public Boolean getIsAttributeSelected() {
        return isAttributeSelected;
    }

    public void setNrPlanLeavesUsingAttribute(int nrPlanLeavesUsingAttribute) {
        this.nrPlanLeavesUsingAttribute = nrPlanLeavesUsingAttribute;
    }

    public int getNrPlanLeavesUsingAttribute() {
        return nrPlanLeavesUsingAttribute;
    }

    public void setIsMeasureSelected(Boolean isMeasureSelected) {
        this.isMeasureSelected = isMeasureSelected;
    }

    public Boolean getIsMeasureSelected() {
        return isMeasureSelected;
    }

    public void setNrCPLPotentialKO(int nrCPLPotentialKO) {
        this.nrCPLPotentialKO = nrCPLPotentialKO;
    }

    public int getNrCPLPotentialKO() {
        return nrCPLPotentialKO;
    }

    public void setNrCPLActualKO(int nrCPLActualKO) {
        this.nrCPLActualKO = nrCPLActualKO;
    }

    public int getNrCPLActualKO() {
        return nrCPLActualKO;
    }

    public void setNrCPLMeasurementsObtained(int nrCPLMeasurementsObtained) {
        this.nrCPLMeasurementsObtained = nrCPLMeasurementsObtained;
    }

    public int getNrCPLMeasurementsObtained() {
        return nrCPLMeasurementsObtained;
    }

    public void setCplNumericMeasurementsMin(double cplNumericMeasurementsMin) {
        this.cplNumericMeasurementsMin = cplNumericMeasurementsMin;
    }

    public double getCplNumericMeasurementsMin() {
        return cplNumericMeasurementsMin;
    }

    public void setCplNumericMeasurementsMax(double cplNumericMeasurementsMax) {
        this.cplNumericMeasurementsMax = cplNumericMeasurementsMax;
    }

    public double getCplNumericMeasurementsMax() {
        return cplNumericMeasurementsMax;
    }

    public void setCplNumericMeasurementsAvg(double cplNumericMeasurementsAvg) {
        this.cplNumericMeasurementsAvg = cplNumericMeasurementsAvg;
    }

    public double getCplNumericMeasurementsAvg() {
        return cplNumericMeasurementsAvg;
    }

    public void setCplOrdinalMeasurements(Map<String, Integer> cplOrdinalMeasurements) {
        this.cplOrdinalMeasurements = cplOrdinalMeasurements;
    }

    public Map<String, Integer> getCplOrdinalMeasurements() {
        return cplOrdinalMeasurements;
    }

    public List<Map.Entry<String, Integer>> getCplOrdinalMeasurementsList() {
        return new ArrayList<Map.Entry<String, Integer>>(cplOrdinalMeasurements.entrySet());
    }

    public void setHasCriterionNumericScale(Boolean hasCriterionNumericScale) {
        this.hasCriterionNumericScale = hasCriterionNumericScale;
    }

    public Boolean getHasCriterionNumericScale() {
        return hasCriterionNumericScale;
    }

    public void setHasCriterionOrdinalScale(Boolean hasCriterionOrdinalScale) {
        this.hasCriterionOrdinalScale = hasCriterionOrdinalScale;
    }

    public Boolean getHasCriterionOrdinalScale() {
        return hasCriterionOrdinalScale;
    }

    public void setTransformerTable(KBrowserTransformerTable transformerTable) {
        this.transformerTable = transformerTable;
    }

    public KBrowserTransformerTable getTransformerTable() {
        return transformerTable;
    }

    public void setNrCriteriaUsedAtLeastOnce(int nrCriteriaUsedAtLeastOnce) {
        this.nrCriteriaUsedAtLeastOnce = nrCriteriaUsedAtLeastOnce;
    }

    public int getNrCriteriaUsedAtLeastOnce() {
        return nrCriteriaUsedAtLeastOnce;
    }

    public void setHasCriterionEvaluations(Boolean hasCriterionEvaluations) {
        this.hasCriterionEvaluations = hasCriterionEvaluations;
    }

    public Boolean getHasCriterionEvaluations() {
        return hasCriterionEvaluations;
    }

    public void setCplAvgPotentialOutputRangeOccurrenceBased(double cplAvgPotentialOutputRangeOccurrenceBased) {
        this.cplAvgPotentialOutputRangeOccurrenceBased = cplAvgPotentialOutputRangeOccurrenceBased;
    }

    public double getCplAvgPotentialOutputRangeOccurrenceBased() {
        return cplAvgPotentialOutputRangeOccurrenceBased;
    }

    public void setCplMaxPotentialOutputRange(double cplMaxPotentialOutputRange) {
        this.cplMaxPotentialOutputRange = cplMaxPotentialOutputRange;
    }

    public double getCplMaxPotentialOutputRange() {
        return cplMaxPotentialOutputRange;
    }

    public void setCplMinPotentialOutputRange(double cplMinPotentialOutputRange) {
        this.cplMinPotentialOutputRange = cplMinPotentialOutputRange;
    }

    public double getCplMinPotentialOutputRange() {
        return cplMinPotentialOutputRange;
    }

    public void setCplAvgActualOutputRangeOccurrenceBased(double cplAvgActualOutputRangeOccurrenceBased) {
        this.cplAvgActualOutputRangeOccurrenceBased = cplAvgActualOutputRangeOccurrenceBased;
    }

    public double getCplAvgActualOutputRangeOccurrenceBased() {
        return cplAvgActualOutputRangeOccurrenceBased;
    }

    public void setCplMaxActualOutputRange(double cplMaxActualOutputRange) {
        this.cplMaxActualOutputRange = cplMaxActualOutputRange;
    }

    public double getCplMaxActualOutputRange() {
        return cplMaxActualOutputRange;
    }

    public void setCplMinActualOutputRange(double cplMinActualOutputRange) {
        this.cplMinActualOutputRange = cplMinActualOutputRange;
    }

    public double getCplMinActualOutputRange() {
        return cplMinActualOutputRange;
    }

    public void setImportanceAnalysis(ImportanceAnalysis importanceAnalysis) {
        this.importanceAnalysis = importanceAnalysis;
    }

    public ImportanceAnalysis getImportanceAnalysis() {
        return importanceAnalysis;
    }

    public void setNrOverallCriteria(int nrOverallCriteria) {
        this.nrOverallCriteria = nrOverallCriteria;
    }

    public int getNrOverallCriteria() {
        return nrOverallCriteria;
    }

    public void setCplIf1(double cplIf1) {
        this.cplIf1 = cplIf1;
    }

    public double getCplIf1() {
        return cplIf1;
    }

    public void setCplIf2(double cplIf2) {
        this.cplIf2 = cplIf2;
    }

    public double getCplIf2() {
        return cplIf2;
    }

    public void setCplIf3(double cplIf3) {
        this.cplIf3 = cplIf3;
    }

    public double getCplIf3() {
        return cplIf3;
    }

    public void setCplIf4(double cplIf4) {
        this.cplIf4 = cplIf4;
    }

    public double getCplIf4() {
        return cplIf4;
    }

    public void setCplIf5(double cplIf5) {
        this.cplIf5 = cplIf5;
    }

    public double getCplIf5() {
        return cplIf5;
    }

    public void setCplIf6(double cplIf6) {
        this.cplIf6 = cplIf6;
    }

    public double getCplIf6() {
        return cplIf6;
    }

    public void setCplIf7(double cplIf7) {
        this.cplIf7 = cplIf7;
    }

    public double getCplIf7() {
        return cplIf7;
    }

    public void setCplIf8(double cplIf8) {
        this.cplIf8 = cplIf8;
    }

    public double getCplIf8() {
        return cplIf8;
    }

    public void setCplIf9(double cplIf9) {
        this.cplIf9 = cplIf9;
    }

    public double getCplIf9() {
        return cplIf9;
    }

    public void setCplIf10(double cplIf10) {
        this.cplIf10 = cplIf10;
    }

    public double getCplIf10() {
        return cplIf10;
    }

    public void setCplIf11(double cplIf11) {
        this.cplIf11 = cplIf11;
    }

    public double getCplIf11() {
        return cplIf11;
    }

    public void setCplIf12(double cplIf12) {
        this.cplIf12 = cplIf12;
    }

    public double getCplIf12() {
        return cplIf12;
    }

    public void setCplIf13(double cplIf13) {
        this.cplIf13 = cplIf13;
    }

    public double getCplIf13() {
        return cplIf13;
    }

    public void setCplIf14(double cplIf14) {
        this.cplIf14 = cplIf14;
    }

    public double getCplIf14() {
        return cplIf14;
    }

    public void setCplIf15(double cplIf15) {
        this.cplIf15 = cplIf15;
    }

    public double getCplIf15() {
        return cplIf15;
    }

    public void setCplIf16(double cplIf16) {
        this.cplIf16 = cplIf16;
    }

    public double getCplIf16() {
        return cplIf16;
    }

    public void setCplIf17(double cplIf17) {
        this.cplIf17 = cplIf17;
    }

    public double getCplIf17() {
        return cplIf17;
    }

    public void setCplIf18(double cplIf18) {
        this.cplIf18 = cplIf18;
    }

    public double getCplIf18() {
        return cplIf18;
    }

    public boolean isMeasureDominated() {
        return measureDominated;
    }

    public void setMeasureDominated(boolean measureDominated) {
        this.measureDominated = measureDominated;
    }

    public void setEvaluatedValuesZero(int evaluatedValuesZero) {
        this.evaluatedValuesZero = evaluatedValuesZero;
    }

    public int getEvaluatedValuesZero() {
        return evaluatedValuesZero;
    }

    public void setEvaluatedValuesToOne(int evaluatedValuesToOne) {
        this.evaluatedValuesToOne = evaluatedValuesToOne;
    }

    public int getEvaluatedValuesToOne() {
        return evaluatedValuesToOne;
    }

    public void setEvaluatedValuesToTwo(int evaluatedValuesToTwo) {
        this.evaluatedValuesToTwo = evaluatedValuesToTwo;
    }

    public int getEvaluatedValuesToTwo() {
        return evaluatedValuesToTwo;
    }

    public void setEvaluatedValuesToThree(int evaluatedValuesToThree) {
        this.evaluatedValuesToThree = evaluatedValuesToThree;
    }

    public int getEvaluatedValuesToThree() {
        return evaluatedValuesToThree;
    }

    public void setEvaluatedValuesToFour(int evaluatedValuesToFour) {
        this.evaluatedValuesToFour = evaluatedValuesToFour;
    }

    public int getEvaluatedValuesToFour() {
        return evaluatedValuesToFour;
    }

    public void setEvaluatedValuesToFive(int evaluatedValuesToFive) {
        this.evaluatedValuesToFive = evaluatedValuesToFive;
    }

    public int getEvaluatedValuesToFive() {
        return evaluatedValuesToFive;
    }

    public HashMap<String, Integer> getScoreDistribution() {
        HashMap<String, Integer> data = new HashMap<String, Integer>();
        data.put("scores0", evaluatedValuesZero);
        data.put("scores1", evaluatedValuesToOne);
        data.put("scores2", evaluatedValuesToTwo);
        data.put("scores3", evaluatedValuesToThree);
        data.put("scores4", evaluatedValuesToFour);
        data.put("scores5", evaluatedValuesToFive);
        return data;
    }

    public void setNrCPLEvaluations(int nrCPLEvaluations) {
        this.nrCPLEvaluations = nrCPLEvaluations;
    }

    public int getNrCPLEvaluations() {
        return nrCPLEvaluations;
    }

    public SortOrder[] getCifIfSortOrder() {
        return cifIfSortOrder;
    }

    public void setCifIfSortOrder(SortOrder[] cifIfSortOrder) {
        this.cifIfSortOrder = cifIfSortOrder;
    }

    /**
     * Sets the sort order of the specified column for the full impact table.
     * 
     * @param column
     *            Column index starting from 0.
     */
    public void sortCifByColumn(long lcolumn) {
        int column = (int) lcolumn;
        log.debug("Sorting Criterion Impact Factors by IF" + column);
        SortOrder currentColumn = cifIfSortOrder[column];
        clearCifSortOrders();
        if (currentColumn.equals(SortOrder.descending)) {
            cifIfSortOrder[column] = SortOrder.ascending;
        } else {
            cifIfSortOrder[column] = SortOrder.descending;
        }
    }

    /**
     * Clears the sort orders for all columns of the full CIF table.
     */
    private void clearCifSortOrders() {
        for (int i = 0; i < cifIfSortOrder.length; i++) {
            cifIfSortOrder[i] = SortOrder.unsorted;
        }
    }

    public SortOrder[] getCifIfSortOrderCompact() {
        return cifIfSortOrderCompact;
    }

    public void setCifIfSortOrderCompact(SortOrder[] cifIfSortOrderCompact) {
        this.cifIfSortOrderCompact = cifIfSortOrderCompact;
    }

    public void exportImpactFactorsToCSV() {
        StringBuilder csvBuf = new StringBuilder();
        csvBuf
            .append("Category; Criterion; IF1; IF2; IF3;IF4; IF5; IF6; IF7; IF8; IF9; IF10; IF11;IF12; IF13; IF14; IF15; IF16; IF17; IF18; IF19\n");

        for (ImportanceAnalysisProperty p : importanceAnalysis.getTableRows()) {
            csvBuf.append(p.getCategory()).append(";");
            csvBuf.append(p.getAttribute() + " " + p.getMeasure()).append(";");
            csvBuf.append(p.getIf1()).append(";");
            csvBuf.append(p.getIf2()).append(";");
            csvBuf.append(p.getIf3()).append(";");
            csvBuf.append(p.getIf4()).append(";");
            csvBuf.append(p.getIf5()).append(";");
            csvBuf.append(p.getIf6()).append(";");
            csvBuf.append(p.getIf7()).append(";");
            csvBuf.append(p.getIf8()).append(";");
            csvBuf.append(p.getIf9()).append(";");
            csvBuf.append(p.getIf10()).append(";");
            csvBuf.append(p.getIf11()).append(";");
            csvBuf.append(p.getIf12()).append(";");
            csvBuf.append(p.getIf13()).append(";");
            csvBuf.append(p.getIf14()).append(";");
            csvBuf.append(p.getIf15()).append(";");
            csvBuf.append(p.getIf16()).append(";");
            csvBuf.append(p.getIf17()).append(";");
            csvBuf.append(p.getIf18()).append(";");
            csvBuf.append(p.getIf19());
            csvBuf.append("\n");
        }

        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
            .getResponse();
        response.setHeader("Content-disposition", "attachment; filename= ImpactFactors.csv");
        response.setContentLength(csvBuf.length());
        response.setContentType("application/vnd.ms-excel");

        try {
            Writer writer = new OutputStreamWriter(response.getOutputStream());
            writer.append(csvBuf);
            writer.flush();
            writer.close();
            context.responseComplete();
            log.debug("Exported impact factors successfully to CSV-File.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Sets the sort order of the specified column for the compact impact table.
     * 
     * @param column
     *            Column index starting from 0.
     */
    public void sortCifByColumnCompact(long lcolumn) {
        int column = (int) lcolumn;

        log.debug("Sorting compact Criterion Impact Factors by IF" + Integer.toString(column));
        SortOrder currentColumn = cifIfSortOrderCompact[column];
        clearCifSortOrdersCompact();
        if (currentColumn.equals(SortOrder.descending)) {
            cifIfSortOrderCompact[column] = SortOrder.ascending;
        } else {
            cifIfSortOrderCompact[column] = SortOrder.descending;
        }
    }

    /**
     * Clears the sort orders for all columns of the compact CIF table.
     */
    private void clearCifSortOrdersCompact() {
        for (int i = 0; i < cifIfSortOrderCompact.length; i++) {
            cifIfSortOrderCompact[i] = SortOrder.unsorted;
        }
    }

    /**
     * Returns a data object that holds data that shows the relation of the
     * potential to range for the selected criterion.
     * 
     * @return The data object
     */
    public DiagramData getPotentialToRangeData() {

        // Create data object
        PotentialToRangeMaxData data = new PotentialToRangeMaxData();
        // Title
        data.setTitle("Potential to range");

        data.setMaxValue(potentialToRangeScale);
        data.setFormatString("%.3f");

        // Label of series
        ArrayList<String> seriesLabels = new ArrayList<String>();
        seriesLabels.add("IF9: Maximum Potential");
        seriesLabels.add("IF5: Potential");
        seriesLabels.add("IF10: Maximum Range");
        seriesLabels.add("IF6: Range");

        // Data lists for criteria hierarchies
        ArrayList<Double> seriesData = new ArrayList<Double>();

        // Add data
        seriesData.add(getCplIf9());
        seriesData.add(getCplIf5());
        seriesData.add(getCplIf10());
        seriesData.add(getCplIf6());

        // Set data
        data.setSeriesLabels(seriesLabels);
        data.setSeriesData(seriesData);
        return data;
    }

    /**
     * Calculates the maximum scale value for the potential-to-range diagram
     * 
     * @return the scale value
     */
    private void calculatePotentialToRangeScale() {

        double maxIf9 = 0.0d;
        double maxIf10 = 0.0d;

        ImportanceAnalysis importanceAnalysis = getImportanceAnalysis();

        for (ImportanceAnalysisProperty property : importanceAnalysis.getTableRows()) {
            maxIf9 = Math.max(maxIf9, property.getIf9());
            maxIf10 = Math.max(maxIf10, property.getIf10());
        }

        double scaleValue = Math.max(maxIf9, maxIf10) + 0.12d;

        potentialToRangeScale = new BigDecimal(scaleValue, new MathContext(2)).doubleValue();
    }

    public List<PlanInfo> getSelectedPlans() {
        return selectedPlans;
    }

    public void setSelectedPlans(List<PlanInfo> selectedPlans) {
        this.selectedPlans = selectedPlans;
    }

    public CriterionSelector getCriterionSelector() {
        return criterionSelector;
    }

    public void setCriterionSelector(CriterionSelector criterionSelector) {
        this.criterionSelector = criterionSelector;
    }

    public DominatedSetCalculator getDominatedSetCalculator() {
        return dominatedSetCalculator;
    }

    public void setDominatedSetCalculator(DominatedSetCalculator dominatedSetCalculator) {
        this.dominatedSetCalculator = dominatedSetCalculator;
    }

}
