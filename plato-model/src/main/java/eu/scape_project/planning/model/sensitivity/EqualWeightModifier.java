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

import eu.scape_project.planning.model.tree.TreeNode;

public class EqualWeightModifier implements IWeightModifier {

    public boolean performModification(TreeNode node) {
        for(TreeNode child : node.getChildren()) {
            child.setWeight(1);
        }
        return false;
    }

}
