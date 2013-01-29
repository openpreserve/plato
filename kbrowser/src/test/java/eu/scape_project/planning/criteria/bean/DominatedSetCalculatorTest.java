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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.scape_project.planning.model.beans.ResultNode;
import eu.scape_project.planning.model.kbrowser.VPlanLeaf;
import eu.scape_project.planning.model.measurement.Measure;

import org.junit.Test;

public class DominatedSetCalculatorTest {

    @Test
    public void getRankingChangedPowerSetTest_M1M2M3_Dominated() throws Exception {

        // Plans
        List<PlanInfo> plans = new ArrayList<PlanInfo>();
        // Plan1
        PlanInfo plan1 = mock(PlanInfo.class);
        when(plan1.getId()).thenReturn(1);
        ResultNode plan1ResultNode = mock(ResultNode.class);
        HashMap<String, Double> plan1Results = new HashMap<String, Double>();
        plan1Results.put("alternative1", 1d);
        plan1Results.put("alternative2", 3d);
        plan1Results.put("alternative3", 4.7d);
        when(plan1ResultNode.getResults()).thenReturn(plan1Results);
        when(plan1.getOverallResults()).thenReturn(plan1ResultNode);
        when(plan1.getWinningAlternative()).thenReturn("alternative3");
        plans.add(plan1);

        // Measures
        Measure m1 = mock(Measure.class);
        when(m1.getUri()).thenReturn("http://measure1");
        Measure m2 = mock(Measure.class);
        when(m2.getUri()).thenReturn("http://measure2");
        Measure m3 = mock(Measure.class);
        when(m3.getUri()).thenReturn("http://measure3");

        // Leaves
        // Leaf1
        VPlanLeaf leaf1 = mock(VPlanLeaf.class);
        when(leaf1.isMapped()).thenReturn(true);
        when(leaf1.getPlanId()).thenReturn(1);
        when(leaf1.getMeasure()).thenReturn(m1);
        when(leaf1.hasKOPotential()).thenReturn(false);
        when(leaf1.getPotentialMinimum()).thenReturn(1d);
        when(leaf1.getPotentialMaximum()).thenReturn(4d);
        when(leaf1.getTotalWeight()).thenReturn(0.1d);
        Map<String, Double> leaf1Results = new HashMap<String, Double>();
        leaf1Results.put("alternative1", 1d);
        leaf1Results.put("alternative2", 2d);
        leaf1Results.put("alternative3", 3d);
        when(leaf1.getAlternativeResultsAsMap()).thenReturn(leaf1Results);

        // Leaf2
        VPlanLeaf leaf2 = mock(VPlanLeaf.class);
        when(leaf2.isMapped()).thenReturn(true);
        when(leaf2.getPlanId()).thenReturn(1);
        when(leaf2.getMeasure()).thenReturn(m2);
        when(leaf2.hasKOPotential()).thenReturn(false);
        when(leaf2.getPotentialMinimum()).thenReturn(1d);
        when(leaf2.getPotentialMaximum()).thenReturn(4d);
        when(leaf2.getTotalWeight()).thenReturn(0.1d);
        Map<String, Double> leaf2Results = new HashMap<String, Double>();
        leaf2Results.put("alternative1", 1d);
        leaf2Results.put("alternative2", 2d);
        leaf2Results.put("alternative3", 3d);
        when(leaf2.getAlternativeResultsAsMap()).thenReturn(leaf2Results);

        // Leaf3
        VPlanLeaf leaf3 = mock(VPlanLeaf.class);
        when(leaf3.isMapped()).thenReturn(true);
        when(leaf3.getPlanId()).thenReturn(1);
        when(leaf3.getMeasure()).thenReturn(m3);
        when(leaf3.hasKOPotential()).thenReturn(false);
        when(leaf3.getPotentialMinimum()).thenReturn(1d);
        when(leaf3.getPotentialMaximum()).thenReturn(4d);
        when(leaf3.getTotalWeight()).thenReturn(0.1d);
        Map<String, Double> leaf3Results = new HashMap<String, Double>();
        leaf3Results.put("alternative1", 1d);
        leaf3Results.put("alternative2", 2d);
        leaf3Results.put("alternative3", 3d);
        when(leaf3.getAlternativeResultsAsMap()).thenReturn(leaf3Results);

        List<VPlanLeaf> leaves = new ArrayList<VPlanLeaf>();
        leaves.add(leaf1);
        leaves.add(leaf2);
        leaves.add(leaf3);

        DominatedSetCalculator c = new DominatedSetCalculator(plans, leaves);

        List<List<String>> rangeChangingSets = c.getRankingChangedPowerSet();

        // Assert
        assertEquals(rangeChangingSets.size(), 1);
        List<String> set1 = rangeChangingSets.get(0);
        assertEquals(set1.size(), 3);
        assertTrue(set1.contains(m1.getUri()));
        assertTrue(set1.contains(m2.getUri()));
        assertTrue(set1.contains(m3.getUri()));
    }

