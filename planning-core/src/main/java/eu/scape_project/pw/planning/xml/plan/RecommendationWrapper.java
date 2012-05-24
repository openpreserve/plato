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


package eu.scape_project.pw.planning.xml.plan;

import java.util.HashMap;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.Recommendation;

/**
 * Wrapper which provides an attribute for the name of the selected alternative - for {@link eu.scape_project.planning.xml.ProjectImporter}
 *  
 * @author Michael Kraxner
 */
public class RecommendationWrapper extends Recommendation {
    /**
     * 
     */
    private static final long serialVersionUID = 5007267951604755340L;
    private String alternativeName;
    public void setAlternativeName(String alternative) {
        this.alternativeName = alternative;
    }
    public String getAlternativeName() {
        return alternativeName;
    }
    
    public Recommendation getRecommendation(HashMap<String, Alternative> alternatives) {
        setAlternative(alternatives.get(alternativeName));
        return this;
    }

}
