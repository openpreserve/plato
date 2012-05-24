package eu.planets_project.pp.plato.model.tree;
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


import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.tree.Node;
import eu.scape_project.planning.validation.ValidationError;


public class NodeTester {
    
    @Test
    public void isCompletelySpecified(){
        Node node = new Node();
        Leaf leaf = new Leaf();
        leaf.setName("Name");
        node.addChild(leaf);
        node.addChild(leaf);
        Leaf leaf2 = new Leaf();
        leaf2.setName("Name2");
        node.addChild(leaf2);
        node.addChild(leaf2);
        List<ValidationError> errors = new ArrayList<ValidationError>();
        System.out.println(node.getChildren().size());
        node.isCompletelySpecified(errors);
        System.out.println(errors.size());
        for (ValidationError error : errors) {
            System.out.println(error.getMessage());
        }
        
        assert(errors.size() == 2);
    }

}
