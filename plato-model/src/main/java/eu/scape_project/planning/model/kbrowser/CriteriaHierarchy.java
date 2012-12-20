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
package eu.scape_project.planning.model.kbrowser;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class CriteriaHierarchy implements Serializable {
    private static final long serialVersionUID = 7043155199631459302L;

    @Id
    @GeneratedValue
    private int id;

    private String name;
    
    @OneToOne(cascade = CascadeType.ALL)
    private CriteriaNode criteriaTreeRoot;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /*
    public void setScaleWeights(Boolean scaleWeights) {
        this.scaleWeights = scaleWeights;
    }

    public Boolean getScaleWeights() {
        return scaleWeights;
    }
    */

    public void setCriteriaTreeRoot(CriteriaNode criteriaTreeRoot) {
        this.criteriaTreeRoot = criteriaTreeRoot;
    }

    public CriteriaNode getCriteriaTreeRoot() {
        return criteriaTreeRoot;
    }
}
