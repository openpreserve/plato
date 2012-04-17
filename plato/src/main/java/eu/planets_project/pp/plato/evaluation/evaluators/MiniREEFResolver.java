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
package eu.planets_project.pp.plato.evaluation.evaluators;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import at.tuwien.minireef.MiniREEF;
import at.tuwien.minireef.ResultSet;

public class MiniREEFResolver implements Serializable {
    private static final long serialVersionUID = 5115476163768005323L;

    private static MiniREEFResolver reefResolver;

    private MiniREEF reef;
    

    private MiniREEFResolver() {
        reef = new MiniREEF();
        URL modelURL = Thread.currentThread().getContextClassLoader().getResource("data/p2/p2unified.rdf");
        reef.addModel(modelURL.getFile());
        reef.addReasoning();
        
    }
    public static MiniREEFResolver getInstance() {
        if (reefResolver == null ) {
            reefResolver = new MiniREEFResolver();
        }
        return reefResolver;
    }
    
    public ResultSet resolve(String statement, Map<String, String> params) {
        for (String key : params.keySet()) {
            // parameters have to be wrapped
            statement = statement.replace("$" + key + "$", params.get(key));
        }
        
        return reef.resolve(statement);
    }
    
    
    
}
