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
package eu.scape_project.planning.model;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import eu.scape_project.planning.model.SampleAggregationMode;
import eu.scape_project.planning.model.TargetValueObject;
import eu.scape_project.planning.model.Values;
import eu.scape_project.planning.model.kbrowser.VPlanLeaf;
import eu.scape_project.planning.model.scales.OrdinalScale;
import eu.scape_project.planning.model.scales.PositiveFloatScale;
import eu.scape_project.planning.model.transform.NumericTransformer;
import eu.scape_project.planning.model.transform.OrdinalTransformer;
import eu.scape_project.planning.model.values.FloatValue;
import eu.scape_project.planning.model.values.OrdinalValue;

public class VPlanLeafTest {
    @Test
    public void hasKOPotential_ordinalTrans_independentOfAggregation() {
        VPlanLeaf avgAggLeaf = createLeafWithOrdinalTransformer(SampleAggregationMode.AVERAGE);
        VPlanLeaf worstAggLeaf = createLeafWithOrdinalTransformer(SampleAggregationMode.WORST);
        
        assertEquals(false, avgAggLeaf.hasKOPotential());
        assertEquals(false, worstAggLeaf.hasKOPotential());
    }

    @Test
    public void hasKOPotential_numericTransformer_independentOfAggregation() {
        VPlanLeaf avgAggLeaf = createLeafWithNumericTransformer(SampleAggregationMode.AVERAGE);
        VPlanLeaf worstAggLeaf = createLeafWithNumericTransformer(SampleAggregationMode.WORST);
        
        assertEquals(true, avgAggLeaf.hasKOPotential());
        assertEquals(true, worstAggLeaf.hasKOPotential());
    }

    @Test
    public void getActualKO_ordinalTransformerNotMappingTo0_average() {
        VPlanLeaf leaf = createLeafWithOrdinalTransformer(SampleAggregationMode.AVERAGE);
        
        assertEquals(0, leaf.getActualKO(), 0.001);
    }
    
    @Test
    public void getActualKO_ordinalTransformerNotMappingTo0_worst() {
        VPlanLeaf leaf = createLeafWithOrdinalTransformer(SampleAggregationMode.WORST);
        
        assertEquals(0, leaf.getActualKO(), 0.001);
    }

    @Test
    public void getActualKO_numericTransformer_average() {
        VPlanLeaf leaf = createLeafWithNumericTransformer(SampleAggregationMode.AVERAGE);
        
        assertEquals(0, leaf.getActualKO(), 0.001);
    }   

    @Test
    public void getActualKO_numericTransformer_worst() {
        VPlanLeaf leaf = createLeafWithNumericTransformer(SampleAggregationMode.WORST);
        
        assertEquals(1, leaf.getActualKO(), 0.001);
    }   

    @Test
    public void getPotentialOutputRange_ordinalTransformer_independentOfAggregation() {
        VPlanLeaf avgAggLeaf = createLeafWithOrdinalTransformer(SampleAggregationMode.AVERAGE);
        VPlanLeaf worstAggLeaf = createLeafWithOrdinalTransformer(SampleAggregationMode.WORST);
        
        assertEquals(4, avgAggLeaf.getPotentialOutputRange(), 0.001);
        assertEquals(4, worstAggLeaf.getPotentialOutputRange(), 0.001);
    }    
    
    @Test
    public void getPotentialOutputRange_numericTransformer_independentOfAggregation() {
        VPlanLeaf avgAggLeaf = createLeafWithNumericTransformer(SampleAggregationMode.AVERAGE);
        VPlanLeaf worstAggLeaf = createLeafWithNumericTransformer(SampleAggregationMode.WORST);
        
        assertEquals(5, avgAggLeaf.getPotentialOutputRange(), 0.001);
        assertEquals(5, worstAggLeaf.getPotentialOutputRange(), 0.001);
    }        
    
    @Test
    public void getActualOutputRange_ordinalTransformer_average() {
        VPlanLeaf leaf = createLeafWithOrdinalTransformer(SampleAggregationMode.AVERAGE);
        
        assertEquals(3, leaf.getActualOutputRange(), 0.001);
    }
    
    @Test
    public void getActualOutputRange_ordinalTransformer_worst() {
        VPlanLeaf leaf = createLeafWithOrdinalTransformer(SampleAggregationMode.WORST);
        
        assertEquals(0, leaf.getActualOutputRange(), 0.001);
    }
    
