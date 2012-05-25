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

import java.io.Serializable;

public enum CriterionCategory implements Serializable {
    ACTION("action", ""), 
    OUTCOME_OBJECT("outcome", "object"),
    OUTCOME_FORMAT("outcome", "format"), 
    OUTCOME_EFFECT("outcome", "effect");
    
    
    private String category;
    private String subCategory;
    
    private CriterionCategory(String category, String subCategory) {
        this.category = category;
        this.subCategory = subCategory;
    }
    
    @Override
    public String toString() {
        if (subCategory.equals("")) {
            return category;
        }
        
        return category + ":" + subCategory;
    }
    
    public String getCategory(){
        return category;
    }
    
    public String getSubCategory() {
        return subCategory;
    }
    
    /**
     * factory method, returns the criterion category for the given category and subcategory
     * @param category
     * @param subCategory
     * @return category, or null if category and subCategory do not correspond to a criterion category 
     */
    public static CriterionCategory getType(String category, String subCategory) {
        if ("action".equals(category)) {
            return ACTION;
        } else if ("outcome".equals(category)) {
            if(OUTCOME_EFFECT.subCategory.equals(subCategory)) {
                return OUTCOME_EFFECT;
            } else if(OUTCOME_FORMAT.subCategory.equals(subCategory)) {
                return OUTCOME_FORMAT;
            } else if(OUTCOME_OBJECT.subCategory.equals(subCategory)) {
                return OUTCOME_OBJECT;
            } 
        }
        return null;
    }
}