    @Test
    public void getRankingChangedPowerSetTest_M1KO_M2_DominatedLeaf() throws Exception {

        // Plans
        List<PlanInfo> plans = new ArrayList<PlanInfo>();
        // Plan1
        PlanInfo plan1 = mock(PlanInfo.class);
        when(plan1.getId()).thenReturn(1);
        ResultNode plan1ResultNode = mock(ResultNode.class);
        HashMap<String, Double> plan1Results = new HashMap<String, Double>();
        plan1Results.put("alternative1", 1d);
        plan1Results.put("alternative2", 3d);
        plan1Results.put("alternative3", 4.8d);
        when(plan1ResultNode.getResults()).thenReturn(plan1Results);
        when(plan1.getOverallResults()).thenReturn(plan1ResultNode);
        when(plan1.getWinningAlternative()).thenReturn("alternative3");
        plans.add(plan1);

        // Measures
        Measure m1 = mock(Measure.class);
        when(m1.getUri()).thenReturn("http://measure1");
        Measure m2 = mock(Measure.class);
        when(m2.getUri()).thenReturn("http://measure2");

        // Leaves
        // Leaf1
        VPlanLeaf leaf1 = mock(VPlanLeaf.class);
        when(leaf1.isMapped()).thenReturn(true);
        when(leaf1.getPlanId()).thenReturn(1);
        when(leaf1.getMeasure()).thenReturn(m1);
        when(leaf1.hasKOPotential()).thenReturn(true);
        when(leaf1.getPotentialMinimum()).thenReturn(1d);
        when(leaf1.getPotentialMaximum()).thenReturn(4d);
        when(leaf1.getTotalWeight()).thenReturn(0.1d);
        Map<String, Double> leaf1Results = new HashMap<String, Double>();
        leaf1Results.put("alternative1", 1d);
        leaf1Results.put("alternative2", 2d);
        leaf1Results.put("alternative3", 3d);
        when(leaf1.getAlternativeResultsAsMap()).thenReturn(leaf1Results);

        // Leaf2
        VPlanLeaf leaf2 = mock(VPlanLeaf.class);
        when(leaf2.isMapped()).thenReturn(true);
        when(leaf2.getPlanId()).thenReturn(1);
        when(leaf2.getMeasure()).thenReturn(m2);
        when(leaf2.hasKOPotential()).thenReturn(false);
        when(leaf2.getPotentialMinimum()).thenReturn(1d);
        when(leaf2.getPotentialMaximum()).thenReturn(4d);
        when(leaf2.getTotalWeight()).thenReturn(0.1d);
        Map<String, Double> leaf2Results = new HashMap<String, Double>();
        leaf2Results.put("alternative1", 1d);
        leaf2Results.put("alternative2", 2d);
        leaf2Results.put("alternative3", 3d);
        when(leaf2.getAlternativeResultsAsMap()).thenReturn(leaf2Results);

        List<VPlanLeaf> leaves = new ArrayList<VPlanLeaf>();
        leaves.add(leaf1);
        leaves.add(leaf2);

        DominatedSetCalculator c = new DominatedSetCalculator(plans, leaves);

        List<List<String>> rangeChangingSets = c.getRankingChangedPowerSet();

        // Assert
        assertEquals(rangeChangingSets.size(), 1);
        List<String> set1 = rangeChangingSets.get(0);
        assertEquals(set1.size(), 1);
        assertTrue(set1.contains(m2.getUri()));
    }

