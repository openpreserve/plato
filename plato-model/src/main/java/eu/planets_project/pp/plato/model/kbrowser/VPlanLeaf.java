package eu.planets_project.pp.plato.model.kbrowser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.planets_project.pp.plato.model.SampleAggregationMode;
import eu.planets_project.pp.plato.model.TargetValueObject;
import eu.planets_project.pp.plato.model.Values;
import eu.planets_project.pp.plato.model.measurement.Criterion;
import eu.planets_project.pp.plato.model.scales.FloatRangeScale;
import eu.planets_project.pp.plato.model.scales.FloatScale;
import eu.planets_project.pp.plato.model.scales.IntRangeScale;
import eu.planets_project.pp.plato.model.scales.IntegerScale;
import eu.planets_project.pp.plato.model.scales.PositiveFloatScale;
import eu.planets_project.pp.plato.model.scales.PositiveIntegerScale;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.transform.NumericTransformer;
import eu.planets_project.pp.plato.model.transform.OrdinalTransformer;
import eu.planets_project.pp.plato.model.transform.Transformer;
import eu.planets_project.pp.plato.model.values.INumericValue;
import eu.planets_project.pp.plato.model.values.IOrdinalValue;
import eu.planets_project.pp.plato.model.values.TargetValue;
import eu.planets_project.pp.plato.model.values.Value;

@Entity
public class VPlanLeaf {    
    private static final Logger log = LoggerFactory.getLogger(VPlanLeaf.class);
    
    @Id
    private int id;
    
    private int planId;
    
    @Column(name="absoluteWeight")
    private double weight;
    
    @Column(name="relativeWeight")
    private double totalWeight;
    
    @OneToOne(fetch=FetchType.EAGER)
    private Scale scale;
    
    @OneToOne(fetch=FetchType.EAGER)
    private Transformer transformer;
    
    @ManyToOne(fetch=FetchType.EAGER)
    private Criterion criterion;
    
    @Enumerated
    private SampleAggregationMode aggregationMode;

   @OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
//    @IndexColumn(name="key_name")
    private Map<String, Values> valueMap = new ConcurrentHashMap<String, Values>();          
       
