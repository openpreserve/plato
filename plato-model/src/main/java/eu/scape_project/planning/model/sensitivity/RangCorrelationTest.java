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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.aggregators.IAggregator;
import eu.scape_project.planning.model.beans.ResultNode;
import eu.scape_project.planning.model.tree.TreeNode;

/**
 * This sensitivity analysis test is similar to the OrderChangeCountTest.
 * 
 * It first determines the "normal" ordering of alternatives (which is best, which is worst etc.). After each iteration
 * (after each modification to the importance factors) the order of alternatives is recalculated. The new ordering is
 * than compared with the normal ordering using the Spearman's rank correlation coefficient.
 * 
 * The correlation coefficient is 0 if the ordering hasn't changed.
 * 
 * The coefficients of all iterations for each node are added together and then devided by the number of iteration
 * (= compute the average correlation coefficient). If this value exceeds a certain threshold the node is considered
 * sensitive (the weight modification produces "too strong changes" in the ordering of the alternatives).
 * 
 * @author Jan Zarnikov
 *
 */
public class RangCorrelationTest extends OrderChangeTest {
    
    private double rangCorrelationSum = 0;
    
    private int roundCount = 0;
    
       
    public RangCorrelationTest(TreeNode root, IAggregator aggregator, List<Alternative> alternatives) {
        super(root, aggregator, alternatives);
    }

    public void afterIteration(ResultNode node) {
        roundCount++;
        SortedSet<ComparableAlternative> order = getOrder();
        if(order.size() != normalOrder.size()) {
            // huh?! how did this happen? someone has modified the alternatives list
            return;
        }
        
        rangCorrelationSum += Math.abs(getRankCorrelation(order, normalOrder));
        
    }
    
    /**
     * Compute Spearman's rank correlation coefficient
     * @param ranks1 Sorted non-empty set of ComparableAlternatives, it doesn't matter whether they are sorted 
     *          in ascending or descending order. 
     * @param ranks2 Must be a set of the same Alternatives sorted using the same Comparator as ranks1. 
     *         Of course the sorting might be different (because of the different results).
     *         
     * @return 0 if the ordering of rangs1 and rangs2 is the same. Otherwise the bigger the difference in the ordering
     *          of the alternatives the bigger the returned value.
     */
    private double getRankCorrelation(SortedSet<ComparableAlternative> ranks1, SortedSet<ComparableAlternative> ranks2) {       
        double n = ranks1.size();
        // avoid devision by 0 further bellow
        if(n == 0) {
            return 0;
        }
        
        // the following two maps should have the same key set.
        Map<ComparableAlternative, Double> rankedAlternatives1 = getRankings(ranks1);
        Map<ComparableAlternative, Double> rankedAlternatives2 = getRankings(ranks2);
        double rankDifferenceSum = 0;
        
        for(ComparableAlternative ca : rankedAlternatives1.keySet()) {
            double difference = rankedAlternatives1.get(ca) - rankedAlternatives2.get(ca);
            rankDifferenceSum += difference*difference;
        }
        double result =  ((6 * rankDifferenceSum) / (n * (n * n - 1)));

        return result;
    }
    
    /**
     * Computes the rankings of a sorted set of Alternatives.
     * 
     * Example:
     * 
     * Alternative_A = 150
     * Alternative_B = 110 
     * Alternative_C = 110
     * Alternative_D = 70
     * 
     * will result in:
     * 
     * Alternative_A -> 1
     * Alternative_B -> 2.5
     * Alternative_C -> 2.5
     * Alternative_D -> 4
     * 
     * Note that B and C share third and fourth place.
     * Works just like in sport events on TV ;-)
     * 
     * @param alternatives
     * @return
     */
    private Map<ComparableAlternative, Double> getRankings(SortedSet<ComparableAlternative> alternatives) {
        List<ComparableAlternative> buffer = new LinkedList<ComparableAlternative>();
        Map<ComparableAlternative, Double> result = new HashMap<ComparableAlternative, Double>();
        
        // the set is already sorted so this should iterate in ascending order.
        for(ComparableAlternative ca : alternatives) {
            if(buffer.isEmpty()) {
                buffer.add(ca);
            } else {
                // if we have the same value as the previous alternative then add it to the buffer
                if(ca.getValue() == buffer.get(0).getValue()) {
                    buffer.add(ca);
                } else {
                    // flush the buffer
                    for(ComparableAlternative bufferedAlternative : buffer) {
                        double rank = alternatives.size() - result.size() - (buffer.size() - 1)/2;
                        result.put(bufferedAlternative, rank);
                    }
                    
                    // and add this ca to the newly created buffer
                    buffer = new LinkedList<ComparableAlternative>();
                    buffer.add(ca);
                }
            }
        }
        
        // flush the last values from the buffer.
        for(ComparableAlternative bufferedAlternative : buffer) {
            double rank = alternatives.size() - result.size() - (buffer.size() - 1)/2;
            result.put(bufferedAlternative, rank);
        }
        
        return result;
    }

    public void afterNode(ResultNode node) {
            node.setSensitivityAnalysisResult(new RangCorrelationResult(rangCorrelationSum, roundCount));
    }


    public void beforeNode(ResultNode node) {
        rangCorrelationSum = 0;
        roundCount = 0;

    }
    
    /**
     * A simple container for storing the results of the sensitivity analysis.
     * 
     * It stores the sum of all correlation coefficients (all iterations over a node) and the number of nodes.
     * The quotient of these both is used as a result (= the average correlation coefficient).
     * 
     * @author Jan Zarnikov
     *
     */
    private static class RangCorrelationResult implements ISensitivityAnalysisResult {
        
        private double correlationSum = 0;
        
        private int rounds = 0;
        
        private static final double THRESHOLD = 0.049;
        
        public RangCorrelationResult(double correlationSum, int rounds) {
            super();
            this.correlationSum = correlationSum;
            this.rounds = rounds;
        }

        public double getSensitivityCoefficient() {
            return correlationSum / rounds;
        }

        public double getSensitivityThreashold() {
            return THRESHOLD;
        }


        public boolean isSensitive() {
            return getSensitivityCoefficient() > getSensitivityThreashold();
        }
        
    }
}