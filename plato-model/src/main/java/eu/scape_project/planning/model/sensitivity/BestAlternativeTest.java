/*******************************************************************************
 * Copyright 2012 Vienna University of Technology
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
 * 
 * This work originates from the Planets project, co-funded by the European Union under the Sixth Framework Programme.
 ******************************************************************************/
package eu.scape_project.planning.model.sensitivity;

import java.util.List;
import java.util.Vector;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.aggregators.IAggregator;
import eu.scape_project.planning.model.beans.ResultNode;
import eu.scape_project.planning.model.tree.TreeNode;

/**
 * This sensitivity analysis test compares the best alternative.
 * 
 * At start (before the modifications to the importance weights) all alternatives are evaluated to find 
 * the best. 
 * 
 * After each iteration (modifications to the weights) the alternatives are evaluated again. If the winner changes
 * than the node is considered sensitive.
 * 
 * @author Jan Zarnikov
 *
 */
public class BestAlternativeTest implements ISensitivityTest {
    
    /**
     * The list of alternatives with the best results under normal importance factors weighting.
     * This is stored as a list because there can be more "winners" - alternatives which are equaly good.
     */
    private List<Alternative> winners = new Vector<Alternative>();
    
    /**
     * A flag that is changed if winners change after an iteration.
     */
    private boolean winnerChanged = false;
    
    private TreeNode root;
    
    private IAggregator aggregator;
    
    private List<Alternative> alternatives;
    
 
    public BestAlternativeTest(TreeNode root, IAggregator aggregator, List<Alternative> alternatives) {
        this.root = root;
        this.aggregator = aggregator;
        this.alternatives = alternatives;
        // initialize the winners
        winners = getWinners();
    }
    
    private List<Alternative> getWinners() {
        List<Alternative> result = new Vector<Alternative>();
        double highestValue = 0;
        for(Alternative a : alternatives) {
            double newValue = aggregator.getAggregatedValue(root, a);
            if(newValue > highestValue) {
                result = new Vector<Alternative>();
                result.add(a);
                highestValue = newValue;
            } else if(newValue == highestValue) {
                result.add(a);
            }
        }
        return result;
    }

    public void afterIteration(ResultNode node) {
        List<Alternative> iterationWinners = getWinners();
        if(!winners.equals(iterationWinners)) {
            winnerChanged = true;
        }
        
    }

    public void afterNode(ResultNode node) {
       node.setSensitivityAnalysisResult(new BestAlternativeResult(winnerChanged));

    }

    public void beforeIteration(ResultNode node) {

    }

    public void beforeNode(ResultNode node) {
        // reset the flag when starting a new node.
        winnerChanged = false;
    }
    
    private static class BestAlternativeResult implements ISensitivityAnalysisResult {
        
        private boolean changed;

        public BestAlternativeResult(boolean changed) {
            this.changed = changed;
        }
        
        public String toString() {
            if(changed) {
                return "!";
            } else {
                return "";
            }
        }

        public boolean isSensitive() {
            return changed;
        }

        public double getSensitivityCoefficient() {
            return changed ? 1 : 0;
        }

        public double getSensitivityThreashold() {
            return 0.5;
        }

    }


}
