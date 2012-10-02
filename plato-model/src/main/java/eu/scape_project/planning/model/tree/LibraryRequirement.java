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
 * 
 * This work originates from the Planets project, co-funded by the European Union under the Sixth Framework Programme.
 ******************************************************************************/
package eu.scape_project.planning.model.tree;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.measurement.CriterionCategory;

@Entity
@DiscriminatorValue("LR")
public class LibraryRequirement extends Node {
    private static final long serialVersionUID = 2407351076268518335L;

    private CriterionCategory category = null;
    
    private boolean predefined = false;


    public LibraryRequirement() {
    }
    
    public CriterionCategory getCategory() {
        return category;
    }

    public void setCategory(CriterionCategory category) {
        this.category = category;
    }

    public boolean isPredefined() {
        return predefined;
    }

    public void setPredefined(boolean predefined) {
        this.predefined = predefined;
    }
    
    public Leaf addCriterion() {
        Leaf l = new Leaf();
        Measure mInfo = l.getMeasure();
//        if ((mInfo.getScheme() == null) ||("".equals(mInfo.getScheme()))) {
//            if ((category == CriterionCategory.AJ)||
//                (category == CriterionCategory.AR)||
//                (category == CriterionCategory.AS)){
//                mInfo.setScheme("action");
//            } else {
//                mInfo.setScheme("object");
//            }
//        }
        addChild(l);
        return l;
    }
    public LibraryRequirement addRequirement(){
        LibraryRequirement r = new LibraryRequirement();
        r.setCategory(category);
        addChild(r);
        return r;
    }
}
