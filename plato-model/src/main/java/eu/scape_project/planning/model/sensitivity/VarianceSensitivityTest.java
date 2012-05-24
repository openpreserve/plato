/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.scape_project.planning.model.sensitivity;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.aggregators.IAggregator;
import eu.scape_project.planning.model.beans.ResultNode;
import eu.scape_project.planning.model.tree.TreeNode;

/**
 * This sensitivity test implementation uses statistics to analyse the model. It
 * should be used with a IWeightModifier that uses several (>10) random
 * modifications for each node. After each modification to the node weights the
 * value for each alternative of the whole tree is evaluated.
 * 
 * @author Jan Zarnikov
 * 
 */
public class VarianceSensitivityTest implements ISensitivityTest {
             
   private TreeNode root;
   
   private IAggregator aggregator;
   
   private List<Alternative> alternatives;
   
   private Map<String, DescriptiveStatistics> statisticsMap = new HashMap<String, DescriptiveStatistics>();
   

    public VarianceSensitivityTest(TreeNode root, IAggregator aggregator, List<Alternative> alternatives) {
        super();
        this.root = root;
        this.aggregator = aggregator;
        this.alternatives = alternatives;
    }

    public void afterIteration(ResultNode node) {
        for(Alternative a : alternatives) {
            double value = aggregator.getAggregatedValue(root, a);
            statisticsMap.get(a.getName()).addValue(value);
        }
    }

    public void afterNode(ResultNode node) {
        VarianceResult result = new VarianceResult();
        for(Alternative a : alternatives) {
            String alternativeName = a.getName();
            double variance = statisticsMap.get(alternativeName).getVariance();
            double average = statisticsMap.get(alternativeName).getMean();
            result.addAlternativeResult(a, variance, average);
        }
        node.setSensitivityAnalysisResult(result);

    }

    public void beforeIteration(ResultNode node) {
    }

    public void beforeNode(ResultNode node) {
        for(Alternative a : alternatives) {
            statisticsMap.put(a.getName(), new DescriptiveStatistics());
        }

    }
    
    private static class VarianceResult implements ISensitivityAnalysisResult {
        
        
        private static final DecimalFormat format = new DecimalFormat("#0.00");
        
        private Map<Alternative, Double> variances = new HashMap<Alternative, Double>();
        
        private Map<Alternative, Double> averages = new HashMap<Alternative, Double>();
        
        private static final double THRESHOLD = 0.3;

        public String toString() {
            return "Variance: " + format.format(getHighestVarianceCoefficient());
        }

        public boolean isSensitive() {
            return getHighestVarianceCoefficient() > THRESHOLD;
        }
        
        public void addAlternativeResult(Alternative a, double variance, double average) {
            variances.put(a, variance);
            averages.put(a, average);
        }
        
        private double getVarianceCoefficient(Alternative a) {
            double variance = variances.get(a);
            double average = averages.get(a);
            if(average != 0) {
                return Math.sqrt(variance)/average;
            } else {
                return 0;
            }
            
        }
        
        public double getHighestVarianceCoefficient() {
            double highest = 0;
            for(Alternative a : variances.keySet()) {
                double v = getVarianceCoefficient(a);
                if(v > highest) {
                    highest = v;
                }
            }
            return highest;
        }

        public double getSensitivityCoefficient() {
            return getHighestVarianceCoefficient();
        }

        public double getSensitivityThreashold() {
            return THRESHOLD;
        }

    }

}
