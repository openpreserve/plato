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
package eu.planets_project.pp.plato.model.sensitivity;

import java.util.List;
import java.util.Vector;

import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.aggregators.IAggregator;
import eu.planets_project.pp.plato.model.beans.ResultNode;
import eu.planets_project.pp.plato.model.tree.TreeNode;

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
