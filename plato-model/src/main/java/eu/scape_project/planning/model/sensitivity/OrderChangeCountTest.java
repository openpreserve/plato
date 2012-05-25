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
package eu.scape_project.planning.model.sensitivity;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.aggregators.IAggregator;
import eu.scape_project.planning.model.beans.ResultNode;
import eu.scape_project.planning.model.tree.TreeNode;

/**
 * This senstitivity analysis test is similar to the OrderChangeTest.
 * 
 * At start (with the normal importance weights) the order of the alternatives is evaluated - which 
 * alternative is currently the best, which is second best etc. This is the "normal order".
 * 
 * After each iteration the alternatives are evaluated again. Then the order of the alternatives is compared
 * to the "normal order". Afterwards the ratio between the number of all iterations and the number 
 * of iterations where the order has changed is computet (e.g. a node was processed 50 times and in 15 of these
 * iterations the order of the alternatives has chaged - compared to the "normal order").
 * 
 * If the ratio is higher than a certain threshold then the node is considered sensitive.
 *  
 * @author Jan Zarnikov
 *
 */
public class OrderChangeCountTest extends OrderChangeTest {
    
    private int orderChangeCount = 0;
    
    private int roundCount = 0;
    
       
    public OrderChangeCountTest(TreeNode root, IAggregator aggregator, List<Alternative> alternatives) {
        super(root, aggregator, alternatives);
    }

    public void afterIteration(ResultNode node) {
        roundCount++;
        SortedSet<ComparableAlternative> order = getOrder();
        // unfortunately we cannot compare order and normalOrder using equals()
        Iterator<ComparableAlternative> normalOrderIterator = normalOrder.iterator();
        if(order.size() != normalOrder.size()) {
            // huh?! how did this happen? someone has modified the alternatives list
            return;
        }
        for(ComparableAlternative ca : order) {
            if(!ca.equals(normalOrderIterator.next())) {
                orderChangeCount++;
                return;
            }
        }
        
    }

    public void afterNode(ResultNode node) {
            node.setSensitivityAnalysisResult(new OrderChangeCountResult(orderChangeCount, roundCount));
    }


    public void beforeNode(ResultNode node) {
        orderChangeCount = 0;
        roundCount = 0;

    }
    
    public String toString() {
        return "Order changed " + orderChangeCount + "/" + roundCount;
    }
    
    private  static class OrderChangeCountResult implements ISensitivityAnalysisResult {
        private double ratio = Double.NaN;
        
        /**
         * This defines a threshold from which a node is considered sensitive.
         */
        public static final double RATIO_THRESHOLD = 0.03;
        
        private static final DecimalFormat format = new DecimalFormat("#0.00");
        
        public OrderChangeCountResult(int orderChangeCount, int roundCount) {
            ratio = (double) orderChangeCount / (double) roundCount;
        }
        
        public String toString() {
            if(Double.isInfinite(ratio) || Double.isNaN(ratio)) {
                return "Change ratio: -";
            } else {
                return "Change ratio: " + format.format(ratio);
            }
        }
        
        public boolean isSensitive() {
            if(Double.isInfinite(ratio) || Double.isNaN(ratio)) {
                return false;
            } else {
                return ratio > RATIO_THRESHOLD;
            }
        }

        public double getSensitivityCoefficient() {
            return (Double.isInfinite(ratio) || Double.isNaN(ratio)) ? 0 : ratio;
        }

        public double getSensitivityThreashold() {
            return RATIO_THRESHOLD;
        }
    }

}
