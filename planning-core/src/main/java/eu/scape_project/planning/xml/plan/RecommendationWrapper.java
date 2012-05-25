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
package eu.scape_project.planning.xml.plan;

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