    @Test
    public void getActualOutputRange_numericTransformer_average() {
        VPlanLeaf leaf = createLeafWithNumericTransformer(SampleAggregationMode.AVERAGE);
        
        assertEquals(0.6, leaf.getActualOutputRange(), 0.001);
    }

    @Test
    public void getActualOutputRange_numericTransformer_worst() {
        VPlanLeaf leaf = createLeafWithNumericTransformer(SampleAggregationMode.WORST);
        
        assertEquals(1, leaf.getActualOutputRange(), 0.001);
    }   
    

    // -------------------------- helper methods --------------------------
    
    private VPlanLeaf createLeafWithOrdinalTransformer(SampleAggregationMode aggregationMode) {
        VPlanLeaf leaf = new VPlanLeaf();
        
        // create simple transformer
        OrdinalTransformer ordinalTrans = new OrdinalTransformer();
        Map<String, TargetValueObject> mapping = new HashMap<String, TargetValueObject>();
        TargetValueObject tv0 = new TargetValueObject();
        TargetValueObject tv1 = new TargetValueObject();
        tv0.setValue(5);
        tv1.setValue(1);
        mapping.put("Y", tv0);
        mapping.put("N", tv1);
        ordinalTrans.setMapping(mapping);
        leaf.setTransformer(ordinalTrans);
        
        // set scale
        OrdinalScale ordinalScale = new OrdinalScale();
        ordinalScale.setRestriction("Yes/No");
        leaf.setScale(ordinalScale);

        // valueMap
        OrdinalValue yes = new OrdinalValue();
        yes.setValue("Y");
        OrdinalValue no = new OrdinalValue();
        no.setValue("N");
        
        Values valuesAlternative1 = new Values();
        Values valuesAlternative2 = new Values();
        valuesAlternative1.add(yes);
        valuesAlternative1.add(yes);
        valuesAlternative1.add(yes);
        valuesAlternative1.add(no);
        valuesAlternative2.add(no);
        valuesAlternative2.add(no);
        valuesAlternative2.add(no);
        valuesAlternative2.add(no);
        
        Map<String, Values> valueMap = new HashMap<String, Values>();
        valueMap.put("alt1", valuesAlternative1);
        valueMap.put("alt2", valuesAlternative2);
        leaf.setValueMap(valueMap);
        
        leaf.setAggregationMode(aggregationMode);
        leaf.setWeight(1);
        leaf.setTotalWeight(1);
        
        return leaf;
    }
    
    private VPlanLeaf createLeafWithNumericTransformer(SampleAggregationMode aggregationMode) {
        VPlanLeaf leaf = new VPlanLeaf();
        
        // create simple transformer
        NumericTransformer numericTrans = new NumericTransformer();
        numericTrans.setThreshold1(1d);
        numericTrans.setThreshold2(2d);
        numericTrans.setThreshold3(3d);
        numericTrans.setThreshold4(4d);
        numericTrans.setThreshold5(5d);
        leaf.setTransformer(numericTrans);

        // set scale
        PositiveFloatScale scale = new PositiveFloatScale();
        leaf.setScale(scale);
       
        // valueMap
        FloatValue v0 = new FloatValue();
        v0.setValue(0d);
        FloatValue v1 = new FloatValue();
        v1.setValue(1d);
        FloatValue v2 = new FloatValue();
        v2.setValue(2d);
        FloatValue v3 = new FloatValue();
        v3.setValue(3d);
        FloatValue v4 = new FloatValue();
        v4.setValue(4d);
        FloatValue v5 = new FloatValue();
        v5.setValue(5d);
        
        Values valuesAlternative1 = new Values();
        Values valuesAlternative2 = new Values();
        valuesAlternative1.add(v0);
        valuesAlternative1.add(v0);
        valuesAlternative1.add(v3);
        valuesAlternative1.add(v4);
        valuesAlternative1.add(v5);
        valuesAlternative2.add(v3);
        valuesAlternative2.add(v3);
        valuesAlternative2.add(v1);
        valuesAlternative2.add(v3);
        valuesAlternative2.add(v5);
        
        Map<String, Values> valueMap = new HashMap<String, Values>();
        valueMap.put("alt1", valuesAlternative1);
        valueMap.put("alt2", valuesAlternative2);
        leaf.setValueMap(valueMap);
        
        leaf.setAggregationMode(aggregationMode);
        leaf.setWeight(1);
        leaf.setTotalWeight(1);
        
        return leaf;
    }
}
