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
package eu.scape_project.planning.evaluation.evaluators;

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