    @Test
    public void getRankingChangedPowerSetTest_M1M2_Dominated() throws Exception {

        // Plans
        List<PlanInfo> plans = new ArrayList<PlanInfo>();
        // Plan1
        PlanInfo plan1 = mock(PlanInfo.class);
        when(plan1.getId()).thenReturn(1);
        ResultNode plan1ResultNode = mock(ResultNode.class);
        HashMap<String, Double> plan1Results = new HashMap<String, Double>();
        plan1Results.put("alternative1", 1d);
        plan1Results.put("alternative2", 3d);
        plan1Results.put("alternative3", 3.8d);
        when(plan1ResultNode.getResults()).thenReturn(plan1Results);
        when(plan1.getOverallResults()).thenReturn(plan1ResultNode);
        when(plan1.getWinningAlternative()).thenReturn("alternative3");
        plans.add(plan1);

        // Measures
        Measure m1 = mock(Measure.class);
        when(m1.getUri()).thenReturn("http://measure1");
        Measure m2 = mock(Measure.class);
        when(m2.getUri()).thenReturn("http://measure2");
        Measure m3 = mock(Measure.class);
        when(m3.getUri()).thenReturn("http://measure3");

        // Leaves
        // Leaf1
        VPlanLeaf leaf1 = mock(VPlanLeaf.class);
        when(leaf1.isMapped()).thenReturn(true);
        when(leaf1.getPlanId()).thenReturn(1);
        when(leaf1.getMeasure()).thenReturn(m1);
        when(leaf1.hasKOPotential()).thenReturn(false);
        when(leaf1.getPotentialMinimum()).thenReturn(1d);
        when(leaf1.getPotentialMaximum()).thenReturn(4d);
        when(leaf1.getTotalWeight()).thenReturn(0.1d);
        Map<String, Double> leaf1Results = new HashMap<String, Double>();
        leaf1Results.put("alternative1", 1d);
        leaf1Results.put("alternative2", 2d);
        leaf1Results.put("alternative3", 3d);
        when(leaf1.getAlternativeResultsAsMap()).thenReturn(leaf1Results);

        // Leaf2
        VPlanLeaf leaf2 = mock(VPlanLeaf.class);
        when(leaf2.isMapped()).thenReturn(true);
        when(leaf2.getPlanId()).thenReturn(1);
        when(leaf2.getMeasure()).thenReturn(m2);
        when(leaf2.hasKOPotential()).thenReturn(false);
        when(leaf2.getPotentialMinimum()).thenReturn(1d);
        when(leaf2.getPotentialMaximum()).thenReturn(4d);
        when(leaf2.getTotalWeight()).thenReturn(0.1d);
        Map<String, Double> leaf2Results = new HashMap<String, Double>();
        leaf2Results.put("alternative1", 1d);
        leaf2Results.put("alternative2", 2d);
        leaf2Results.put("alternative3", 3d);
        when(leaf2.getAlternativeResultsAsMap()).thenReturn(leaf2Results);

        // Leaf3
        VPlanLeaf leaf3 = mock(VPlanLeaf.class);
        when(leaf3.isMapped()).thenReturn(true);
        when(leaf3.getPlanId()).thenReturn(1);
        when(leaf3.getMeasure()).thenReturn(m3);
        when(leaf3.hasKOPotential()).thenReturn(false);
        when(leaf3.getPotentialMinimum()).thenReturn(1d);
        when(leaf3.getPotentialMaximum()).thenReturn(4d);
        when(leaf3.getTotalWeight()).thenReturn(0.5d);
        Map<String, Double> leaf3Results = new HashMap<String, Double>();
        leaf3Results.put("alternative1", 1d);
        leaf3Results.put("alternative2", 2d);
        leaf3Results.put("alternative3", 3d);
        when(leaf3.getAlternativeResultsAsMap()).thenReturn(leaf3Results);

        List<VPlanLeaf> leaves = new ArrayList<VPlanLeaf>();
        leaves.add(leaf1);
        leaves.add(leaf2);
        leaves.add(leaf3);

        DominatedSetCalculator c = new DominatedSetCalculator(plans, leaves);

        List<List<String>> rangeChangingSets = c.getRankingChangedPowerSet();

        // Assert
        assertEquals(rangeChangingSets.size(), 1);
        List<String> set1 = rangeChangingSets.get(0);
        assertEquals(set1.size(), 2);
        assertTrue(set1.contains(m1.getUri()));
        assertTrue(set1.contains(m2.getUri()));
    }