    /**
     * Method responsible for assessing the potential output range of this requirement leaf.
     * Calculation rule:
     * if (minPossibleTransformedValue == 0) koFactor = 1; else koFactor = 0;
     * potentialOutputRange = relativeWeight * (maxPossibleTransformedValue - minPossibleTransformedValue) + koFactor;
     * 
     * @return potential output range. 
     *         If the corresponding plan is not yet at a evaluation stage where actual output range can be calculated -1 is returned ("ignore value").
     */
    public double getPotentialOutputRange() {
        // If the plan is not yet at a evaluation stage where potential output range can be calculated - return 0.
        if (transformer == null) {
            return 0;
        }
        
        double outputLowerBound = 10;
        double outputUpperBound = -10;
        
        // Check OrdinalTransformer
        if (transformer instanceof OrdinalTransformer) {
            OrdinalTransformer ot = (OrdinalTransformer) transformer;
            Map<String, TargetValueObject> otMapping = ot.getMapping();

            // set upper- and lower-bound  
            for(TargetValueObject tv : otMapping.values()) {
                if (tv.getValue() > outputUpperBound) {
                    outputUpperBound = tv.getValue();
                }
                if (tv.getValue() < outputLowerBound) {
                    outputLowerBound = tv.getValue();
                }
            }
        }
        
        // Check NumericTransformer
        if (transformer instanceof NumericTransformer) {
            // I have to identify the scale bounds before I can calculate the output bounds.   
            double scaleLowerBound = - Double.MAX_VALUE;
            double scaleUpperBound = Double.MAX_VALUE;

            // At Positive Scales lowerBound is 0, upperBound has to be fetched
            if (scale instanceof PositiveIntegerScale) {
                PositiveIntegerScale s = (PositiveIntegerScale) scale;
                scaleLowerBound = 0;
                scaleUpperBound = s.getUpperBound();
            }
            if (scale instanceof PositiveFloatScale) {
                PositiveFloatScale s = (PositiveFloatScale) scale;
                scaleLowerBound = 0;
                scaleUpperBound = s.getUpperBound();                
            }
            
            // At Range Scales lowerBound and upperBound have to be fetched
            if (scale instanceof IntRangeScale) {
                IntRangeScale s = (IntRangeScale) scale;
                scaleLowerBound = s.getLowerBound();
                scaleUpperBound = s.getUpperBound();
            }
            if (scale instanceof FloatRangeScale) {
                FloatRangeScale s = (FloatRangeScale) scale;
                scaleLowerBound = s.getLowerBound();
                scaleUpperBound = s.getUpperBound();
            }

            // get Transformer thresholds
            NumericTransformer nt = (NumericTransformer) transformer;
            double transformerT1 = nt.getThreshold1();
            double transformerT2 = nt.getThreshold2();
            double transformerT3 = nt.getThreshold3();
            double transformerT4 = nt.getThreshold4();
            double transformerT5 = nt.getThreshold5();
            
            // calculate output bounds
            // increasing thresholds
            if (transformerT1 <= transformerT5) {
                // lower bound
                if (scaleLowerBound < transformerT1) {
                    outputLowerBound = 0;
                }
                else if (scaleLowerBound < transformerT2) {
                    outputLowerBound = 1;
                }
                else if (scaleLowerBound < transformerT3) {
                    outputLowerBound = 2;
                }
                else if (scaleLowerBound < transformerT4) {
                    outputLowerBound = 3;
                }
                else if (scaleLowerBound < transformerT5) {
                    outputLowerBound = 4;
                }
                else {
                    outputLowerBound = 5;
                }
                
                // upper bound
                if (scaleUpperBound < transformerT1) {
                    outputUpperBound = 0;
                }
                else if (scaleUpperBound < transformerT2) {
                    outputUpperBound = 1;
                }
                else if (scaleUpperBound < transformerT3) {
                    outputUpperBound = 2;
                }
                else if (scaleUpperBound < transformerT4) {
                    outputUpperBound = 3;
                }
                else if (scaleUpperBound < transformerT5) {
                    outputUpperBound = 4;
                }
                else {
                    outputUpperBound = 5;
                }
            }
            
            // decreasing thresholds
            if (transformerT1 > transformerT5) {
                // lower bound
                if (scaleUpperBound > transformerT1) {
                    outputLowerBound = 0;
                }
                else if (scaleUpperBound > transformerT2) {
                    outputLowerBound = 1;
                }
                else if (scaleUpperBound > transformerT3) {
                    outputLowerBound = 2;
                }
                else if (scaleUpperBound > transformerT4) {
                    outputLowerBound = 3;
                }
                else if (scaleUpperBound > transformerT5) {
                    outputLowerBound = 4;
                }
                else {
                    outputLowerBound = 5;
                }
                
                // upper bound
                if (scaleLowerBound > transformerT1) {
                    outputUpperBound = 0;
                }
                else if (scaleLowerBound > transformerT2) {
                    outputUpperBound = 1;
                }
                else if (scaleLowerBound > transformerT3) {
                    outputUpperBound = 2;
                }
                else if (scaleLowerBound > transformerT4) {
                    outputUpperBound = 3;
                }
                else if (scaleLowerBound > transformerT5) {
                    outputUpperBound = 4;
                }
                else {
                    outputUpperBound = 5;
                }
            }
        }
                
        double potentialOutputRange = totalWeight * (outputUpperBound - outputLowerBound);
                
        return potentialOutputRange;
    }
    
