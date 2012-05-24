/*******************************************************************************
 * Copyright 2012 Vienna University of Technology
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
package eu.scape_project.planning.model.sensitivity;

/**
 * Implementations of this interface are used as results of the sensitivity analysis.
 * Every node in the result tree has a map of results (each for every alternative).
 * 
 * You should implement the toString() method to produce some user readable output.
 * 
 * The results are stored as one instance per ResultNode.
 * 
 * @author Jan Zarnikov
 *
 */
public interface ISensitivityAnalysisResult {
    
    /**
     * Is the node sensitive to changes in the weights of the importance factors.
     * @return
     */
    public boolean isSensitive();
        
    /**
     * This is a non-negative number describing the sensitivity of the node.
     * The higher the more sensitive is the weight distribution of its children
     * (even small changes to the weights will cause significat changes in the results). 
     * @return
     */
    public double getSensitivityCoefficient();
    
    /**
     * A non-negative number describing the threshold of the sensitivity 
     * (as measured by the getSensitivityCoefficient()). If the sensitivity
     * coefficient is bigger than this then isSensitive() must return true.
     * @return
     */
    public double getSensitivityThreashold();
    

}