    @Test
    public void getRankingChangedPowerSetTest_M1_M2_M3_MeasuresDominated() throws Exception {

        // Plans
        List<PlanInfo> plans = new ArrayList<PlanInfo>();
        // Plan1
        PlanInfo plan1 = mock(PlanInfo.class);
        when(plan1.getId()).thenReturn(1);
        ResultNode plan1ResultNode = mock(ResultNode.class);
        HashMap<String, Double> plan1Results = new HashMap<String, Double>();
        plan1Results.put("alternative1", 1d);
        plan1Results.put("alternative2", 2.1d);
        plan1Results.put("alternative3", 3.2d);
        when(plan1ResultNode.getResults()).thenReturn(plan1Results);
        when(plan1.getOverallResults()).thenReturn(plan1ResultNode);
        when(plan1.getWinningAlternative()).thenReturn("alternative3");
        plans.add(plan1);

        // Measures
        Measure m1 = mock(Measure.class);
        when(m1.getUri()).thenReturn("http://measure1");
        Measure m2 = mock(Measure.class);
        when(m2.getUri()).thenReturn("http://measure2");
        Measure m3 = mock(Measure.class);
        when(m3.getUri()).thenReturn("http://measure3");

        // Leaves
        // Leaf1
        VPlanLeaf leaf1 = mock(VPlanLeaf.class);
        when(leaf1.isMapped()).thenReturn(true);
        when(leaf1.getPlanId()).thenReturn(1);
        when(leaf1.getMeasure()).thenReturn(m1);
        when(leaf1.hasKOPotential()).thenReturn(false);
        when(leaf1.getPotentialMinimum()).thenReturn(1d);
        when(leaf1.getPotentialMaximum()).thenReturn(4d);
        when(leaf1.getTotalWeight()).thenReturn(0.2d);
        Map<String, Double> leaf1Results = new HashMap<String, Double>();
        leaf1Results.put("alternative1", 1d);
        leaf1Results.put("alternative2", 2d);
        leaf1Results.put("alternative3", 3d);
        when(leaf1.getAlternativeResultsAsMap()).thenReturn(leaf1Results);

        // Leaf2
        VPlanLeaf leaf2 = mock(VPlanLeaf.class);
        when(leaf2.isMapped()).thenReturn(true);
        when(leaf2.getPlanId()).thenReturn(1);
        when(leaf2.getMeasure()).thenReturn(m2);
        when(leaf2.hasKOPotential()).thenReturn(false);
        when(leaf2.getPotentialMinimum()).thenReturn(1d);
        when(leaf2.getPotentialMaximum()).thenReturn(4d);
        when(leaf2.getTotalWeight()).thenReturn(0.2d);
        Map<String, Double> leaf2Results = new HashMap<String, Double>();
        leaf2Results.put("alternative1", 1d);
        leaf2Results.put("alternative2", 2d);
        leaf2Results.put("alternative3", 3d);
        when(leaf2.getAlternativeResultsAsMap()).thenReturn(leaf2Results);

        // Leaf3
        VPlanLeaf leaf3 = mock(VPlanLeaf.class);
        when(leaf3.isMapped()).thenReturn(true);
        when(leaf3.getPlanId()).thenReturn(1);
        when(leaf3.getMeasure()).thenReturn(m3);
        when(leaf3.hasKOPotential()).thenReturn(false);
        when(leaf3.getPotentialMinimum()).thenReturn(1d);
        when(leaf3.getPotentialMaximum()).thenReturn(4d);
        when(leaf3.getTotalWeight()).thenReturn(0.2d);
        Map<String, Double> leaf3Results = new HashMap<String, Double>();
        leaf3Results.put("alternative1", 1d);
        leaf3Results.put("alternative2", 2d);
        leaf3Results.put("alternative3", 3d);
        when(leaf3.getAlternativeResultsAsMap()).thenReturn(leaf3Results);

        List<VPlanLeaf> leaves = new ArrayList<VPlanLeaf>();
        leaves.add(leaf1);
        leaves.add(leaf2);
        leaves.add(leaf3);

        DominatedSetCalculator c = new DominatedSetCalculator(plans, leaves);

        List<List<String>> rangeChangingSets = c.getRankingChangedPowerSet();

        // Assert
        assertEquals(rangeChangingSets.size(), 1);
        List<String> set1 = rangeChangingSets.get(0);
        assertEquals(set1.size(), 2);
        assertTrue(set1.contains(m1.getUri()));
        assertTrue(set1.contains(m2.getUri()));
    }