    /**
     * Method responsible for assessing the actual output range of this requirement leaf.
     * Calculation rule:
     * if (minActualTransformedValue == 0) koFactor = 1; else koFactor = 0;
     * actualOutputRange = relativeWeight * (maxActualTransformedValue - minActualTransformedValue) + koFactor;
     * 
     * @return actual output range.
     *         If the corresponding plan is not yet at a evaluation stage where actual output range can be calculated -1 is returned ("ignore value").
     */
    public double getActualOutputRange() {
        // If the plan is not yet at a evaluation stage where actual output range can be calculated - return 0.
        if (transformer == null) {
            return 0;
        }

        List<Double> alternativeAggregatedValues = getAlternativeResults();
        
        // calculate upper/lower bound
        double outputLowerBound = 10;
        double outputUpperBound = -10;

        for (Double aVal : alternativeAggregatedValues) {
            if (aVal > outputUpperBound) {
                outputUpperBound = aVal;
            }
            if (aVal < outputLowerBound) {
                outputLowerBound = aVal;
            }
        }
        
        double actualOutputRange = totalWeight * (outputUpperBound - outputLowerBound);
        
        return actualOutputRange;
    }

    /**
     * Method responsible for calculating and returning the numeric result of each leaf alternative.
     * @return Numeric result of each leaf alternative
     */
    public List<Double> getAlternativeResults() {
        List<Double> alternativeAggregatedValues = new ArrayList<Double>();
        Boolean skipAlternativeBecauseOfErrors = false;
        
        // iterate each alternative and calculate for each alternative its aggregated value
        for (String alternative : valueMap.keySet()) {
            List<Value> alternativeValues = valueMap.get(alternative).getList();
            List<Double> alternativeTransformedValues = new ArrayList<Double>();
            
            // collect alternativeTransformedValues
            for (Value alternativeValue : alternativeValues) {
                TargetValue targetValue;
                
                // do ordinal transformation
                if (transformer instanceof OrdinalTransformer) {
                    OrdinalTransformer ordTrans = (OrdinalTransformer) transformer;
                    
                    if (alternativeValue instanceof IOrdinalValue) {
                        try {
                            targetValue = ordTrans.transform((IOrdinalValue) alternativeValue);
                        }
                        catch (NullPointerException e) {
                            log.warn("Measurement of leaf doesn't match with OrdinalTransformer! Ignoring it!");
                            log.warn("MeasuredValue-id: " + alternativeValue.getId() + "; Transformer-id: " + ordTrans.getId());
                            // FIXME: this is a workaround for a strange bug described in changeset 4342
                            skipAlternativeBecauseOfErrors = true;
                            continue;
                        }
                        alternativeTransformedValues.add(targetValue.getValue());
                    }
                    else {
                        log.warn("getActualOutputRange(): INumericValue value passed to OrdinalTransformer - ignore value");
                    }
                }

                // do numeric transformation
                if (transformer instanceof NumericTransformer) {
                    NumericTransformer numericTrans = (NumericTransformer) transformer;
                    
                    if (alternativeValue instanceof INumericValue) {
                        targetValue = numericTrans.transform((INumericValue) alternativeValue);
                        alternativeTransformedValues.add(targetValue.getValue());
                    }
                    else {
                        log.warn("getActualOutputRange(): IOrdinalValue value passed to NumericTransformer - ignore value");
                    }
                }
            }
            
            // aggregate the transformed values            
            double count = 0;
            double sum = 0;
            double minValue = 5;
            Double alternativeAggregatedValue;
            
            for (Double alternativeTransformedValue : alternativeTransformedValues) {
                count++;
                sum = sum + alternativeTransformedValue;
                if (alternativeTransformedValue < minValue) {
                    minValue = alternativeTransformedValue;
                }
            }
                
            if (aggregationMode == SampleAggregationMode.AVERAGE) {
                alternativeAggregatedValue = sum / count; 
            }
            else {
                alternativeAggregatedValue = minValue;
            }
            
            if (!skipAlternativeBecauseOfErrors) {
                alternativeAggregatedValues.add(alternativeAggregatedValue);
            }
            skipAlternativeBecauseOfErrors = false;
        }
        return alternativeAggregatedValues;
    }
    
