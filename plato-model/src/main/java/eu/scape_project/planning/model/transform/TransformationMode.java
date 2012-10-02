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
package eu.scape_project.planning.model.transform;

/**
 * The transformation mode determines the way numeric transformation is being calucalated.
 * {@link #THRESHOLD_STEPPING} indicates a step-approach, while 
 * {@link #LINEAR} indicates that values between steps are linearly interpolated 
 * @author Christoph Becker
 * @author Kevin Stadler (getName/toString)
 *
 */
public enum TransformationMode {
    THRESHOLD_STEPPING ("Steps"),             
    LINEAR ("Linear");

    /**
     * @return all possible enum values
     */
    public TransformationMode[] getEnumValues() {
        return TransformationMode.values();       
    }
    
    /**
     * a more human friendly name for the enum value
     */
    private String niceName;
    
    private TransformationMode(String niceName) {
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
