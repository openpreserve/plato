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
package eu.scape_project.planning.model.measurement;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * This is based on the quality model of preservation planning attributes and measures 
 * 
 * @author Michael Kraxner
 * 
 */
@Entity
public class CriterionCategory {
    
    @Id
    @GeneratedValue
    private long id;
    
    private String name;
    
    private String uri;
    
    @Enumerated
    private EvaluationScope scope;
    
    public CriterionCategory(){
        
    }
    
    public CriterionCategory(final String uri, final String name,final EvaluationScope scope) {
        this.uri = uri;
        this.name = name;
        this.scope = scope;
    }

    public EvaluationScope getScope() {
        return scope;
    }

    public void setScope(EvaluationScope scope) {
        this.scope = scope;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