    /**
     * Method responsible for calculating and returning the numeric result of each leaf alternative.
     * @return Numeric result of each leaf alternative
     */
    public Map<String,Double> getAlternativeResultsAsMap() {
        Map<String,Double> alternativeAggregatedValues = new HashMap<String,Double>();
        
        // iterate each alternative and calculate for each alternative its aggregated value
        for (String alternative : valueMap.keySet()) {
            List<Value> alternativeValues = valueMap.get(alternative).getList();
            List<Double> alternativeTransformedValues = new ArrayList<Double>();
            Boolean skipAlternativeBecauseOfErrors = false;
            
            // collect alternativeTransformedValues
            for (Value alternativeValue : alternativeValues) {
                TargetValue targetValue;
                
                // do ordinal transformation
                if (transformer instanceof OrdinalTransformer) {
                    OrdinalTransformer ordTrans = (OrdinalTransformer) transformer;
                    
                    if (alternativeValue instanceof IOrdinalValue) {
                        try {
                            targetValue = ordTrans.transform((IOrdinalValue) alternativeValue);
                        }
                        catch (NullPointerException e) {
                            log.warn("Measurement of leaf doesn't match with OrdinalTransformer! Ignoring it!");
                            log.warn("MeasuredValue-id: " + alternativeValue.getId() + "; Transformer-id: " + ordTrans.getId());
                            // FIXME: this is a workaround for a strange bug described in changeset 4342
                            skipAlternativeBecauseOfErrors = true;
                            continue;
                        }
                        alternativeTransformedValues.add(targetValue.getValue());
                    }
                    else {
                        log.warn("getActualOutputRange(): INumericValue value passed to OrdinalTransformer - ignore value");
                    }
                }

                // do numeric transformation
                if (transformer instanceof NumericTransformer) {
                    NumericTransformer numericTrans = (NumericTransformer) transformer;
                    
                    if (alternativeValue instanceof INumericValue) {
                        targetValue = numericTrans.transform((INumericValue) alternativeValue);
                        alternativeTransformedValues.add(targetValue.getValue());
                    }
                    else {
                        log.warn("getActualOutputRange(): IOrdinalValue value passed to NumericTransformer - ignore value");
                    }
                }
            }
            
            // aggregate the transformed values            
            double count = 0;
            double sum = 0;
            double minValue = 5;
            Double alternativeAggregatedValue;
            
            for (Double alternativeTransformedValue : alternativeTransformedValues) {
                count++;
                sum = sum + alternativeTransformedValue;
                if (alternativeTransformedValue < minValue) {
                    minValue = alternativeTransformedValue;
                }
            }
                
            if (aggregationMode == SampleAggregationMode.AVERAGE) {
                alternativeAggregatedValue = sum / count; 
            }
            else {
                alternativeAggregatedValue = minValue;
            }
            
            if (!skipAlternativeBecauseOfErrors) {
                alternativeAggregatedValues.put(alternative, alternativeAggregatedValue);
            }
            skipAlternativeBecauseOfErrors = false;
        }
        return alternativeAggregatedValues;
    }
    
