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

import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.random.RandomGenerator;

import eu.scape_project.planning.model.tree.TreeNode;

public class GaussianRandomModifier implements IWeightModifier {
    
    private RandomGenerator generator = new JDKRandomGenerator();
    
    private int counter = 0;
    
    public boolean performModification(TreeNode node) {
        int nodeCount = node.getChildren().size();
        for(TreeNode child : node.getChildren()) {
            double oldWeight = child.getWeight();
            double newWeight = oldWeight +  generator.nextGaussian()/(8*nodeCount);
            child.setWeight(newWeight);
        }
        counter++;
        if(counter >= 50) {
            counter = 0;
            return false;
        } else {
            return true;
        }
    }

}
