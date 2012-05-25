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
package eu.scape_project.planning.model;


public enum TriggerType {
    NEW_COLLECTION ("New Collection"),
    PERIODIC_REVIEW ("Periodic Review"),
    CHANGED_ENVIRONMENT ("Changed Environment"),
    CHANGED_OBJECTIVE ("Changed Objective"),
    CHANGED_COLLECTION_PROFILE ("Changed Collection Profile");
    
    /**
     * @return all possible enum values
     */
    public TriggerType[] getEnumValues() {
        return TriggerType.values();       
    }
    
    /**
     * a more human friendly name for the enum value
     */
    private String niceName;
    
    private TriggerType(String niceName) {
        this.niceName = niceName;
    }
    
    public String toString() {
        return this.niceName;
    }

    /**
     * Kept for backwards compatibility
     */
    public String getName() {
        return this.toString();
    }    
}