    /**
     * Method responsible for assessing the relative output range of this requirement leaf.
     * Calculation rule:
     * relativeOutputRange = actualOutputRange / potentialOutputRange
     * 
     * @return relative output range.
     *         If the corresponding plan is not yet at a evaluation stage where relative output range can be calculated -1 is returned ("ignore value").
     */
    public double getRelativeOutputRange() {
        double actualOutputRange = getActualOutputRange();
        double potentialOutputRange = getPotentialOutputRange();
        
        if ((actualOutputRange == -1) || (potentialOutputRange == -1)) {
            return -1;
        }
        
        if (potentialOutputRange == 0) {
            return 0;
        }
        
        return actualOutputRange / potentialOutputRange; 
    }

    
    /**
     * Method responsible for assessing if the leaf has KnockOut potential.
     * @return true if the leaf has KO potential, otherwise false.
     */
    public Boolean hasKOPotential() {
        // check OrdinalTransformer KnockOut potential
        if (transformer instanceof OrdinalTransformer) {
            OrdinalTransformer ot = (OrdinalTransformer) transformer;
            Map<String, TargetValueObject> otMapping = ot.getMapping();

            // if any string maps to value 0 -> KnockOut potential 
            for(TargetValueObject tv : otMapping.values()) {
                if (tv.getValue() == 0) {
                    return true;
                }
            }
        }
        // NumericTransformer has KnockOut potential if the relatedScale allows a 0 transformed value.
        else if (transformer instanceof NumericTransformer) {
            // IntegerScale and FloatScale have no restrictions -> always have KO Potential
            if ((scale instanceof IntegerScale) || (scale instanceof FloatScale)) {
                return true;
            }

            double scaleLowerBound = Double.MIN_VALUE;
            double scaleUpperBound = Double.MAX_VALUE;
            
            // At Positive Scales lowerBound is 0, upperBound has to be fetched
            if (scale instanceof PositiveIntegerScale) {
                PositiveIntegerScale s = (PositiveIntegerScale) scale;
                scaleLowerBound = 0;
                scaleUpperBound = s.getUpperBound();
            }
            if (scale instanceof PositiveFloatScale) {
                PositiveFloatScale s = (PositiveFloatScale) scale;
                scaleLowerBound = 0;
                scaleUpperBound = s.getUpperBound();                
            }
            
            // At Range Scales lowerBound and upperBound have to be fetched
            if (scale instanceof IntRangeScale) {
                IntRangeScale s = (IntRangeScale) scale;
                scaleLowerBound = s.getLowerBound();
                scaleUpperBound = s.getUpperBound();
            }
            if (scale instanceof FloatRangeScale) {
                FloatRangeScale s = (FloatRangeScale) scale;
                scaleLowerBound = s.getLowerBound();
                scaleUpperBound = s.getUpperBound();
            }
 
            // get Transformer tresholds
            NumericTransformer nt = (NumericTransformer) transformer;
            double transformerT1 = nt.getThreshold1();
            double transformerT5 = nt.getThreshold5();
            
            // check for KO-Potential
            if ((transformerT1 < transformerT5) && (scaleLowerBound < transformerT1)) {
                return true;
            }
            if ((transformerT1 > transformerT5) && (scaleUpperBound > transformerT1)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Method responsible for assessing the number of KnockOut values (=0) this leaf produces.
     * @return number of KO values produced by this leaf.
     */
    public int getActualKO() {
        if (transformer == null) {
            return 0;
        }
        
        List<Double> alternativeResult = getAlternativeResults();
        
        int koCount = 0;
        
        for (Double result : alternativeResult) {
            if (result == 0) {
                koCount++;
            }
        }
        
        return koCount;
    }
    
    /**
     * Method responsible for returning the measured values of this leaf.
     * @return Measured values of this leaf.
     */
    public List<Value> getMeasuredValues() {
        List<Value> result = new ArrayList<Value>();
        for (String alternative : valueMap.keySet()) {
            List<Value> alternativeValues = valueMap.get(alternative).getList();
            result.addAll(alternativeValues);
        }
        
        return result;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }

    public Scale getScale() {
        return scale;
    }

    public void setTransformer(Transformer transformer) {
        this.transformer = transformer;
    }

    public Transformer getTransformer() {
        return transformer;
    }

    public void setCriterion(Criterion criterion) {
        this.criterion = criterion;
    }

    public Criterion getCriterion() {
        return criterion;
    }
    
    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public int getPlanId() {
        return planId;
    }

    public void setValueMap(Map<String, Values> valueMap) {
        this.valueMap = valueMap;
    }

    public Map<String, Values> getValueMap() {
        return valueMap;
    }

    public void setAggregationMode(SampleAggregationMode aggregationMode) {
        this.aggregationMode = aggregationMode;
    }

    public SampleAggregationMode getAggregationMode() {
        return aggregationMode;
    }
}
