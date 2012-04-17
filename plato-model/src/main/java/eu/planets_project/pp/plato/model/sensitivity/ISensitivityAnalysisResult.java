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
package eu.planets_project.pp.plato.model.sensitivity;

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
