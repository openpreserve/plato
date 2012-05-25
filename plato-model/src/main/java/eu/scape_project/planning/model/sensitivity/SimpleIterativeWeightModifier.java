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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import eu.scape_project.planning.model.tree.TreeNode;

/**
 * This weight modifier sees the weights as n-dimensional space.
 * 
 * E.g. if you have a node with three children A, B, C it interprets the their weights as three
 * dimensional space where the values in each dimension can be 0 <= w <= 1.
 * 
 * Each dimension (=each weight) gets values from (original_value - LIMIT) to (original_value + LIMIT)
 * in STEPS steps. Example: let's assume A has the weight 0.35, LIMIT = 0.05 and STEPS = 3. Then A will
 * get the following weights: 0.30, 0.35 and 0.40. Of course this is done to B and C as well.
 * 
 * In total there is STEPS^n combinations - the number of iterations grows exponentially with the number of nodes.
 * 
 * @author Jan Zarnikov
 *
 */
public class SimpleIterativeWeightModifier implements IWeightModifier {

    
    private int counter = 0;
    
    public static final int STEPS = 2;
    
    public static final double LIMIT = 0.05;
    
    public static final double STEP = (LIMIT * 2) / (STEPS-1);
        
    
    public boolean performModification(TreeNode node) {
        int nodeCount = node.getChildren().size();
        
        // we have NODE_COUNT-dimensional space with STEPS possible values in each dimension
        // => STEPS^number-of-nodes combinations
        long rounds = (long) Math.pow(STEPS, nodeCount);
        

        List<Long> vector = integerToBinary(counter, STEPS);
        for(int nx = vector.size();  nx < nodeCount; nx++) {
                vector.add(0, 0L);
        }
        
        for(int np = 0; np < nodeCount; np++) {
                Long x = vector.get(np);
                TreeNode child = node.getChildren().get(np);
                double oldWeight = child.getWeight();
                double newWeight = oldWeight + (x * STEP -LIMIT);
                child.setWeight(newWeight);
        }

        counter++;
        if(counter >= rounds) {
            counter = 0;
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Convert a number to a different base.
     * 
     * Example: integerToBinary(11, 2) = {1, 0, 1, 1}
     * (most significat bit first)
     *  
     * @param num
     * @param base
     * @return
     */
    private List<Long> integerToBinary(long num, int base) {
        if (num > 0) { //Check to make sure integer is positive.
            List<Long> result = integerToBinary(num / base, base);
            result.addAll(Collections.singletonList(num % base));
            return result;
        } else {
            return new LinkedList<Long>();
        }

    }
}