    @Test
    public void getRankingChangedPowerSetTest_Duplicates() throws Exception {

        // Plans
        List<PlanInfo> plans = new ArrayList<PlanInfo>();
        // Plan1
        PlanInfo plan1 = mock(PlanInfo.class);
        when(plan1.getId()).thenReturn(1);
        ResultNode plan1ResultNode = mock(ResultNode.class);
        HashMap<String, Double> plan1Results = new HashMap<String, Double>();
        plan1Results.put("alternative1", 1d);
        plan1Results.put("alternative2", 3d);
        plan1Results.put("alternative3", 4.7d);
        when(plan1ResultNode.getResults()).thenReturn(plan1Results);
        when(plan1.getOverallResults()).thenReturn(plan1ResultNode);
        when(plan1.getWinningAlternative()).thenReturn("alternative3");
        plans.add(plan1);

        // Measures
        Measure m1 = mock(Measure.class);
        when(m1.getUri()).thenReturn("http://measure1");

        // Leaves
        // Leaf1
        VPlanLeaf leaf1 = mock(VPlanLeaf.class);
        when(leaf1.isMapped()).thenReturn(true);
        when(leaf1.getPlanId()).thenReturn(1);
        when(leaf1.getMeasure()).thenReturn(m1);
        when(leaf1.hasKOPotential()).thenReturn(false);
        when(leaf1.getPotentialMinimum()).thenReturn(1d);
        when(leaf1.getPotentialMaximum()).thenReturn(4d);
        when(leaf1.getTotalWeight()).thenReturn(0.1d);
        Map<String, Double> leaf1Results = new HashMap<String, Double>();
        leaf1Results.put("alternative1", 1d);
        leaf1Results.put("alternative2", 2d);
        leaf1Results.put("alternative3", 3d);
        when(leaf1.getAlternativeResultsAsMap()).thenReturn(leaf1Results);

        List<VPlanLeaf> leaves = new ArrayList<VPlanLeaf>();
        leaves.add(leaf1);
        leaves.add(leaf1);
        leaves.add(leaf1);

        DominatedSetCalculator c = new DominatedSetCalculator(plans, leaves);

        List<List<String>> rangeChangingSets = c.getRankingChangedPowerSet();

        // Assert
        assertEquals(rangeChangingSets.size(), 1);
        List<String> set1 = rangeChangingSets.get(0);
        assertEquals(set1.size(), 1);
        assertTrue(set1.contains(m1.getUri()));
    }

}
